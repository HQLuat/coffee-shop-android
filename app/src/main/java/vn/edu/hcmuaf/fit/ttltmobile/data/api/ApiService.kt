package vn.edu.hcmuaf.fit.ttltmobile.data.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    // --- PRODUCT (Hibike) ---
    @GET("products")
    fun getProducts(): Call<List<Product>>
}