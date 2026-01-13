package vn.edu.hcmuaf.fit.ttltmobile.ui.order

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ItemOrderHistoryBinding
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.OrderHistoryResponse
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.OrderStatus
import java.text.SimpleDateFormat
import java.util.*

class OrderHistoryAdapter(
    private val onItemClick: (OrderHistoryResponse) -> Unit
) : ListAdapter<OrderHistoryResponse, OrderHistoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: ItemOrderHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = getItem(position)

        with(holder.binding) {
            tvOrderCode.text = "Mã đơn: ${order.orderCode}"
            tvOrderDate.text = formatDate(order.createdAt)
            tvOrderAmount.text = "₫${String.format("%,d", order.totalAmount.toLong())}"
            tvItemCount.text = "${order.itemCount} sản phẩm"

            // Status
            val statusText = order.statusDisplay ?: getStatusText(order.status)
            tvOrderStatus.text = statusText

            // Status color
            val statusColor = when (order.status) {
                OrderStatus.PENDING -> R.color.holo_orange_dark
                OrderStatus.CONFIRMED -> R.color.holo_blue_dark
                OrderStatus.PREPARING -> R.color.holo_purple
                OrderStatus.SHIPPING -> R.color.holo_blue_light
                OrderStatus.DELIVERED -> R.color.holo_green_dark
                OrderStatus.CANCELLED -> R.color.holo_red_dark
                OrderStatus.REFUNDED -> R.color.holo_orange_light
            }
            tvOrderStatus.setTextColor(holder.itemView.context.getColor(statusColor))
        }
    }

    private fun getStatusText(status: OrderStatus): String {
        return when (status) {
            OrderStatus.PENDING -> "Đang chờ"
            OrderStatus.CONFIRMED -> "Đã xác nhận"
            OrderStatus.PREPARING -> "Đang chuẩn bị"
            OrderStatus.SHIPPING -> "Đang giao"
            OrderStatus.DELIVERED -> "Hoàn thành"
            OrderStatus.CANCELLED -> "Đã hủy"
            OrderStatus.REFUNDED -> "Đã hoàn tiền"
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<OrderHistoryResponse>() {
        override fun areItemsTheSame(old: OrderHistoryResponse, new: OrderHistoryResponse): Boolean {
            return old.id == new.id
        }

        override fun areContentsTheSame(old: OrderHistoryResponse, new: OrderHistoryResponse): Boolean {
            return old == new
        }
    }
}