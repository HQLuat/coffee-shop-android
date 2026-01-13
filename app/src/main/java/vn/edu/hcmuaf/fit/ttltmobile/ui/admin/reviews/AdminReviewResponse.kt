package vn.edu.hcmuaf.fit.ttltmobile.data.model.admin

import com.google.gson.annotations.SerializedName

data class AdminReviewResponse(
    val id: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("userName") val userName: String,
    @SerializedName("userEmail") val userEmail: String,
    @SerializedName("productId") val productId: Long,
    @SerializedName("productName") val productName: String,
    val rating: Int,
    val comment: String,
    @SerializedName("createdAt") val createdAt: String
)