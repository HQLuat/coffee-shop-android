package vn.edu.hcmuaf.fit.ttltmobile.ui.admin.orders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vn.edu.hcmuaf.fit.ttltmobile.data.repository.AdminOrderRepository
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.*

class AdminOrderViewModel(application: Application) : AndroidViewModel(application) {

    private val adminOrderRepository = AdminOrderRepository(application)

    private val _orders = MutableLiveData<List<OrderResponse>>()
    val orders: LiveData<List<OrderResponse>> = _orders

    private val _statistics = MutableLiveData<OrderStatisticsResponse>()
    val statistics: LiveData<OrderStatisticsResponse> = _statistics

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private var currentStatus: String? = null
    private var currentKeyword: String? = null

    init {
        loadStatistics()
        loadAllOrders()
    }

    fun loadAllOrders() {
        _isLoading.value = true
        currentStatus = null
        currentKeyword = null

        adminOrderRepository.getAllOrders(
            page = 0,
            size = 100,
            onSuccess = { pageResponse ->
                _isLoading.value = false
                _orders.value = pageResponse.content
            },
            onError = { error ->
                _isLoading.value = false
                _errorMessage.value = error
            }
        )
    }

    fun loadOrdersByStatus(status: OrderStatus?) {
        if (status == null) {
            loadAllOrders()
            return
        }

        _isLoading.value = true
        currentStatus = status.name
        currentKeyword = null

        adminOrderRepository.getOrdersByStatus(
            status = status.name,
            onSuccess = { orderList ->
                _isLoading.value = false
                _orders.value = orderList
            },
            onError = { error ->
                _isLoading.value = false
                _errorMessage.value = error
            }
        )
    }

    fun searchOrders(keyword: String) {
        if (keyword.isBlank()) {
            loadAllOrders()
            return
        }

        _isLoading.value = true
        currentKeyword = keyword
        currentStatus = null

        adminOrderRepository.searchOrders(
            keyword = keyword,
            onSuccess = { orderList ->
                _isLoading.value = false
                _orders.value = orderList
            },
            onError = { error ->
                _isLoading.value = false
                _errorMessage.value = error
            }
        )
    }

    fun loadStatistics(startDate: String? = null, endDate: String? = null) {
        adminOrderRepository.getStatistics(
            startDate = startDate,
            endDate = endDate,
            onSuccess = { stats ->
                _statistics.value = stats
            },
            onError = { error ->
                // Don't show error for statistics
            }
        )
    }

    fun updateOrderStatus(orderId: Long, newStatus: OrderStatus) {
        _isLoading.value = true
        adminOrderRepository.updateOrderStatus(
            orderId = orderId,
            status = newStatus,
            onSuccess = { updatedOrder ->
                _isLoading.value = false
                _successMessage.value = "Đã cập nhật trạng thái đơn hàng"
                refreshCurrentView()
            },
            onError = { error ->
                _isLoading.value = false
                _errorMessage.value = error
            }
        )
    }

    fun deleteOrder(orderId: Long) {
        _isLoading.value = true
        adminOrderRepository.deleteOrder(
            orderId = orderId,
            onSuccess = { message ->
                _isLoading.value = false
                _successMessage.value = message
                refreshCurrentView()
            },
            onError = { error ->
                _isLoading.value = false
                _errorMessage.value = error
            }
        )
    }

    private fun refreshCurrentView() {
        when {
            currentKeyword != null -> searchOrders(currentKeyword!!)
            currentStatus != null -> loadOrdersByStatus(OrderStatus.valueOf(currentStatus!!))
            else -> loadAllOrders()
        }
    }

    fun refresh() {
        loadStatistics()
        refreshCurrentView()
    }

    fun clearMessages() {
        _errorMessage.value = ""
        _successMessage.value = ""
    }
}