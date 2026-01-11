package vn.edu.hcmuaf.fit.ttltmobile.ui.admin

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityAdminUserManagementBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity

class AdminUserManagementActivity : BaseActivity<ActivityAdminUserManagementBinding>() {

    private lateinit var viewModel: AdminViewModel
    private lateinit var adapter: AdminUserAdapter

    override fun getViewBinding(): ActivityAdminUserManagementBinding {
        return ActivityAdminUserManagementBinding.inflate(layoutInflater)
    }

    override fun createView() {
        setupViewModel()
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        viewModel.loadUsers()
        viewModel.loadStatistics()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[AdminViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = AdminUserAdapter(
            users = mutableListOf(),
            onUserClick = { user ->
                val intent = Intent(this, UserDetailActivity::class.java)
                intent.putExtra("userId", user.id)
                startActivity(intent)
            },
            onLockClick = { user ->
                val isLocked = user.locked ?: false
                val action = if (isLocked) "mở khóa" else "khóa"

                AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc muốn $action user ${user.fullName}?")
                    .setPositiveButton("Xác nhận") { _, _ ->
                        viewModel.toggleLockUser(user.id, !isLocked)
                    }
                    .setNegativeButton("Hủy", null)
                    .show()
            },
            onDeleteClick = { user ->
                AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa user ${user.fullName}? Hành động này không thể hoàn tác!")
                    .setPositiveButton("Xóa") { _, _ ->
                        viewModel.deleteUser(user.id)
                    }
                    .setNegativeButton("Hủy", null)
                    .show()
            }
        )

        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(this@AdminUserManagementActivity)
            adapter = this@AdminUserManagementActivity.adapter
        }
    }

    private fun setupListeners() {
        binding.apply {
            // Back button
            btnBack.setOnClickListener {
                finish()
            }

            // Refresh
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.loadUsers()
                viewModel.loadStatistics()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.userList.observe(this) { pagedResponse ->
            adapter.updateUsers(pagedResponse.content)

            // Hide empty state
            binding.layoutEmptyState.visibility =
                if (pagedResponse.content.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.statistics.observe(this) { stats ->
            binding.apply {
                cardTotal.tvStatValue.text = stats.totalUsers.toString()
                cardTotal.tvStatLabel.text = "Tổng số"

                cardActive.tvStatValue.text = stats.activeUsers.toString()
                cardActive.tvStatLabel.text = "Hoạt động"

                cardLocked.tvStatValue.text = stats.lockedUsers.toString()
                cardLocked.tvStatLabel.text = "Đã khóa"
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                showToast(message)
            }
        }

        viewModel.successMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                showToast(message)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUsers()
        viewModel.loadStatistics()
    }
}