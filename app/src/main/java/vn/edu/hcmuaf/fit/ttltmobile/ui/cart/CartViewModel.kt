package vn.edu.hcmuaf.fit.ttltmobile.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vn.edu.hcmuaf.fit.ttltmobile.data.model.cart.CartResponse
import vn.edu.hcmuaf.fit.ttltmobile.data.repository.CartRepository

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CartRepository(application)

    private val _cartData = MutableLiveData<CartResponse>()
    val cartData: LiveData<CartResponse> = _cartData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    fun loadCart() {
        _isLoading.value = true
        repository.getCart { result ->
            _isLoading.value = false
            result.onSuccess { cart ->
                _cartData.value = cart
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Lỗi khi tải giỏ hàng"
            }
        }
    }

    fun addToCart(productId: Long, quantity: Int) {
        _isLoading.value = true
        repository.addToCart(productId, quantity) { result ->
            _isLoading.value = false
            result.onSuccess { cart ->
                _cartData.value = cart
                _successMessage.value = cart.message ?: "Đã thêm vào giỏ hàng"
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Không thể thêm vào giỏ hàng"
            }
        }
    }

    fun increaseQuantity(cartItemId: Long, currentQuantity: Int) {
        updateQuantity(cartItemId, currentQuantity + 1)
    }

    fun decreaseQuantity(cartItemId: Long, currentQuantity: Int) {
        if (currentQuantity > 1) {
            updateQuantity(cartItemId, currentQuantity - 1)
        }
    }

    private fun updateQuantity(cartItemId: Long, newQuantity: Int) {
        _isLoading.value = true
        repository.updateCartItem(cartItemId, newQuantity) { result ->
            _isLoading.value = false
            result.onSuccess { cart ->
                _cartData.value = cart
                _successMessage.value = cart.message ?: "Đã cập nhật"
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Không thể cập nhật"
            }
        }
    }

    fun removeItem(cartItemId: Long) {
        _isLoading.value = true
        repository.removeCartItem(cartItemId) { result ->
            _isLoading.value = false
            result.onSuccess { cart ->
                _cartData.value = cart
                _successMessage.value = cart.message ?: "Đã xóa sản phẩm"
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Không thể xóa sản phẩm"
            }
        }
    }

    fun clearCart() {
        _isLoading.value = true
        repository.clearCart { result ->
            _isLoading.value = false
            result.onSuccess { cart ->
                _cartData.value = cart
                _successMessage.value = cart.message ?: "Đã xóa giỏ hàng"
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Không thể xóa giỏ hàng"
            }
        }
    }
}