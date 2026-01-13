package vn.edu.hcmuaf.fit.ttltmobile.data.repository

import android.content.Context
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.OrderApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.*
import java.math.BigDecimal

class OrderRepository(context: Context) {

    private val orderService: OrderApiService = ApiConfig.getOrderService(context)

    // Create Order
    fun createOrder(
        request: CreateOrderRequest,
        onSuccess: (OrderResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("OrderRepository", "Creating order with ${request.items.size} items")

        orderService.createOrder(request).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("OrderRepository", "✅ Order created: ${it.id}")
                        onSuccess(it)
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Token hết hạn hoặc không hợp lệ (401)"
                        403 -> "Không có quyền truy cập (403)"
                        400 -> {
                            try {
                                val errorBody = response.errorBody()?.string()
                                Log.e("OrderRepository", "Error body: $errorBody")
                                "Dữ liệu không hợp lệ: $errorBody"
                            } catch (e: Exception) {
                                "Dữ liệu không hợp lệ (400)"
                            }
                        }
                        500 -> "Lỗi server (500)"
                        else -> "Lỗi không xác định (${response.code()})"
                    }

                    Log.e("OrderRepository", "❌ Create order failed: $errorMsg")
                    onError(errorMsg)
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                val errorMsg = "Lỗi kết nối: ${t.message}"
                Log.e("OrderRepository", "❌ $errorMsg", t)
                onError(errorMsg)
            }
        })
    }

    // Get Order Detail
    fun getOrder(
        orderId: Long,
        onSuccess: (OrderResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        orderService.getOrder(orderId).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Không thể tải đơn hàng")
                    Log.e("OrderRepository", "Get order failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                onError("Lỗi kết nối: ${t.message}")
                Log.e("OrderRepository", "Get order error", t)
            }
        })
    }

    // Get Order History
    fun getOrderHistory(
        onSuccess: (List<OrderHistoryResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        orderService.getOrderHistory().enqueue(object : Callback<List<OrderHistoryResponse>> {
            override fun onResponse(
                call: Call<List<OrderHistoryResponse>>,
                response: Response<List<OrderHistoryResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) } ?: onSuccess(emptyList())
                } else {
                    onError("Không thể tải lịch sử đơn hàng")
                    Log.e("OrderRepository", "Get history failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<OrderHistoryResponse>>, t: Throwable) {
                onError("Lỗi kết nối: ${t.message}")
                Log.e("OrderRepository", "Get history error", t)
            }
        })
    }

    // Create ZaloPay Payment
    fun createZaloPayPayment(
        orderId: Long,
        onSuccess: (ZaloPayResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        orderService.createZaloPayPayment(orderId).enqueue(object : Callback<ZaloPayResponse> {
            override fun onResponse(call: Call<ZaloPayResponse>, response: Response<ZaloPayResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { zpResponse ->
                        Log.d("OrderRepository", "ZaloPay response: ${zpResponse.orderUrl}")
                        onSuccess(zpResponse)
                    }
                } else {
                    val errorMsg = "Tạo thanh toán thất bại (${response.code()})"
                    Log.e("OrderRepository", errorMsg)
                    onError(errorMsg)
                }
            }

            override fun onFailure(call: Call<ZaloPayResponse>, t: Throwable) {
                onError("Lỗi kết nối ZaloPay: ${t.message}")
                Log.e("OrderRepository", "Create payment error", t)
            }
        })
    }

    // Query Payment Status
    fun queryPaymentStatus(
        appTransId: String,
        onSuccess: (Map<String, Any>) -> Unit,
        onError: (String) -> Unit
    ) {
        orderService.queryPaymentStatus(appTransId).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Không thể kiểm tra trạng thái thanh toán")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                onError("Lỗi kết nối: ${t.message}")
                Log.e("OrderRepository", "Query payment error", t)
            }
        })
    }

    // Verify and Update Order
    fun verifyAndUpdateOrder(
        orderId: Long,
        onSuccess: (Map<String, Any>) -> Unit,
        onError: (String) -> Unit
    ) {
        orderService.verifyAndUpdateOrder(orderId).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Không thể xác minh thanh toán")
                    Log.e("OrderRepository", "Verify payment failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                onError("Lỗi kết nối: ${t.message}")
                Log.e("OrderRepository", "Verify payment error", t)
            }
        })
    }

    // Cancel Order
    fun cancelOrder(
        orderId: Long,
        onSuccess: (OrderResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        orderService.cancelOrder(orderId).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Không thể hủy đơn hàng")
                    Log.e("OrderRepository", "Cancel order failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                onError("Lỗi kết nối: ${t.message}")
                Log.e("OrderRepository", "Cancel order error", t)
            }
        })
    }

    // Refund Order
    fun refundOrder(
        orderId: Long,
        amount: BigDecimal,
        description: String,
        onSuccess: (RefundResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = RefundRequest(orderId, amount, description)

        orderService.refundOrder(orderId, request).enqueue(object : Callback<RefundResponse> {
            override fun onResponse(call: Call<RefundResponse>, response: Response<RefundResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Không thể hoàn tiền")
                    Log.e("OrderRepository", "Refund failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RefundResponse>, t: Throwable) {
                onError("Lỗi kết nối: ${t.message}")
                Log.e("OrderRepository", "Refund error", t)
            }
        })
    }

    // Query Refund Status
    fun queryRefundStatus(
        refundId: String,
        onSuccess: (Map<String, Any>) -> Unit,
        onError: (String) -> Unit
    ) {
        orderService.queryRefundStatus(refundId).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Không thể kiểm tra trạng thái hoàn tiền")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                onError("Lỗi kết nối: ${t.message}")
                Log.e("OrderRepository", "Query refund error", t)
            }
        })
    }

    // Get Refund History - FIXED: Dùng RefundHistoryResponse
    fun getRefundHistory(
        orderId: Long,
        onSuccess: (List<RefundHistoryResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        orderService.getRefundHistory(orderId).enqueue(object : Callback<List<RefundHistoryResponse>> {
            override fun onResponse(
                call: Call<List<RefundHistoryResponse>>,
                response: Response<List<RefundHistoryResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) } ?: onSuccess(emptyList())
                } else {
                    onError("Không thể tải lịch sử hoàn tiền")
                }
            }

            override fun onFailure(call: Call<List<RefundHistoryResponse>>, t: Throwable) {
                onError("Lỗi kết nối: ${t.message}")
                Log.e("OrderRepository", "Get refund history error", t)
            }
        })
    }

    // Reorder
    fun reorder(
        orderId: Long,
        onSuccess: (OrderResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        orderService.reorder(orderId).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Không thể đặt lại đơn hàng")
                    Log.e("OrderRepository", "Reorder failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                onError("Lỗi kết nối: ${t.message}")
                Log.e("OrderRepository", "Reorder error", t)
            }
        })
    }

    // Get Payment Info (Debug)
    fun getOrderPaymentInfo(
        orderId: Long,
        onSuccess: (Map<String, Any>) -> Unit,
        onError: (String) -> Unit
    ) {
        orderService.getOrderPaymentInfo(orderId).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Không thể lấy thông tin thanh toán")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                onError("Lỗi kết nối: ${t.message}")
                Log.e("OrderRepository", "Get payment info error", t)
            }
        })
    }
}