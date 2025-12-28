package vn.edu.hcmuaf.fit.ttltmobile.data.model.auth

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)

data class User(
    val id: Long? = null,
    val fullName: String? = null,
    val email: String? = null,
    val message: String? = null,
    val token: String? = null,
    val refreshToken: String? = null
)

data class LogoutRequest(
    val refreshToken: String
)

data class LogoutResponse(
    val message: String
)