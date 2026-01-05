package vn.edu.hcmuaf.fit.ttltmobile.ui.profile

import android.content.Context
import android.content.Intent
import android.util.Log
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiService
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityChangePasswordBinding
import vn.edu.hcmuaf.fit.ttltmobile.ui.auth.LoginActivity
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity
import java.util.regex.Pattern

class ChangePasswordActivity : BaseActivity<ActivityChangePasswordBinding>() {

    companion object {
        private val PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        )
    }

    private val apiService: ApiService by lazy {
        ApiConfig.createService(ApiService::class.java, this)
    }

    override fun getViewBinding(): ActivityChangePasswordBinding {
        return ActivityChangePasswordBinding.inflate(layoutInflater)
    }

    override fun createView() {
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }

            btnChangePassword.setOnClickListener {
                val currentPassword = edtCurrentPassword.text.toString().trim()
                val newPassword = edtNewPassword.text.toString().trim()
                val confirmPassword = edtConfirmPassword.text.toString().trim()

                if (validateInput(currentPassword, newPassword, confirmPassword)) {
                    performChangePassword(currentPassword, newPassword, confirmPassword)
                }
            }
        }
    }

    private fun validateInput(currentPassword: String, newPassword: String, confirmPassword: String): Boolean {
        binding.apply {
            when {
                currentPassword.isEmpty() -> {
                    edtCurrentPassword.error = "Vui lòng nhập mật khẩu hiện tại"
                    edtCurrentPassword.requestFocus()
                    return false
                }
                newPassword.isEmpty() -> {
                    edtNewPassword.error = "Vui lòng nhập mật khẩu mới"
                    edtNewPassword.requestFocus()
                    return false
                }
                !PASSWORD_PATTERN.matcher(newPassword).matches() -> {
                    edtNewPassword.error = "Mật khẩu yếu! Cần tối thiểu 8 ký tự, gồm: chữ hoa, thường, số và ký tự đặc biệt (@#$%^&+=!)"
                    edtNewPassword.requestFocus()
                    return false
                }
                currentPassword == newPassword -> {
                    edtNewPassword.error = "Mật khẩu mới phải khác mật khẩu hiện tại"
                    edtNewPassword.requestFocus()
                    return false
                }
                confirmPassword.isEmpty() -> {
                    edtConfirmPassword.error = "Vui lòng xác nhận mật khẩu mới"
                    edtConfirmPassword.requestFocus()
                    return false
                }
                newPassword != confirmPassword -> {
                    edtConfirmPassword.error = "Mật khẩu xác nhận không khớp"
                    edtConfirmPassword.requestFocus()
                    return false
                }
            }
        }
        return true
    }

    private fun performChangePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        showLoading()

        val body = mapOf(
            "currentPassword" to currentPassword,
            "newPassword" to newPassword,
            "confirmPassword" to confirmPassword
        )

        apiService.changePassword(body).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                hideLoading()

                if (response.isSuccessful) {
                    val message = response.body()?.get("message") ?: "Đổi mật khẩu thành công!"
                    showToast(message)

                    clearUserData()
                    navigateToLogin()
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.optString("message", "Đổi mật khẩu thất bại")
                            showToast(errorMessage)
                        } else {
                            showToast("Lỗi: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        showToast("Đổi mật khẩu thất bại")
                    }
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                hideLoading()
                Log.e("ChangePassword", "Error: ${t.message}", t)
                showToast("Không thể kết nối đến server")
            }
        })
    }

    private fun clearUserData() {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}