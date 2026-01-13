package vn.edu.hcmuaf.fit.ttltmobile.ui.order

import android.app.Application
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vn.edu.hcmuaf.fit.ttltmobile.data.repository.OrderRepository
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.*
import java.math.BigDecimal
import android.os.Handler

class OrderDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val orderRepository = OrderRepository(application)
    private val pollingHandler = Handler(Looper.getMainLooper())
    private var pollingRunnable: Runnable? = null
    private var isPolling = false

    private val _orderDetail = MutableLiveData<OrderResponse>()
    val orderDetail: LiveData<OrderResponse> = _orderDetail

    private val _refundHistory = MutableLiveData<List<RefundHistoryResponse>>()
    val refundHistory: LiveData<List<RefundHistoryResponse>> = _refundHistory

    private val _paymentStatus = MutableLiveData<Map<String, Any>>()
    val paymentStatus: LiveData<Map<String, Any>> = _paymentStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    companion object {
        private const val POLLING_INTERVAL = 30000L
        private const val MAX_POLLING_ATTEMPTS = 20
    }
    private var pollingAttempts = 0


    fun loadOrderDetail(orderId: Long) {
        _isLoading.value = true
        orderRepository.getOrder(
            orderId = orderId,
            onSuccess = { order ->
                _isLoading.value = false
                _orderDetail.value = order

                // Load refund history nếu order đã thanh toán
                if (order.isPaid()) {
                    loadRefundHistory(orderId)
                }
            },
            onError = { error ->
                _isLoading.value = false
                _errorMessage.value = error
            }
        )
    }

    fun loadRefundHistory(orderId: Long) {
        orderRepository.getRefundHistory(
            orderId = orderId,
            onSuccess = { refunds ->
                if (refunds.isNotEmpty()) {
                    _refundHistory.value = refunds

                    val hasProcessingRefund = refunds.any {
                        it.status == RefundStatus.REFUND_PROCESSING ||
                                it.status == RefundStatus.REFUND_PENDING
                    }

                    if (hasProcessingRefund) {
                        startPollingRefundStatus(orderId)
                    } else {
                        stopPolling()
                    }
                }
            },
            onError = { error ->
                // Don't show error for refund history
                Log.e("OrderDetailViewModel", "Load refund history error: $error")
            }
        )
    }

    private fun startPollingRefundStatus(orderId: Long) {
        if (isPolling) {
            Log.d("OrderDetailViewModel", "Already polling")
            return
        }

        isPolling = true
        pollingAttempts = 0

        Log.d("OrderDetailViewModel", "Start polling refund status for order $orderId")

        pollingRunnable = object : Runnable {
            override fun run() {
                if (pollingAttempts >= MAX_POLLING_ATTEMPTS) {
                    Log.d("OrderDetailViewModel", "Max polling attempts reached")
                    stopPolling()
                    return
                }

                pollingAttempts++
                Log.d("OrderDetailViewModel", "Polling attempt $pollingAttempts/$MAX_POLLING_ATTEMPTS")

                // Reload refund history (không hiện loading indicator)
                orderRepository.getRefundHistory(
                    orderId = orderId,
                    onSuccess = { refunds ->
                        _refundHistory.value = refunds

                        // Kiểm tra còn refund PROCESSING không
                        val hasProcessingRefund = refunds.any {
                            it.status == RefundStatus.REFUND_PROCESSING ||
                                    it.status == RefundStatus.REFUND_PENDING
                        }

                        if (!hasProcessingRefund) {
                            // Tất cả refund đã hoàn tất → stop polling
                            Log.d("OrderDetailViewModel", "All refunds completed, stop polling")
                            stopPolling()

                            // Reload order để cập nhật status
                            loadOrderDetail(orderId)
                        } else {
                            // Vẫn còn PROCESSING → tiếp tục poll
                            pollingHandler.postDelayed(this, POLLING_INTERVAL)
                        }
                    },
                    onError = { error ->
                        Log.e("OrderDetailViewModel", "Polling error: $error")
                        // Vẫn retry
                        pollingHandler.postDelayed(this, POLLING_INTERVAL)
                    }
                )
            }
        }

        pollingHandler.post(pollingRunnable!!)
    }

    fun stopPolling() {
        if (isPolling) {
            Log.d("OrderDetailViewModel", "Stop polling")
            isPolling = false
            pollingRunnable?.let { pollingHandler.removeCallbacks(it) }
            pollingRunnable = null
        }
    }

    fun verifyAndUpdatePayment(orderId: Long) {
        _isLoading.value = true
        orderRepository.verifyAndUpdateOrder(
            orderId = orderId,
            onSuccess = { result ->
                _isLoading.value = false
                _paymentStatus.value = result
                val message = result["message"] as? String
                _successMessage.value = message ?: "Đã cập nhật trạng thái thanh toán"
                loadOrderDetail(orderId)
            },
            onError = { error ->
                _isLoading.value = false
                _errorMessage.value = error
            }
        )
    }

    fun cancelOrder(orderId: Long) {
        _isLoading.value = true
        orderRepository.cancelOrder(
            orderId = orderId,
            onSuccess = { order ->
                _isLoading.value = false
                _orderDetail.value = order
                _successMessage.value = "Đã hủy đơn hàng thành công"
            },
            onError = { error ->
                _isLoading.value = false
                _errorMessage.value = error
            }
        )
    }

    fun refundOrder(orderId: Long, amount: BigDecimal, description: String) {
        _isLoading.value = true
        orderRepository.refundOrder(
            orderId = orderId,
            amount = amount,
            description = description,
            onSuccess = { refund ->
                _isLoading.value = false

                Log.d("OrderDetailViewModel", "Refund response: $refund")

                _successMessage.value = "Đã gửi yêu cầu hoàn tiền. Mã: ${refund.refundId}"

                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    loadOrderDetail(orderId)
                }, 1000)
            },
            onError = { error ->
                _isLoading.value = false
                Log.e("OrderDetailViewModel", "Refund error: $error")
                _errorMessage.value = error
            }
        )
    }

    fun createZaloPayPayment(orderId: Long) {
        _isLoading.value = true
        orderRepository.createZaloPayPayment(
            orderId = orderId,
            onSuccess = { zpResponse ->
                _isLoading.value = false
                _successMessage.value = "ZALOPAY_URL:${zpResponse.orderUrl}"
            },
            onError = { error ->
                _isLoading.value = false
                _errorMessage.value = error
            }
        )
    }

    fun clearMessages() {
        _errorMessage.value = ""
        _successMessage.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }

}