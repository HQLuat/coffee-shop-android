package vn.edu.hcmuaf.fit.ttltmobile.ui.auth

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiService
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityForgotPasswordBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity

class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding>() {

    private val apiService by lazy {
        ApiConfig.createService(ApiService::class.java)
    }

    override fun getViewBinding(): ActivityForgotPasswordBinding {
        return ActivityForgotPasswordBinding.inflate(layoutInflater)
    }

    override fun createView() {
        binding.btnSend.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                requestResetPassword(email)
            } else {
                binding.edtEmail.error = "Vui lòng nhập email"
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun requestResetPassword(email: String) {
        showLoading()
        val body = mapOf("email" to email)

        apiService.forgotPassword(body).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(
                call: Call<Map<String, String>>,
                response: Response<Map<String, String>>
            ) {
                hideLoading()
                if (response.isSuccessful) {
                    showToast(response.body()?.get("message") ?: "Vui lòng kiểm tra email")
                    finish()
                } else {
                    showToast("Lỗi: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                hideLoading()
                showToast("Lỗi kết nối mạng")
            }
        })
    }
}