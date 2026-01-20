package vn.edu.hcmuaf.fit.ttltmobile.ui.checkout

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vn.edu.hcmuaf.fit.ttltmobile.data.repository.OrderRepository
import vn.edu.hcmuaf.fit.ttltmobile.data.model.cart.CartResponse
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.*
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    private val orderRepository = OrderRepository(application)
    private val cartService = ApiConfig.getCartService(application)
    private val tokenManager = TokenManager(application)

    private val _cartData = MutableLiveData<CartResponse>()
    val cartData: LiveData<CartResponse> = _cartData

    private val _orderCreated = MutableLiveData<OrderResponse>()
    val orderCreated: LiveData<OrderResponse> = _orderCreated

    private val _zaloPayResponse = MutableLiveData<ZaloPayResponse>()
    val zaloPayResponse: LiveData<ZaloPayResponse> = _zaloPayResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _tokenExpired = MutableLiveData<Boolean>()
    val tokenExpired: LiveData<Boolean> = _tokenExpired

    val subtotal: LiveData<BigDecimal> = MutableLiveData()
    val tax: LiveData<BigDecimal> = MutableLiveData()
    val delivery: LiveData<BigDecimal> = MutableLiveData(BigDecimal(15000))
    val total: LiveData<BigDecimal> = MutableLiveData()

    private val _paymentVerified = MutableLiveData<Boolean>()
    val paymentVerified: LiveData<Boolean> = _paymentVerified

    init {
        loadCart()
    }

    fun verifyPayment(orderId: Long) {
        Log.d("CheckoutViewModel", "Verifying payment for order $orderId")

        _isLoading.value = true
        orderRepository.verifyAndUpdateOrder(
            orderId = orderId,
            onSuccess = { result ->
                _isLoading.value = false

                Log.d("CheckoutViewModel", "Full response: $result")

                val message = result["message"] as? String

                val paymentStatus = result["paymentStatus"] as? Map<*, *>
                val returnCode = paymentStatus?.get("return_code") as? Double
                val returnCodeInt = returnCode?.toInt()

                Log.d("CheckoutViewModel", "Message: $message")
                Log.d("CheckoutViewModel", "Return code: $returnCodeInt (type: ${returnCode?.javaClass?.simpleName})")
                Log.d("CheckoutViewModel", "Payment status: $paymentStatus")

                when (returnCodeInt) {
                    1 -> {
                        _successMessage.value = message ?: "Thanh toán thành công!"
                        _paymentVerified.value = true
                    }
                    2 -> {
                        Log.e("CheckoutViewModel", "Payment FAILED")
                        _errorMessage.value = "Thanh toán thất bại"
                        _paymentVerified.value = false
                    }
                    3 -> {
                        Log.d("CheckoutViewModel", "Payment still PROCESSING")
                        _errorMessage.value = "Đơn hàng đang được xử lý"
                        _paymentVerified.value = false
                    }
                    else -> {
                        Log.e("CheckoutViewModel", "Unknown payment status: $returnCodeInt")
                        _errorMessage.value = "Trạng thái thanh toán không xác định"
                        _paymentVerified.value = false
                    }
                }
            },
            onError = { error ->
                _isLoading.value = false
                Log.e("CheckoutViewModel", "Verify payment error: $error")
                _errorMessage.value = "Không thể kiểm tra trạng thái thanh toán: $error"
            }
        )
    }

    fun loadCart() {
        // Kiểm tra token
        if (!isTokenValid()) {
            Log.e("CheckoutViewModel", "Token is null or expired")
            _tokenExpired.value = true
            return
        }

        _isLoading.value = true
        cartService.getCart().enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let { cart ->
                        _cartData.value = cart
                        calculateTotals(cart.totalAmount)
                    }
                } else if (response.code() == 401 || response.code() == 403) {
                    Log.e("CheckoutViewModel", "Token expired when loading cart")
                    _tokenExpired.value = true
                } else {
                    _errorMessage.value = "Không thể tải giỏ hàng"
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Lỗi kết nối: ${t.message}"
                Log.e("CheckoutViewModel", "Load cart failed", t)
            }
        })
    }

    private fun calculateTotals(subtotalAmount: BigDecimal) {
        (subtotal as MutableLiveData).value = subtotalAmount
        val taxAmount = subtotalAmount.multiply(BigDecimal("0.02"))
        (tax as MutableLiveData).value = taxAmount
        val totalAmount = subtotalAmount.add(taxAmount).add(delivery.value ?: BigDecimal.ZERO)
        (total as MutableLiveData).value = totalAmount
    }

    fun createOrder(
        address: String,
        phone: String,
        note: String?,
        paymentMethod: String
    ) {
        if (address.isBlank()) {
            _errorMessage.value = "Vui lòng nhập địa chỉ giao hàng"
            return
        }

        if (phone.isBlank()) {
            _errorMessage.value = "Vui lòng nhập số điện thoại"
            return
        }

        if (!isValidPhoneNumber(phone)) {
            _errorMessage.value = "Số điện thoại không hợp lệ"
            return
        }

        // Kiểm tra cart data
        val cart = _cartData.value
        if (cart == null || cart.items.isEmpty()) {
            _errorMessage.value = "Giỏ hàng trống"
            return
        }

        // Kiểm tra token
        if (!isTokenValid()) {
            Log.e("CheckoutViewModel", "Token expired before creating order")
            _errorMessage.value = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
            _tokenExpired.value = true
            return
        }

        Log.d("CheckoutViewModel", "Creating order with ${cart.items.size} items")
        Log.d("CheckoutViewModel", "Payment method: $paymentMethod")

        _isLoading.value = true

        val cartItems = cart.items.map { item ->
            CartItemRequest(
                productId = item.productId,
                productName = item.productName,
                price = item.price,
                quantity = item.quantity
            )
        }

        val paymentMethodEnum = when (paymentMethod.uppercase()) {
            "ZALOPAY", "ZALO_PAY" -> PaymentMethod.ZALO_PAY
            "COD" -> PaymentMethod.COD
            "BANKING", "BANK_TRANSFER" -> PaymentMethod.BANK_TRANSFER
            else -> PaymentMethod.ZALO_PAY  // Default
        }

        val request = CreateOrderRequest(
            items = cartItems,
            paymentMethod = paymentMethodEnum,
            deliveryAddress = address,
            phoneNumber = phone,
            note = note
        )

        Log.d("CheckoutViewModel", "Request: items=${cartItems.size}, method=$paymentMethodEnum")

        orderRepository.createOrder(
            request = request,
            onSuccess = { order ->
                _isLoading.value = false
                Log.d("CheckoutViewModel", "Order created: ${order.id}")
                _orderCreated.value = order
                _successMessage.value = "Đặt hàng thành công!"

                if (paymentMethodEnum == PaymentMethod.ZALO_PAY) {
                    createZaloPayPayment(order.id)
                }
            },
            onError = { error ->
                _isLoading.value = false
                Log.e("CheckoutViewModel", "Create order failed: $error")
                _errorMessage.value = "Đặt hàng thất bại: $error"
            }
        )
    }

    fun createZaloPayPayment(orderId: Long) {
        if (!isTokenValid()) {
            _tokenExpired.value = true
            return
        }

        _isLoading.value = true
        orderRepository.createZaloPayPayment(
            orderId = orderId,
            onSuccess = { zpResponse ->
                _isLoading.value = false
                Log.d("CheckoutViewModel", "ZaloPay URL: ${zpResponse.orderUrl}")
                _zaloPayResponse.value = zpResponse
            },
            onError = { error ->
                _isLoading.value = false
                Log.e("CheckoutViewModel", "ZaloPay error: $error")

                if (error.contains("401") || error.contains("403")) {
                    _tokenExpired.value = true
                } else {
                    _errorMessage.value = error
                }
            }
        )
    }

    private fun isTokenValid(): Boolean {
        val token = tokenManager.getAccessToken()
        val isValid = !token.isNullOrEmpty()

        Log.d("CheckoutViewModel", "Token valid: $isValid, length: ${token?.length ?: 0}")

        return isValid
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        val phonePattern = "^(\\+84|0)[0-9]{9,10}$"
        return phone.matches(phonePattern.toRegex())
    }

    fun clearMessages() {
        _errorMessage.value = ""
        _successMessage.value = ""
    }
}