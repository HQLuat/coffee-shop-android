package vn.edu.hcmuaf.fit.ttltmobile.ui.admin

import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityUserDetailBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity
import java.text.SimpleDateFormat
import java.util.Locale

class UserDetailActivity : BaseActivity<ActivityUserDetailBinding>() {

    private lateinit var viewModel: AdminViewModel
    private var userId: Long = 0

    override fun getViewBinding(): ActivityUserDetailBinding {
        return ActivityUserDetailBinding.inflate(layoutInflater)
    }

    override fun createView() {
        userId = intent.getLongExtra("userId", 0)
        if (userId == 0L) {
            showToast("User không hợp lệ")
            finish()
            return
        }

        setupViewModel()
        setupListeners()
        observeViewModel()

        viewModel.loadUserDetails(userId)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[AdminViewModel::class.java]
    }

    private fun setupListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }

            btnChangeRole.setOnClickListener {
                showChangeRoleDialog()
            }

            btnToggleLock.setOnClickListener {
                viewModel.userDetails.value?.let { user ->
                    val isLocked = user.locked ?: false
                    val action = if (isLocked) "mở khóa" else "khóa"

                    AlertDialog.Builder(this@UserDetailActivity)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc muốn $action user này?")
                        .setPositiveButton("Xác nhận") { _, _ ->
                            viewModel.toggleLockUser(userId, !isLocked)
                        }
                        .setNegativeButton("Hủy", null)
                        .show()
                }
            }

            btnResetPassword.setOnClickListener {
                showResetPasswordDialog()
            }

            btnDeleteUser.setOnClickListener {
                AlertDialog.Builder(this@UserDetailActivity)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa user này? Hành động này không thể hoàn tác!")
                    .setPositiveButton("Xóa") { _, _ ->
                        viewModel.deleteUser(userId)
                    }
                    .setNegativeButton("Hủy", null)
                    .show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.userDetails.observe(this) { user ->
            binding.apply {
                // User information
                tvUserName.text = user.fullName ?: "N/A"
                tvUserEmail.text = user.email ?: "N/A"
                tvUserPhone.text = user.phoneNumber ?: "Chưa cập nhật"
                tvUserAddress.text = user.address ?: "Chưa cập nhật"

                // Role
                tvUserRole.text = when (user.role) {
                    "USER" -> "Người dùng"
                    "ADMIN" -> "Quản trị viên"
                    else -> user.role ?: "N/A"
                }

                // Status
                val statusText = when {
                    user.locked == true -> "Đã khóa"
                    user.enabled == false -> "Chưa xác thực"
                    else -> "Hoạt động"
                }
                tvUserStatus.text = statusText
                tvUserStatus.setTextColor(
                    when {
                        user.locked == true -> Color.RED
                        user.enabled == false -> Color.parseColor("#FF9800")
                        else -> Color.parseColor("#4CAF50")
                    }
                )

                tvCreatedAt.text = formatDate(user.createdAt)
                tvLastLoginAt.text = formatDate(user.lastLoginAt) ?: "Chưa đăng nhập"

                // Button lock
                btnToggleLock.text = if (user.locked == true) "Mở khóa User" else "Khóa User"
                btnToggleLock.setBackgroundColor(
                    if (user.locked == true) Color.parseColor("#4CAF50") else Color.RED
                )
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                showToast(message)
            }
        }

        viewModel.successMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                showToast(message)
                if (message.contains("xóa", ignoreCase = true)) {
                    finish()
                }
            }
        }
    }

    private fun showChangeRoleDialog() {
        val roles = arrayOf("USER", "ADMIN")
        val roleNames = arrayOf("Người dùng", "Quản trị viên")

        AlertDialog.Builder(this)
            .setTitle("Chọn role mới")
            .setItems(roleNames) { _, which ->
                val newRole = roles[which]
                viewModel.changeUserRole(userId, newRole)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showResetPasswordDialog() {
        val dialogView = layoutInflater.inflate(
            android.R.layout.simple_list_item_1,
            null
        )

        val editText = android.widget.EditText(this).apply {
            hint = "Nhập mật khẩu mới"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        AlertDialog.Builder(this)
            .setTitle("Reset mật khẩu")
            .setView(editText)
            .setPositiveButton("Reset") { _, _ ->
                val newPassword = editText.text.toString().trim()
                if (newPassword.length >= 8) {
                    viewModel.resetPassword(userId, newPassword)
                } else {
                    showToast("Mật khẩu phải có ít nhất 8 ký tự")
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun formatDate(dateString: String?): String {
        return try {
            if (dateString.isNullOrEmpty()) return "N/A"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: "N/A"
        } catch (e: Exception) {
            "N/A"
        }
    }
}