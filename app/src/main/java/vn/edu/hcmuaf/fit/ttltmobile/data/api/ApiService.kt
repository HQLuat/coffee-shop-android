package vn.edu.hcmuaf.fit.ttltmobile.data.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import vn.edu.hcmuaf.fit.ttltmobile.data.model.LoginRequest
import vn.edu.hcmuaf.fit.ttltmobile.data.model.Product
import vn.edu.hcmuaf.fit.ttltmobile.data.model.User

interface ApiService {
    // --- AUTH (Ezchiel) ---
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<User>

    // --- PRODUCT (Hibike) ---
    @GET("products")
    fun getProducts(): Call<List<Product>>
}