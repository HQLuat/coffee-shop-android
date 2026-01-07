package vn.edu.hcmuaf.fit.ttltmobile.data.repository
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.CartApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.cart.*

class CartRepository(private val context: Context) {

    private val apiService: CartApiService = ApiConfig.getCartService(context)

    fun getCart(callback: (Result<CartResponse>) -> Unit) {
        apiService.getCart().enqueue(object : Callback<CartResponse> {
            override fun onResponse(
                call: Call<CartResponse>,
                response: Response<CartResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Không thể tải giỏ hàng")))
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun addToCart(productId: Long, quantity: Int, callback: (Result<CartResponse>) -> Unit) {
        val request = AddToCartRequest(productId, quantity)

        apiService.addToCart(request).enqueue(object : Callback<CartResponse> {
            override fun onResponse(
                call: Call<CartResponse>,
                response: Response<CartResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Không thể thêm vào giỏ hàng")))
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun updateCartItem(cartItemId: Long, quantity: Int, callback: (Result<CartResponse>) -> Unit) {
        val request = UpdateCartItemRequest(quantity)

        apiService.updateCartItem(cartItemId, request).enqueue(object : Callback<CartResponse> {
            override fun onResponse(
                call: Call<CartResponse>,
                response: Response<CartResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Không thể cập nhật giỏ hàng")))
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun removeCartItem(cartItemId: Long, callback: (Result<CartResponse>) -> Unit) {
        apiService.removeCartItem(cartItemId).enqueue(object : Callback<CartResponse> {
            override fun onResponse(
                call: Call<CartResponse>,
                response: Response<CartResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Không thể xóa sản phẩm")))
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun clearCart(callback: (Result<CartResponse>) -> Unit) {
        apiService.clearCart().enqueue(object : Callback<CartResponse> {
            override fun onResponse(
                call: Call<CartResponse>,
                response: Response<CartResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Không thể xóa giỏ hàng")))
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}