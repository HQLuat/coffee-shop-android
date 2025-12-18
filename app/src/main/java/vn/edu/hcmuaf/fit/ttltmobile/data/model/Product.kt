package vn.edu.hcmuaf.fit.ttltmobile.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    @SerializedName("image_url") val imageUrl: String
)