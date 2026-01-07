package vn.edu.hcmuaf.fit.ttltmobile.data.api

import android.content.Context
import android.content.Intent
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import vn.edu.hcmuaf.fit.ttltmobile.ui.auth.LoginActivity
import vn.edu.hcmuaf.fit.ttltmobile.utils.TokenManager

class AuthInterceptor(private val context: Context) : Interceptor {

    private val tokenManager = TokenManager(context)

    private val refreshClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()

        if (isPublicEndpoint(url)) {
            return chain.proceed(originalRequest)
        }

        val accessToken = tokenManager.getAccessToken()

        if (accessToken.isNullOrEmpty()) {
            redirectToLogin()
            return chain.proceed(originalRequest)
        }

        val authenticatedRequest = buildRequestWithToken(originalRequest, accessToken)
        val response = chain.proceed(authenticatedRequest)

        if (response.code == 401 || response.code == 403) {
            Log.d("AuthInterceptor", "Token expired or rejected (${response.code}). Attempting refresh...")

            response.close()

            synchronized(this) {
                val currentAccessToken = tokenManager.getAccessToken()

                if (currentAccessToken != null && currentAccessToken != accessToken) {
                    Log.d("AuthInterceptor", "Token already refreshed. Retrying...")
                    val newRequest = buildRequestWithToken(originalRequest, currentAccessToken)
                    return chain.proceed(newRequest)
                }

                val newAccessToken = refreshAccessToken()

                if (newAccessToken != null) {
                    Log.d("AuthInterceptor", "Refresh SUCCESS. Retrying original request...")
                    val newRequest = buildRequestWithToken(originalRequest, newAccessToken)
                    return chain.proceed(newRequest)
                } else {
                    Log.e("AuthInterceptor", "Refresh FAILED. Redirecting to login.")
                    redirectToLogin()
                    return chain.proceed(originalRequest)
                }
            }
        }

        return response
    }

    private fun buildRequestWithToken(request: Request, token: String): Request {
        return request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
    }

    private fun isPublicEndpoint(url: String): Boolean {
        return url.contains("/login") ||
                url.contains("/register") ||
                url.contains("/refresh") ||
                url.contains("/forgot-password")
    }

    private fun refreshAccessToken(): String? {
        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken.isNullOrEmpty()) {
            Log.e("AuthInterceptor", "No refresh token found in storage.")
            return null
        }

        try {
            val jsonBody = JSONObject().apply {
                put("refreshToken", refreshToken)
            }

            val requestBody = jsonBody.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            val refreshUrl = "${ApiConfig.BASE_URL}users/refresh"

            Log.d("AuthInterceptor", "Calling Refresh API: $refreshUrl")

            val request = Request.Builder()
                .url(refreshUrl)
                .post(requestBody)
                .build()

            val response = refreshClient.newCall(request).execute()

            val responseBody = response.body?.string()

            Log.d("AuthInterceptor", "Refresh Response Code: ${response.code}")
            Log.d("AuthInterceptor", "Refresh Response Body: $responseBody")

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                val newAccessToken = jsonResponse.optString("accessToken")
                val newRefreshToken = jsonResponse.optString("refreshToken", refreshToken)

                if (newAccessToken.isNotEmpty()) {
                    tokenManager.saveTokens(newAccessToken, newRefreshToken)
                    return newAccessToken
                }
            } else {
                if (response.code == 401 || response.code == 400 || response.code == 403) {
                    Log.e("AuthInterceptor", "Refresh token invalid or expired on server.")
                    tokenManager.clearTokens()
                }
            }
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Exception during refresh", e)
        }
        return null
    }

    private fun redirectToLogin() {
        tokenManager.clearTokens()
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().clear().apply()

        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("session_expired", true)
        }
        context.startActivity(intent)
    }
}