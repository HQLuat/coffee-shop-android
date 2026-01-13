package vn.edu.hcmuaf.fit.ttltmobile.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReviewModel(
    val id: Long,
    val comment: String,
    @SerializedName("createdAt") val createdAt: String,
    val rating: Int,
    @SerializedName("productId") val productId: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("reviewerName") val reviewerName: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
) : Serializable