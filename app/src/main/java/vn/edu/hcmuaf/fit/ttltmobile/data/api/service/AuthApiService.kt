package vn.edu.hcmuaf.fit.ttltmobile.data.api.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import vn.edu.hcmuaf.fit.ttltmobile.data.model.auth.*

interface AuthApiService {
    @POST("auth")
    fun register(@Body registerRequest: RegisterRequest): Call<User>

    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<User>

    @POST("auth/logout")
    fun logout(@Body logoutRequest: LogoutRequest): Call<LogoutResponse>

    @POST("auth/resend-verification")
    fun resendVerification(@Body body: Map<String, String>): Call<Map<String, String>>

    @POST("auth/forgot-password")
    fun forgotPassword(@Body body: Map<String, String>): Call<Map<String, String>>

    @GET("profile")
    fun getProfile(): Call<UserProfile>

    @Multipart
    @PUT("profile")
    fun updateProfile(
        @Part("fullName") fullName: RequestBody,
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part("address") address: RequestBody,
        @Part avatar: MultipartBody.Part? = null,
        @Part("deleteAvatar") deleteAvatar: RequestBody? = null
    ): Call<UserProfile>

    @POST("profile/change-password")
    fun changePassword(@Body body: Map<String, String>): Call<Map<String, String>>
}