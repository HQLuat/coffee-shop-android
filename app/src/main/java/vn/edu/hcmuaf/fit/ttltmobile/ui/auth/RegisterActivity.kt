package vn.edu.hcmuaf.fit.ttltmobile.ui.auth

import android.content.Intent
import android.util.Log
import android.util.Patterns
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.AuthApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.auth.RegisterRequest
import vn.edu.hcmuaf.fit.ttltmobile.data.model.auth.User
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityRegisterBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity
import java.util.regex.Pattern

class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {

    // Regex
    companion object {
        private val PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        )
        private val TRUSTED_DOMAINS = setOf(
            "gmail.com", "yahoo.com", "outlook.com", "hotmail.com",
            "icloud.com", "protonmail.com", "zoho.com", "aol.com"
        )
    }

    private val apiService: AuthApiService by lazy {
        ApiConfig.getAuthService()
    }

    override fun getViewBinding(): ActivityRegisterBinding {
        return ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun createView() {
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnRegister.setOnClickListener {
                val fullName = edtFullName.text.toString().trim()
                val email = edtEmail.text.toString().trim()
                val password = edtPassword.text.toString().trim()
                val confirmPassword = edtConfirmPassword.text.toString().trim()
                
                if (validateInput(fullName, email, password, confirmPassword)) {
                    performRegister(fullName, email, password)
                }
            }
            
            tvLogin.setOnClickListener {
                navigateToLogin()
            }
            
            backBtn.setOnClickListener {
                finish()
            }
        }
    }

    private fun validateInput(fullName: String, email: String, password: String, confirmPassword: String): Boolean {
        binding.apply {
            // check full name
            if (fullName.isEmpty()) {
                edtFullName.error = "Vui lòng nhập họ và tên"
                edtFullName.requestFocus()
                return false
            }
            if (fullName.length < 2) {
                edtFullName.error = "Họ tên phải có ít nhất 2 ký tự"
                edtFullName.requestFocus()
                return false
            }

            // check email
            if (email.isEmpty()) {
                edtEmail.error = "Vui lòng nhập email"
                edtEmail.requestFocus()
                return false
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.error = "Định dạng email không hợp lệ"
                edtEmail.requestFocus()
                return false
            }
            if (!isValidEmailDomain(email)) {
                edtEmail.error = "Hệ thống chỉ chấp nhận: Gmail, Yahoo, Outlook, iCloud, Protonmail, Zoho, AOL"
                edtEmail.requestFocus()
                return false
            }

            // check password
            if (password.isEmpty()) {
                edtPassword.error = "Vui lòng nhập mật khẩu"
                edtPassword.requestFocus()
                return false
            }
            if (!PASSWORD_PATTERN.matcher(password).matches()) {
                edtPassword.error = "Mật khẩu yếu! Cần tối thiểu 8 ký tự, gồm: chữ hoa, thường, số và ký tự đặc biệt (@#$%^&+=!)"
                edtPassword.requestFocus()
                return false
            }

            // check confirm password
            if (confirmPassword.isEmpty()) {
                edtConfirmPassword.error = "Vui lòng xác nhận mật khẩu"
                edtConfirmPassword.requestFocus()
                return false
            }
            if (password != confirmPassword) {
                edtConfirmPassword.error = "Mật khẩu xác nhận không khớp"
                edtConfirmPassword.requestFocus()
                return false
            }
        }
        return true
    }

    private fun isValidEmailDomain(email: String): Boolean {
        return try {
            val domain = email.substring(email.indexOf("@") + 1).lowercase()
            TRUSTED_DOMAINS.contains(domain)
        } catch (e: Exception) {
            false
        }
    }

    private fun performRegister(fullName: String, email: String, password: String) {
        showLoading()

        val registerRequest = RegisterRequest(fullName, email, password)
        apiService.register(registerRequest).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                hideLoading()

                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse?.id != null) {
                        navigateToVerification(email)
                    } else {
                        showToast(userResponse?.message ?: "Đăng ký thất bại")
                    }
                } else {
                    when (response.code()) {
                        400 -> showToast("Thông tin không hợp lệ")
                        409 -> showToast("Email đã được sử dụng")
                        500 -> showToast("Lỗi server, vui lòng thử lại sau")
                        else -> showToast("Lỗi: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                hideLoading()
                Log.e("RegisterActivity", "Register error: ${t.message}", t)
                showToast("Không thể kết nối đến server")
            }

        })
    }

    private fun navigateToLogin() {
        finish()
    }

    private fun navigateToVerification(email: String) {
        val intent = Intent(this, VerificationPendingActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }
}