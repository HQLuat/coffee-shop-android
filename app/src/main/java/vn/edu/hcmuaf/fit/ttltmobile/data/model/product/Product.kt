package vn.edu.hcmuaf.fit.ttltmobile.data.model.product

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Long,
    val name: String,
    val price: Double,
    @SerializedName("imageUrl") val imageUrl: String,
    val category: String? = null,
    val description: String? = null
)