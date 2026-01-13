package vn.edu.hcmuaf.fit.ttltmobile.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.Product

class MainRepository(private val context: Context) {
    private val apiService = ApiConfig.createService(ApiService::class.java, context)

    fun loadPopular(): LiveData<MutableList<Product>> {
        val listData = MutableLiveData<MutableList<Product>>()
        apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val filtered = response.body()?.filter { it.rating >= 4.5 }?.toMutableList()
                    listData.value = filtered ?: mutableListOf()
                }
            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                listData.value = mutableListOf()
            }
        })
        return listData
    }
}