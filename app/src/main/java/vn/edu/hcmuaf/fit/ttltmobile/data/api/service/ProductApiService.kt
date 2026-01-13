package vn.edu.hcmuaf.fit.ttltmobile.data.api.service

import retrofit2.Call
import retrofit2.http.*
import vn.edu.hcmuaf.fit.ttltmobile.data.model.ReviewModel
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.Product
import com.google.gson.annotations.SerializedName

interface ProductApiService {

    @GET("products")
    fun getProducts(): Call<List<Product>>

    @GET("reviews")
    fun getReviewsByProduct(@Query("productId") productId: Long): Call<List<ReviewModel>>

    @POST("reviews")
    fun postReview(@Body review: ReviewRequestBody): Call<ReviewModel>

    @PUT("reviews/{reviewId}")
    fun updateReview(
        @Path("reviewId") reviewId: Long,
        @Body review: ReviewRequestBody
    ): Call<ReviewModel>

    @DELETE("reviews/{reviewId}")
    fun deleteReview(@Path("reviewId") reviewId: Long): Call<Void>

    // SỬA ENDPOINT ADD TO CART
    @POST("cart/add")
    fun addToCart(@Body request: AddToCartRequestBody): Call<CartResponseBody>
}

// DATA CLASS CHO REVIEW
data class ReviewRequestBody(
    @SerializedName("productId") val productId: Long,
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String
)

// DATA CLASS CHO ADD TO CART
data class AddToCartRequestBody(
    @SerializedName("productId") val productId: Long,
    @SerializedName("quantity") val quantity: Int
)

// DATA CLASS CHO RESPONSE
data class CartResponseBody(
    @SerializedName("message") val message: String?,
    @SerializedName("id") val id: Long?,
    @SerializedName("totalAmount") val totalAmount: Double?,
    @SerializedName("totalItems") val totalItems: Int?
)