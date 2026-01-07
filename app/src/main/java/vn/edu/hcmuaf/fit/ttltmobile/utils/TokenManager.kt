package vn.edu.hcmuaf.fit.ttltmobile.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun getAccessToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            putString("token", accessToken)
            putString("refresh_token", refreshToken)
            apply()
        }
    }

    fun clearTokens() {
        sharedPreferences.edit().apply {
            remove("token")
            remove("refresh_token")
            apply()
        }
    }

    fun hasValidTokens(): Boolean {
        return !getAccessToken().isNullOrEmpty() && !getRefreshToken().isNullOrEmpty()
    }
}