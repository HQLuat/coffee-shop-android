package vn.edu.hcmuaf.fit.ttltmobile.data.api.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.Product

interface AdminProductApiService {
    @GET("products")
    fun getAllProducts(): Call<List<Product>>

    @GET("products/{id}")
    fun getProductById(@Path("id") id: Long): Call<Product>

    @Multipart
    @POST("products")
    fun createProduct(
        @Part("product") product: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<Product>

    @Multipart
    @PUT("products/{id}")
    fun updateProduct(
        @Path("id") id: Long,
        @Part("product") product: RequestBody,
        @Part file: MultipartBody.Part? = null
    ): Call<Product>

    @DELETE("products/{id}")
    fun deleteProduct(@Path("id") id: Long): Call<Void>
}