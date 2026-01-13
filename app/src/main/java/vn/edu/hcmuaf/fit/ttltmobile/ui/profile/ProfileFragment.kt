package vn.edu.hcmuaf.fit.ttltmobile.ui.profile

import android.content.Context
import android.content.Intent
import com.bumptech.glide.request.target.Target
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.R
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.AuthApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.auth.LogoutRequest
import vn.edu.hcmuaf.fit.ttltmobile.data.model.auth.LogoutResponse
import vn.edu.hcmuaf.fit.ttltmobile.databinding.FragmentProfileBinding
import vn.edu.hcmuaf.fit.ttltmobile.ui.auth.LoginActivity
import vn.edu.hcmuaf.fit.ttltmobile.ui.order.OrderHistoryActivity
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseFragment

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val apiService: AuthApiService by lazy {
        ApiConfig.getAuthService(requireContext())
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        loadUserInfo()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        loadUserInfo()
    }

    private fun loadUserInfo() {
        val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val fullName = sharedPref.getString("full_name", "Tên người dùng")
        val email = sharedPref.getString("email", "email@example.com")
        val avatarUrl = sharedPref.getString("avatar_url", null)

        binding.apply {
            tvFullName.text = fullName
            tvEmail.text = email

            // Load avatar
            if (!avatarUrl.isNullOrEmpty()) {
                ivAvatar.background = null
                ivAvatar.setPadding(0, 0, 0, 0)
                ivAvatar.imageTintList = null
                ivAvatar.clearColorFilter()

                Glide.with(this@ProfileFragment)
                    .load(avatarUrl.trim())
                    .circleCrop()
                    .placeholder(R.drawable.ic_user_placeholder)
                    .error(R.drawable.ic_user_placeholder)
                    .into(ivAvatar)
            } else {
                ivAvatar.setImageResource(R.drawable.ic_person)
                ivAvatar.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
                val padding = dpToPx(22)
                ivAvatar.setPadding(padding, padding, padding, padding)
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun setupClickListeners() {
        binding.apply {
            layoutPersonalInfo.setOnClickListener {
                navigateToPersonalInfo()
            }

            layoutChangePassword.setOnClickListener {
                navigateToChangePassword()
            }

            layoutOrders.setOnClickListener {
                navigateToOrderHistory()
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
            override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
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

    private fun navigateToPersonalInfo() {
        val intent = Intent(requireContext(), PersonalInfoActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToChangePassword() {
        val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToOrderHistory() {
        val intent = Intent(requireContext(), OrderHistoryActivity::class.java)
        startActivity(intent)
    }

}