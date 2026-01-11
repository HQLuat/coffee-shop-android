package vn.edu.hcmuaf.fit.ttltmobile.ui.admin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.R
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.AuthApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.admin.AdminMenuItem
import vn.edu.hcmuaf.fit.ttltmobile.data.model.auth.LogoutRequest
import vn.edu.hcmuaf.fit.ttltmobile.data.model.auth.LogoutResponse
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityAdminDashboardBinding
import vn.edu.hcmuaf.fit.ttltmobile.ui.auth.LoginActivity
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity
import vn.edu.hcmuaf.fit.ttltmobile.ui.admin.AdminUserManagementActivity

class AdminDashboardActivity : BaseActivity<ActivityAdminDashboardBinding>() {

    private val apiService: AuthApiService by lazy {
        ApiConfig.getAuthService(this)
    }

    private lateinit var menuAdapter: AdminMenuAdapter

    override fun getViewBinding(): ActivityAdminDashboardBinding {
        return ActivityAdminDashboardBinding.inflate(layoutInflater)
    }

    override fun createView() {
        loadAdminInfo()
        setupMenuGrid()
        setupClickListeners()
    }

    private fun loadAdminInfo() {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val fullName = sharedPref.getString("full_name", "Administrator")
        val email = sharedPref.getString("email", "admin@coffeeshop.com")

        binding.apply {
            tvAdminName.text = fullName
            tvAdminEmail.text = email
        }
    }

    private fun setupMenuGrid() {
        val menuItems = listOf(
            AdminMenuItem(R.drawable.ic_product, "Sản phẩm", "Quản lý menu") { navigateToProductManagement() },
            AdminMenuItem(R.drawable.ic_shopping_bag, "Đơn hàng", "Xem đơn hàng") { navigateToOrderManagement() },
            AdminMenuItem(R.drawable.ic_role, "Người dùng", "Quản lý user") { navigateToUserManagement() },
            AdminMenuItem(R.drawable.ic_voucher, "Voucher", "Mã giảm giá") { navigateToVoucherManagement() },
            AdminMenuItem(R.drawable.ic_statistic, "Thống kê", "Báo cáo doanh thu") { navigateToStatistics() },
            AdminMenuItem(R.drawable.ic_comment, "Đánh giá", "Quản lý review") { navigateToReviewManagement() }
        )

        menuAdapter = AdminMenuAdapter(menuItems)

        binding.rvAdminMenu.apply {
            layoutManager = GridLayoutManager(this@AdminDashboardActivity, 2)
            adapter = menuAdapter
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnLogout.setOnClickListener {
                showLogoutConfirmDialog()
            }

            btnSwitchToUser.setOnClickListener {
                showSwitchModeDialog()
            }
        }
    }

    private fun showSwitchModeDialog() {
        AlertDialog.Builder(this)
            .setTitle("Chuyển sang chế độ người dùng")
            .setMessage("Bạn có muốn chuyển sang giao diện người dùng?")
            .setPositiveButton("Chuyển") { _, _ ->
                val intent = Intent(this, vn.edu.hcmuaf.fit.ttltmobile.ui.home.MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showLogoutConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất?")
            .setPositiveButton("Đăng xuất") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun performLogout() {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val refreshToken = sharedPref.getString("refresh_token", null)

        if (refreshToken.isNullOrEmpty()) {
            navigateToLogin()
            return
        }

        showLoading()

        val logoutRequest = LogoutRequest(refreshToken)
        apiService.logout(logoutRequest).enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                hideLoading()
                clearUserData()
                navigateToLogin()
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                hideLoading()
                clearUserData()
                navigateToLogin()
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

    // Navigation methods
    private fun navigateToProductManagement() {
        showToast("Chức năng Quản lý sản phẩm đang phát triển")
    }

    private fun navigateToOrderManagement() {
        showToast("Chức năng Quản lý đơn hàng đang phát triển")
    }

    private fun navigateToUserManagement() {
        val intent = Intent(this, AdminUserManagementActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToVoucherManagement() {
        showToast("Chức năng Quản lý voucher đang phát triển")
    }

    private fun navigateToStatistics() {
        showToast("Chức năng Thống kê đang phát triển")
    }

    private fun navigateToReviewManagement() {
        showToast("Chức năng Quản lý đánh giá đang phát triển")
    }
}