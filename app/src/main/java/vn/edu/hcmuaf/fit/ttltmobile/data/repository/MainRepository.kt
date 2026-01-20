package vn.edu.hcmuaf.fit.ttltmobile.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.ProductApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.ReviewRequestBody
import vn.edu.hcmuaf.fit.ttltmobile.data.model.ItemModel
import vn.edu.hcmuaf.fit.ttltmobile.data.model.ReviewModel
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.Product
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.ProductConstants.getCategoryLabel
class MainRepository(private val context: Context) {
    private val apiService = ApiConfig.createService(ProductApiService::class.java, context)

    fun loadPopular(): LiveData<MutableList<ItemModel>> {
        val result = MutableLiveData<MutableList<ItemModel>>()
        apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val allProducts = response.body() ?: emptyList()
                    processProducts(allProducts) { cleanedItems ->
                        cleanedItems.sortByDescending { it.rating }

                        result.value = cleanedItems
                    }
                }
            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                result.value = mutableListOf()
            }
        })
        return result
    }

    fun loadByCategory(categoryEnum: String): LiveData<MutableList<ItemModel>> {
        val result = MutableLiveData<MutableList<ItemModel>>()
        apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val allProducts = response.body() ?: emptyList()

                    if (categoryEnum.trim().uppercase() == "ALL") {
                        processProducts(allProducts) { cleanedItems ->
                            result.value = cleanedItems
                        }
                    } else {
                        val filtered = allProducts.filter {
                            it.category?.trim()?.uppercase() == categoryEnum.trim().uppercase()
                        }
                        processProducts(filtered) { cleanedItems ->
                            result.value = cleanedItems
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                result.value = mutableListOf()
            }
        })
        return result
    }

    private fun processProducts(
        rawProducts: List<Product>,
        onComplete: (MutableList<ItemModel>) -> Unit
    ) {
        val uniqueProducts = rawProducts.filter { it.name != null }
            .distinctBy { it.name?.trim()?.lowercase() }

        val items = mapProductToItem(uniqueProducts)

        onComplete(items)
    }

    fun addToCart(productId: Long, quantity: Int): LiveData<Pair<Boolean, String?>> {
        val result = MutableLiveData<Pair<Boolean, String?>>()

        val requestBody = vn.edu.hcmuaf.fit.ttltmobile.data.api.service.AddToCartRequestBody(
            productId = productId,
            quantity = quantity
        )

        apiService.addToCart(requestBody).enqueue(object : Callback<vn.edu.hcmuaf.fit.ttltmobile.data.api.service.CartResponseBody> {
            override fun onResponse(
                call: Call<vn.edu.hcmuaf.fit.ttltmobile.data.api.service.CartResponseBody>,
                response: Response<vn.edu.hcmuaf.fit.ttltmobile.data.api.service.CartResponseBody>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val message = body?.message ?: "Thêm vào giỏ hàng thành công"
                    result.value = Pair(true, message)
                } else {
                    val errorMessage = try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            val json = org.json.JSONObject(errorBody)
                            json.optString("message", "Thêm vào giỏ hàng thất bại")
                        } else {
                            "Thêm vào giỏ hàng thất bại"
                        }
                    } catch (e: Exception) {
                        "Thêm vào giỏ hàng thất bại"
                    }
                    result.value = Pair(false, errorMessage)
                }
            }
            override fun onFailure(call: Call<vn.edu.hcmuaf.fit.ttltmobile.data.api.service.CartResponseBody>, t: Throwable) {
                result.value = Pair(false, "Không thể kết nối đến server")
            }
        })
        return result
    }
    fun loadReviews(productId: Long): LiveData<MutableList<ReviewModel>> {
        val listData = MutableLiveData<MutableList<ReviewModel>>()
        apiService.getReviewsByProduct(productId).enqueue(object : Callback<List<ReviewModel>> {
            override fun onResponse(call: Call<List<ReviewModel>>, response: Response<List<ReviewModel>>) {
                if (response.isSuccessful) {
                    listData.value = response.body()?.toMutableList() ?: mutableListOf()
                } else {
                    listData.value = mutableListOf()
                }
            }
            override fun onFailure(call: Call<List<ReviewModel>>, t: Throwable) {
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    fun postReview(productId: Long, rating: Int, comment: String): LiveData<Pair<Boolean, String?>> {
        val result = MutableLiveData<Pair<Boolean, String?>>()
        val requestBody = ReviewRequestBody(productId, rating, comment)

        apiService.postReview(requestBody).enqueue(object : Callback<ReviewModel> {
            override fun onResponse(call: Call<ReviewModel>, response: Response<ReviewModel>) {
                if (response.isSuccessful) {
                    result.value = Pair(true, null)
                } else {
                    val errorMessage = try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            val json = org.json.JSONObject(errorBody)
                            json.optString("message", "Gửi đánh giá thất bại")
                        } else {
                            "Gửi đánh giá thất bại (${response.code()})"
                        }
                    } catch (e: Exception) {
                        "Gửi đánh giá thất bại"
                    }
                    result.value = Pair(false, errorMessage)
                }
            }
            override fun onFailure(call: Call<ReviewModel>, t: Throwable) {
                result.value = Pair(false, "Không thể kết nối đến server")
            }
        })
        return result
    }

    // THÊM HÀM UPDATE REVIEW
    fun updateReview(reviewId: Long, rating: Int, comment: String): LiveData<Pair<Boolean, String?>> {
        val result = MutableLiveData<Pair<Boolean, String?>>()
        val requestBody = ReviewRequestBody(0, rating, comment) // productId không cần thiết khi update

        apiService.updateReview(reviewId, requestBody).enqueue(object : Callback<ReviewModel> {
            override fun onResponse(call: Call<ReviewModel>, response: Response<ReviewModel>) {
                if (response.isSuccessful) {
                    result.value = Pair(true, null)
                } else {
                    val errorMessage = try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            val json = org.json.JSONObject(errorBody)
                            json.optString("message", "Cập nhật thất bại")
                        } else {
                            "Cập nhật thất bại"
                        }
                    } catch (e: Exception) {
                        "Cập nhật thất bại"
                    }
                    result.value = Pair(false, errorMessage)
                }
            }
            override fun onFailure(call: Call<ReviewModel>, t: Throwable) {
                result.value = Pair(false, "Không thể kết nối đến server")
            }
        })
        return result
    }

    // THÊM HÀM DELETE REVIEW
    fun deleteReview(reviewId: Long): LiveData<Pair<Boolean, String?>> {
        val result = MutableLiveData<Pair<Boolean, String?>>()

        apiService.deleteReview(reviewId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    result.value = Pair(true, null)
                } else {
                    result.value = Pair(false, "Xóa đánh giá thất bại")
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                result.value = Pair(false, "Không thể kết nối đến server")
            }
        })
        return result
    }

    private fun mapProductToItem(products: List<Product>): MutableList<ItemModel> {
        return products.map { p ->
            ItemModel().apply {
                id = p.id ?: 0L
                title = p.name ?: "Unknown"
                price = p.price ?: 0.0
                description = p.description ?: ""
                extra = getCategoryLabel(p.category)

                rating = 0.0

                picUrl.clear()
                if (!p.imageUrl.isNullOrEmpty()) {
                    picUrl.add(p.imageUrl)
                } else {
                    picUrl.add("https://via.placeholder.com/150")
                }
            }
        }.toMutableList()
    }

    // Thêm hàm mới
    fun loadProductVariants(productId: Long): LiveData<List<Product>> {
        val listData = MutableLiveData<List<Product>>()
        apiService.getProductVariants(productId).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    listData.value = response.body() ?: emptyList()
                } else {
                    listData.value = emptyList()
                }
            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                listData.value = emptyList()
            }
        })
        return listData
    }
}