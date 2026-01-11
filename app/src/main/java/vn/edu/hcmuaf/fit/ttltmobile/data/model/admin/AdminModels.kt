package vn.edu.hcmuaf.fit.ttltmobile.data.model.admin

import com.google.gson.annotations.SerializedName

data class AdminMenuItem(
    val icon: String,
    val title: String,
    val subtitle: String,
    val action: () -> Unit
)

// User list response
data class AdminUserResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("fullName")
    val fullName: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("role")
    val role: String?,
    @SerializedName("enabled")
    val enabled: Boolean?,
    @SerializedName("locked")
    val locked: Boolean?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("lastLoginAt")
    val lastLoginAt: String?
)

// Paginated response
data class PagedUserResponse(
    @SerializedName("content")
    val content: List<AdminUserResponse>,
    @SerializedName("totalElements")
    val totalElements: Long,
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("number")
    val number: Int,
    @SerializedName("size")
    val size: Int
)

// Create new user request
data class AdminCreateUserRequest(
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("role")
    val role: String
)

// Update user request
data class AdminUpdateUserRequest(
    @SerializedName("fullName")
    val fullName: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("address")
    val address: String?
)

// Statistics response
data class UserStatisticsResponse(
    @SerializedName("totalUsers")
    val totalUsers: Long,
    @SerializedName("activeUsers")
    val activeUsers: Long,
    @SerializedName("lockedUsers")
    val lockedUsers: Long,
    @SerializedName("unverifiedUsers")
    val unverifiedUsers: Long,
    @SerializedName("newUsersThisMonth")
    val newUsersThisMonth: Long
)