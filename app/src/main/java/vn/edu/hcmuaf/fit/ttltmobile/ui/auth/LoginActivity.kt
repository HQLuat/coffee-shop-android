package vn.edu.hcmuaf.fit.ttltmobile.ui.auth
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.Patterns
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.LoginRequest
import vn.edu.hcmuaf.fit.ttltmobile.data.model.User
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityLoginBinding
import vn.edu.hcmuaf.fit.ttltmobile.ui.home.MainActivity
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private val apiService: ApiService by lazy {
        ApiConfig.createService(ApiService::class.java)
    }

    override fun getViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun createView() {
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = edtEmail.text.toString().trim()
                val password = edtPassword.text.toString().trim()

                if (validateInput(email, password)) {
                    performLogin(email, password)
                }
            }

            tvForgotPassword.setOnClickListener {
                showToast("Chức năng đang phát triển")
            }

            tvSignUp.setOnClickListener {
                showToast("Chức năng đăng ký đang phát triển")
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        binding.apply {
            when {
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
//                password.length < 6 -> {
//                    edtPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
//                    edtPassword.requestFocus()
//                    return false
//                }
            }
        }
        return true
    }

    private fun performLogin(email: String, password: String) {
        showLoading()

        val loginRequest = LoginRequest(email, password)
        apiService.login(loginRequest).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                hideLoading()

                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse?.refreshToken != null) {
                        // Dang nhap thanh cong
                        saveUserData(userResponse)
                        showToast("Đăng nhập thành công!")
                        navigateToMain()
                    } else {
                        showToast(userResponse?.message ?: "Đăng nhập thất bại")
                    }
                } else {
                    when (response.code()) {
                        401 -> showToast("Email hoặc mật khẩu không đúng")
                        500 -> showToast("Lỗi server, vui lòng thử lại sau")
                        else -> showToast("Lỗi: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                hideLoading()
                Log.e("LoginActivity", "Login error: ${t.message}", t)
                showToast("Không thể kết nối đến server")
            }

        })
    }

    private fun saveUserData(userResponse: User) {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putLong("user_id", userResponse.id ?: 0L)
            putString("full_name", userResponse.fullName)
            putString("email", userResponse.email)
            putString("token", userResponse.token)
            putString("refresh_token", userResponse.refreshToken)
            apply()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}