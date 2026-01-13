package vn.edu.hcmuaf.fit.ttltmobile.data.model.product

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Product(
    val id: Long = 0,
    val name: String,
    val price: Double,
    @SerializedName("imageUrl") val imageUrl: String,
    val category: String,
    val size: String,
    val description: String? = null
) : Serializable