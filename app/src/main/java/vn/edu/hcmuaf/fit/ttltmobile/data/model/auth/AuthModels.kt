package vn.edu.hcmuaf.fit.ttltmobile.data.model.auth

import com.google.gson.annotations.SerializedName

// Login
data class LoginRequest(
    val email: String,
    val password: String
)

// Register
data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)

// Login and Register response
data class User(
    val id: Long? = null,
    val fullName: String? = null,
    val email: String? = null,
    val message: String? = null,
    val token: String? = null,
    val refreshToken: String? = null
)

// Logout
data class LogoutRequest(
    val refreshToken: String
)
data class LogoutResponse(
    val message: String
)

// Profile
data class UserProfile(
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
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("lastLoginAt")
    val lastLoginAt: String?,
    @SerializedName("message")
    val message: String?
)
data class UpdateUserProfileRequest(
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("currentPassword")
    val currentPassword: String? = null,
    @SerializedName("newPassword")
    val newPassword: String? = null,
    @SerializedName("confirmNewPassword")
    val confirmNewPassword: String? = null
)