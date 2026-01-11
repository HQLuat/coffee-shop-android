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
}

data class ReviewRequestBody(
    @SerializedName("productId") val productId: Long,
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String
)