package vn.edu.hcmuaf.fit.ttltmobile.data.api.service
import retrofit2.Call
import retrofit2.http.*
import vn.edu.hcmuaf.fit.ttltmobile.data.model.cart.AddToCartRequest
import vn.edu.hcmuaf.fit.ttltmobile.data.model.cart.CartResponse
import vn.edu.hcmuaf.fit.ttltmobile.data.model.cart.UpdateCartItemRequest

interface CartApiService {

    @GET("cart")
    fun getCart(): Call<CartResponse>

    @POST("cart/add")
    fun addToCart(@Body request: AddToCartRequest): Call<CartResponse>

    @PUT("cart/items/{cartItemId}")
    fun updateCartItem(
        @Path("cartItemId") cartItemId: Long,
        @Body request: UpdateCartItemRequest
    ): Call<CartResponse>

    @DELETE("cart/items/{cartItemId}")
    fun removeCartItem(@Path("cartItemId") cartItemId: Long): Call<CartResponse>

    @DELETE("cart/clear")
    fun clearCart(): Call<CartResponse>
}