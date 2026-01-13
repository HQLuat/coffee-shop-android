package vn.edu.hcmuaf.fit.ttltmobile.data.model.product

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductVariant(
    val id: Long,
    val name: String,
    val price: Double,
    @SerializedName("imageUrl") val imageUrl: String,
    val category: String?,
    val description: String?,
    val size: String,
    val rating: Double
) : Serializable