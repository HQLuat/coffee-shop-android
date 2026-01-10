package vn.edu.hcmuaf.fit.ttltmobile.ui.admin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hcmuaf.fit.ttltmobile.data.model.admin.AdminUserResponse
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ItemAdminUserBinding
import java.text.SimpleDateFormat
import java.util.*

class AdminUserAdapter(
    private val users: MutableList<AdminUserResponse>,
    private val onUserClick: (AdminUserResponse) -> Unit,
    private val onLockClick: (AdminUserResponse) -> Unit,
    private val onDeleteClick: (AdminUserResponse) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemAdminUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemAdminUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        with(holder.binding) {
            tvUserName.text = user.fullName ?: "N/A"
            tvUserEmail.text = user.email ?: "N/A"
            tvUserRole.text = when (user.role) {
                "USER" -> "Người dùng"
                "ADMIN" -> "Quản trị viên"
                else -> user.role ?: "N/A"
            }

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

            tvUserCreatedAt.text = "Tạo: ${formatDate(user.createdAt)}"

            // Button lock/unlock
            btnLock.text = if (user.locked == true) "Mở khóa" else "Khóa"
            btnLock.setBackgroundColor(
                if (user.locked == true) Color.parseColor("#4CAF50") else Color.RED
            )

            // Click events
            root.setOnClickListener { onUserClick(user) }
            btnLock.setOnClickListener { onLockClick(user) }
            btnDelete.setOnClickListener { onDeleteClick(user) }
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<AdminUserResponse>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    private fun formatDate(dateString: String?): String {
        return try {
            if (dateString.isNullOrEmpty()) return "N/A"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: "N/A"
        } catch (e: Exception) {
            "N/A"
        }
    }
}