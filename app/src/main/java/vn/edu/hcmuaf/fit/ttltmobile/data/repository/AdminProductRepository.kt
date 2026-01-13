package vn.edu.hcmuaf.fit.ttltmobile.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.Product

class AdminProductRepository(private val context: Context) {
    private val adminApi = ApiConfig.getAdminProductService(context)

    fun getAllProducts(): LiveData<List<Product>?> {
        val result = MutableLiveData<List<Product>?>()
        adminApi.getAllProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                result.value = if (response.isSuccessful) response.body() else null
            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                result.value = null
            }
        })
        return result
    }

    fun getProductById(id: Long): LiveData<Product?> {
        val data = MutableLiveData<Product?>()
        adminApi.getProductById(id).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    data.value = response.body()
                } else {
                    data.value = null
                }
            }
            override fun onFailure(call: Call<Product>, t: Throwable) {
                data.value = null
            }
        })
        return data
    }

    fun saveProductMultipart(
        id: Long?,
        productPart: RequestBody,
        filePart: MultipartBody.Part?,
        isEdit: Boolean
    ): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        val call = if (isEdit && id != null) {
            adminApi.updateProduct(id, productPart, filePart)
        } else {
            adminApi.createProduct(productPart, filePart!!)
        }

        call.enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                result.value = response.isSuccessful
            }
            override fun onFailure(call: Call<Product>, t: Throwable) {
                result.value = false
            }
        })
        return result
    }

    fun deleteProduct(id: Long): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        adminApi.deleteProduct(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                result.value = response.isSuccessful
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                result.value = false
            }
        })
        return result
    }
}