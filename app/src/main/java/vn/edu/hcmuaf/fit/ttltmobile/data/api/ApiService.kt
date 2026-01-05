package vn.edu.hcmuaf.fit.ttltmobile.data.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import vn.edu.hcmuaf.fit.ttltmobile.data.model.auth.*
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.Product

interface ApiService {
    // --- AUTH (Ezchiel) ---
    @POST("users")
    fun register(@Body registerRequest: RegisterRequest): Call<User>

    @POST("users/login")
    fun login(@Body loginRequest: LoginRequest): Call<User>

    @POST("users/logout")
    fun logout(@Body logoutRequest: LogoutRequest): Call<LogoutResponse>

    @POST("users/resend-verification")
    fun resendVerification(@Body body: Map<String, String>): Call<Map<String, String>>

    @POST("users/forgot-password")
    fun forgotPassword(@Body body: Map<String, String>): Call<Map<String, String>>

    @GET("users/profile")
    fun getProfile(): Call<UserProfile>

    @PUT("users/profile")
    fun updateProfile(@Body updateUserProfileRequest: UpdateUserProfileRequest): Call<UserProfile>

    // --- PRODUCT (Hibike) ---
    @GET("products")
    fun getProducts(): Call<List<Product>>
}