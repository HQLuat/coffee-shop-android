package vn.edu.hcmuaf.fit.ttltmobile.ui.admin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hcmuaf.fit.ttltmobile.R
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

            // Status
            val (statusLabel, textColor, bgRes) = when {
                user.locked == true -> Triple(
                    "Đã khóa",
                    Color.parseColor("#D32F2F"),
                    R.drawable.bg_status_locked
                )
                user.enabled == false -> Triple(
                    "Chưa xác thực",
                    Color.parseColor("#F57C00"),
                    R.drawable.bg_status_unverified
                )
                else -> Triple(
                    "Hoạt động",
                    Color.parseColor("#388E3C"),
                    R.drawable.bg_status_active
                )
            }
            tvUserStatus.text = statusLabel
            tvUserStatus.setTextColor(textColor)
            tvUserStatus.setBackgroundResource(bgRes)

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