package vn.edu.hcmuaf.fit.ttltmobile.ui.order

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vn.edu.hcmuaf.fit.ttltmobile.data.repository.OrderRepository
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.OrderHistoryResponse

class OrderHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val orderRepository = OrderRepository(application)

    private val _orderHistory = MutableLiveData<List<OrderHistoryResponse>>()
    val orderHistory: LiveData<List<OrderHistoryResponse>> = _orderHistory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun loadOrderHistory() {
        _isLoading.value = true
        orderRepository.getOrderHistory(
            onSuccess = { orders ->
                _isLoading.value = false
                _orderHistory.value = orders
            },
            onError = { error ->
                _isLoading.value = false
                _errorMessage.value = error
            }
        )
    }

    fun clearMessages() {
        _errorMessage.value = ""
    }
}