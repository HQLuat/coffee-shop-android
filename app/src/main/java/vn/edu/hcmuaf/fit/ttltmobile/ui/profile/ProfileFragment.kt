package vn.edu.hcmuaf.fit.ttltmobile.ui.profile

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.LogoutRequest
import vn.edu.hcmuaf.fit.ttltmobile.data.model.LogoutResponse
import vn.edu.hcmuaf.fit.ttltmobile.databinding.FragmentProfileBinding
import vn.edu.hcmuaf.fit.ttltmobile.ui.auth.LoginActivity
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseFragment

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val apiService: ApiService by lazy {
        ApiConfig.createService(ApiService::class.java, requireContext())
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        loadUserInfo()
        setupClickListeners()
    }

    private fun loadUserInfo() {
        val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val fullName = sharedPref.getString("full_name", "Tên người dùng")
        val email = sharedPref.getString("email", "email@example.com")

        binding.apply {
            tvFullName.text = fullName
            tvEmail.text = email
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            layoutPersonalInfo.setOnClickListener {
                showToast("Chức năng đang phát triển")
            }

            layoutChangePassword.setOnClickListener {
                showToast("Chức năng đang phát triển")
            }

            layoutOrders.setOnClickListener {
                showToast("Chức năng đang phát triển")
            }

            btnLogout.setOnClickListener {
                showLogoutConfirmDialog()
            }
        }
    }

    private fun showLogoutConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất?")
            .setPositiveButton("Đăng xuất") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Huỷ", null)
            .show()
    }

    private fun performLogout() {
        val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val refreshToken = sharedPref.getString("refresh_token", null)

        if (refreshToken.isNullOrEmpty()) {
            showToast("Không tìm thấy thông tin đăng nhập")
            navigateToLogin()
            return
        }

        showLoading()

        val logoutRequest = LogoutRequest(refreshToken)
        apiService.logout(logoutRequest).enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(
                call: Call<LogoutResponse>,
                response: Response<LogoutResponse>
            ) {
                hideLoading()

                if (response.isSuccessful) {
                    val logoutResponse = response.body()
                    showToast(logoutResponse?.message ?: "Đăng xuất thành công")
                    clearUserData()
                    navigateToLogin()
                } else {
                    showToast("Đăng xuất thành công")
                    clearUserData()
                    navigateToLogin()
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                hideLoading()
                Log.e("ProfileActivity", "Logout error: ${t.message}", t)
                showToast("Đăng xuất thành công")
                clearUserData()
                navigateToLogin()
            }

        })
    }

    private fun clearUserData() {
        val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}