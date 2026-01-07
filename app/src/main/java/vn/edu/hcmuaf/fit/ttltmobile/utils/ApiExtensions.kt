package vn.edu.hcmuaf.fit.ttltmobile.utils

import android.content.Context
import android.content.Intent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.ui.auth.LoginActivity

fun <T> Call<T>.enqueueWithAuth(
    context: Context,
    onSuccess: (T) -> Unit,
    onError: ((String) -> Unit)? = null
) {
    this.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            when (response.code()) {
                200, 201 -> {
                    response.body()?.let { onSuccess(it) }
                        ?: onError?.invoke("Dữ liệu trả về rỗng")
                }
                401 -> {
                    // Token expired - already handled by AuthInterceptor
                    // This case should rarely happen because interceptor handles it
                    handleSessionExpired(context)
                }
                else -> {
                    val errorMsg = try {
                        response.errorBody()?.string() ?: "Lỗi: ${response.code()}"
                    } catch (e: Exception) {
                        "Lỗi: ${response.code()}"
                    }
                    onError?.invoke(errorMsg)
                }
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onError?.invoke("Lỗi kết nối: ${t.message}")
        }
    })
}

private fun handleSessionExpired(context: Context) {
    // Clear all user data
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    sharedPref.edit().clear().apply()

    // Redirect to login
    val intent = Intent(context, LoginActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("session_expired", true)
    }
    context.startActivity(intent)
}