package vn.edu.hcmuaf.fit.ttltmobile.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.*

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

    // ====== THÊM 3 HÀM MỚI BÊN DƯỚI ======

    // Hàm 1: Kiểm tra token có hết hạn không
    fun isTokenExpired(): Boolean {
        val token = getAccessToken() ?: return true

        try {
            val parts = token.split(".")
            if (parts.size != 3) return true

            val payload = String(Base64.getUrlDecoder().decode(parts[1]))
            val expMatch = Regex("\"exp\":(\\d+)").find(payload)
            val exp = expMatch?.groupValues?.get(1)?.toLongOrNull() ?: return true

            val currentTime = System.currentTimeMillis() / 1000
            return currentTime >= (exp - 60)

        } catch (e: Exception) {
            return true
        }
    }

    // Hàm 2: Lấy user_id
    fun getUserId(): Long {
        return sharedPreferences.getLong("user_id", 0L)
    }

    // Hàm 3: Lưu user_id
    fun saveUserId(userId: Long) {
        sharedPreferences.edit().apply {
            putLong("user_id", userId)
            apply()
        }
    }
}