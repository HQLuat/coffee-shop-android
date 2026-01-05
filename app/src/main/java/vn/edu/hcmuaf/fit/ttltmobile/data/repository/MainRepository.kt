package vn.edu.hcmuaf.fit.ttltmobile.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.ItemModel
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.Product

class MainRepository(private val context: Context) {
    private val apiService = ApiConfig.createService(ApiService::class.java, context)

    // 1. Lấy danh sách Phổ biến (Popular)
    fun loadPopular(): LiveData<MutableList<ItemModel>> {
        val listData = MutableLiveData<MutableList<ItemModel>>()
        apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val allProducts = response.body() ?: emptyList()

                    // LỌC: Vì Product không có rating, mình lọc lấy các món giá thấp hoặc 3 món đầu
                    val filtered = allProducts.take(3)

                    listData.value = mapProductToItem(filtered)
                }
            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    // 2. Lấy danh sách Đặc biệt (Special)
    fun loadSpecial(): LiveData<MutableList<ItemModel>> {
        val listData = MutableLiveData<MutableList<ItemModel>>()
        apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val allProducts = response.body() ?: emptyList()

                    // LỌC: Lấy các món còn lại (bỏ qua 3 món đầu)
                    val filtered = if (allProducts.size > 3) allProducts.drop(3) else allProducts

                    listData.value = mapProductToItem(filtered)
                }
            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    // 3. Hàm Mapping Xử lý Ảnh, Tên, Giá
    private fun mapProductToItem(products: List<Product>): MutableList<ItemModel> {
        return products.map { p ->
            ItemModel().apply {
                title = p.name
                price = p.price

                // Mặc định vì Product của bạn không có 2 trường này
                description = "Sản phẩm thơm ngon tuyệt vời"
                rating = 5.0

                // XỬ LÝ ẢNH: Đưa imageUrl từ Product vào picUrl của ItemModel
                picUrl.clear()
                if (p.imageUrl.isNotEmpty()) {
                    picUrl.add(p.imageUrl)
                } else {
                    picUrl.add("https://via.placeholder.com/150") // Ảnh lỗi
                }
            }
        }.toMutableList()
    }
}