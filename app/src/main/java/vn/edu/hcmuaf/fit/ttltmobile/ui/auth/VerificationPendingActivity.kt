package vn.edu.hcmuaf.fit.ttltmobile.ui.auth

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiService
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityVerificationPendingBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity

class VerificationPendingActivity : BaseActivity<ActivityVerificationPendingBinding>() {

    private val apiService: ApiService by lazy {
        ApiConfig.createService(ApiService::class.java)
    }

    private var userEmail: String? = null

    override fun getViewBinding(): ActivityVerificationPendingBinding {
        return ActivityVerificationPendingBinding.inflate(layoutInflater)
    }

    override fun createView() {
        userEmail = intent.getStringExtra("email")

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.tvEmailDisplay.text = userEmail ?: "Email không xác định"
    }

    private fun setupClickListeners() {
        binding.apply {
            btnBackToLogin.setOnClickListener {
                navigateToLogin()
            }

            btnResend.setOnClickListener {
                if (!userEmail.isNullOrEmpty()) {
                    resendVerificationEmail(userEmail!!)
                } else {
                    showToast("Không tìm thấy địa chỉ email để gửi lại")
                }
            }
        }
    }

    private fun resendVerificationEmail(email: String) {
        showLoading()

        val body = mapOf("email" to email)

        apiService.resendVerification(body).enqueue(object: Callback<Map<String, String>> {
            override fun onResponse(
                call: Call<Map<String, String>>,
                response: Response<Map<String, String>>
            ) {
                hideLoading()

                if (response.isSuccessful) {
                    val message = response.body()?.get("message")
                    showToast(message ?: "Đã gửi lại email xác thực thành công!")
                } else {
                    val errorBodyString = response.errorBody()?.string()

                    try {
                        val jsonObject = JSONObject(errorBodyString ?: "{}")
                        val errorMessage = jsonObject.optString("message", "Có lỗi xảy ra")

                        if (response.code() == 400 && errorMessage.contains("đã được xác thực", true)) {
                            showUserAlreadyVerifiedDialog()
                        } else {
                            showToast(errorMessage)
                        }
                    } catch (e: Exception) {
                        showToast("Lỗi: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                hideLoading()
                Log.e("VerifyPending", "Resend error: ${t.message}", t)
                showToast("Không thể kết nối đến server.")
            }
        })
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("registered_email", userEmail)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showUserAlreadyVerifiedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Xác thực thành công")
            .setMessage("Tài khoản của bạn đã được xác thực trước đó. Bạn có thể đăng nhập ngay bây giờ.")
            .setPositiveButton("Đăng nhập ngay") { dialog, _ ->
                dialog.dismiss()
                navigateToLogin()
            }
            .setCancelable(false)
            .show()
    }
}