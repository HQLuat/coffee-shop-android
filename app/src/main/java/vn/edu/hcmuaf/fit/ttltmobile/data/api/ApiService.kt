package vn.edu.hcmuaf.fit.ttltmobile.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import vn.edu.hcmuaf.fit.ttltmobile.data.model.Product
import vn.edu.hcmuaf.fit.ttltmobile.data.model.User

interface ApiService {
    // --- AUTH (Ez chiel) ---
    @POST("login")
    fun login(): Call<User>

    // --- PRODUCT (Hi bike) ---
    @GET("products")
    fun getProducts(): Call<List<Product>>
}