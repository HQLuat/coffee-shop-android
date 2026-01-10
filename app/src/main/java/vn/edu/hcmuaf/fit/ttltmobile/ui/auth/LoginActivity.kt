package vn.edu.hcmuaf.fit.ttltmobile.ui.auth
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.Patterns
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.AuthApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.auth.LoginRequest
import vn.edu.hcmuaf.fit.ttltmobile.data.model.auth.User
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityLoginBinding
import vn.edu.hcmuaf.fit.ttltmobile.ui.home.MainActivity
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity
import vn.edu.hcmuaf.fit.ttltmobile.ui.admin.AdminDashboardActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private val apiService: AuthApiService by lazy {
        ApiConfig.getAuthService()
    }

    override fun getViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun createView() {
        setupClickListeners()
        handleRegisteredEmail()
        handleSessionExpired()
    }

    private fun handleSessionExpired() {
        val sessionExpired = intent.getBooleanExtra("session_expired", false)
        if (sessionExpired) {
            showToast("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.")
        }
    }

    private fun handleRegisteredEmail() {
        val registeredEmail = intent.getStringExtra("registered_email")
        if (!registeredEmail.isNullOrEmpty()) {
            binding.edtEmail.setText(registeredEmail)
            binding.edtPassword.requestFocus()
        }
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
                navigateToForgotPass()
            }

            tvSignUp.setOnClickListener {
                navigateToRegister()
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
                    if (userResponse != null) {
                        saveUserData(userResponse)
                        showToast("Đăng nhập thành công!")
                        navigateToMain()
                    } else {
                        showToast("Dữ liệu trả về bị lỗi")
                    }
                } else {
                    try {
                        val errorBody = response.errorBody()

                        if (errorBody != null) {
                            val errorBodyString = errorBody.string()
                            val jsonObject = JSONObject(errorBodyString)
                            val serverMessage = jsonObject.optString("message", "Đăng nhập thất bại")

                            showToast(serverMessage)
                        } else {
                            showToast("Lỗi: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        showToast("Lỗi không xác định")
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
            putString("user_role", userResponse.role)
            apply()
        }
    }

    private fun navigateToMain() {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userRole = sharedPref.getString("user_role", "USER")

        val intent = if (userRole == "ADMIN") {
            Intent(this, AdminDashboardActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToForgotPass() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}