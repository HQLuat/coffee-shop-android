package vn.edu.hcmuaf.fit.ttltmobile.api

import retrofit2.Call
import retrofit2.http.GET
import vn.edu.hcmuaf.fit.ttltmobile.model.Product

interface ApiService {
    @GET("products")
    fun getProducts(): Call<List<Product>>
}