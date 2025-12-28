package vn.edu.hcmuaf.fit.ttltmobile.ui.auth

import android.content.Intent
import android.util.Log
import android.util.Patterns
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.RegisterRequest
import vn.edu.hcmuaf.fit.ttltmobile.data.model.User
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityRegisterBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity

class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {

    private val apiService: ApiService by lazy {
        ApiConfig.createService(ApiService::class.java)
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
            when {
                fullName.isEmpty() -> {
                    edtFullName.error = "Vui lòng nhập họ và tên"
                    edtFullName.requestFocus()
                    return false
                }
                fullName.length < 2 -> {
                    edtFullName.error = "Họ tên phải có ít nhất 2 ký tự"
                    edtFullName.requestFocus()
                    return false
                }
                email.isEmpty() -> {
                    edtEmail.error = "Vui lòng nhập email"
                    edtEmail.requestFocus()
                    return false
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    edtEmail.error = "Email không hợp lệ"
                    edtEmail.requestFocus()
                    return false
                }
                password.isEmpty() -> {
                    edtPassword.error = "Vui lòng nhập mật khẩu"
                    edtPassword.requestFocus()
                    return false
                }
                password.length < 6 -> {
                    edtPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
                    edtPassword.requestFocus()
                    return false
                }
                confirmPassword.isEmpty() -> {
                    edtConfirmPassword.error = "Vui lòng xác nhận mật khẩu"
                    edtConfirmPassword.requestFocus()
                    return false
                }
                password != confirmPassword -> {
                    edtConfirmPassword.error = "Mật khẩu không khớp"
                    edtConfirmPassword.requestFocus()
                    return false
                }
            }
        }
        return true
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
                        showToast("Đăng ký thành công! Vui lòng đăng nhập")
                        navigateToLoginWithEmail(binding.edtEmail.text.toString().trim())
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

    private fun navigateToLoginWithEmail(email: String) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("registered_email", email)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        finish()
    }
}