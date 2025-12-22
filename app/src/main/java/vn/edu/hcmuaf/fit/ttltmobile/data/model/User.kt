package vn.edu.hcmuaf.fit.ttltmobile.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Long? = null,
    val fullName: String? = null,
    val email: String? = null,
    val message: String? = null,
    val token: String? = null,
    val refreshToken: String? = null
)
