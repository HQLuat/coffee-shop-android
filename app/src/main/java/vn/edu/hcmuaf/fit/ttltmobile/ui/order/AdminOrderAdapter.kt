package vn.edu.hcmuaf.fit.ttltmobile.ui.admin.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hcmuaf.fit.ttltmobile.R
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ItemAdminOrderBinding
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.OrderResponse
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.OrderStatus
import java.text.SimpleDateFormat
import java.util.*

class AdminOrderAdapter(
    private val onOrderClick: (OrderResponse) -> Unit,
    private val onUpdateStatus: (OrderResponse) -> Unit,
    private val onDeleteOrder: (OrderResponse) -> Unit
) : ListAdapter<OrderResponse, AdminOrderAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: ItemAdminOrderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = getItem(position)

        with(holder.binding) {
            tvOrderCode.text = order.orderCode
            tvCustomerEmail.text = order.getDisplayEmail()
            tvOrderDate.text = formatDate(order.createdAt)
            tvOrderAmount.text = order.getTotalFormatted()
            tvItemCount.text = "${order.items.size} sản phẩm"

            // Status
            tvOrderStatus.text = order.statusDisplay ?: getStatusText(order.status)
            tvOrderStatus.setTextColor(holder.itemView.context.getColor(getStatusColor(order.status)))

            // Payment status
            val paymentStatusText = when (order.status) {
                OrderStatus.REFUNDED -> "Đã hoàn tiền"
                OrderStatus.REFUND_PENDING -> "Chờ hoàn tiền"
                OrderStatus.REFUND_PROCESSING -> "Đang hoàn tiền"
                OrderStatus.REFUND_FAILED -> "Hoàn tiền thất bại"
                OrderStatus.PENDING -> "Chưa thanh toán"
                OrderStatus.CANCELLED -> "Đã hủy"
                else -> "Đã thanh toán"
            }
            tvPaymentStatus.text = paymentStatusText

            val paymentColor = when (order.status) {
                OrderStatus.REFUNDED -> android.R.color.holo_green_light
                OrderStatus.REFUND_PENDING,
                OrderStatus.REFUND_PROCESSING -> android.R.color.holo_orange_dark
                OrderStatus.REFUND_FAILED -> android.R.color.holo_red_dark
                OrderStatus.PENDING -> android.R.color.holo_red_dark
                OrderStatus.CANCELLED -> android.R.color.darker_gray
                else -> android.R.color.holo_green_dark
            }
            tvPaymentStatus.setTextColor(holder.itemView.context.getColor(paymentColor))

            // Click listeners
            root.setOnClickListener { onOrderClick(order) }
            btnUpdateStatus.setOnClickListener { onUpdateStatus(order) }
            btnDelete.setOnClickListener { onDeleteOrder(order) }
        }
    }

    private fun getStatusText(status: OrderStatus): String {
        return when (status) {
            OrderStatus.PENDING -> "Đang chờ"
            OrderStatus.CONFIRMED -> "Đã xác nhận"
            OrderStatus.PREPARING -> "Đang chuẩn bị"
            OrderStatus.SHIPPING -> "Đang giao"
            OrderStatus.DELIVERED -> "Đã giao"
            OrderStatus.CANCELLED -> "Đã hủy"
            OrderStatus.REFUNDED -> "Đã hoàn tiền"
            OrderStatus.REFUND_PENDING -> "Chờ hoàn tiền"
            OrderStatus.REFUND_PROCESSING -> "Đang hoàn tiền"
            OrderStatus.REFUND_FAILED -> "Hoàn tiền thất bại"
        }
    }

    private fun getStatusColor(status: OrderStatus): Int {
        return when (status) {
            OrderStatus.PENDING -> R.color.orange
            OrderStatus.CONFIRMED -> R.color.blue
            OrderStatus.PREPARING -> R.color.purple
            OrderStatus.SHIPPING -> R.color.teal
            OrderStatus.DELIVERED -> R.color.green
            OrderStatus.CANCELLED -> android.R.color.holo_red_dark
            OrderStatus.REFUNDED -> android.R.color.holo_green_light
            OrderStatus.REFUND_PENDING -> R.color.orange
            OrderStatus.REFUND_PROCESSING -> R.color.purple
            OrderStatus.REFUND_FAILED -> android.R.color.holo_red_dark
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

    class DiffCallback : DiffUtil.ItemCallback<OrderResponse>() {
        override fun areItemsTheSame(oldItem: OrderResponse, newItem: OrderResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderResponse, newItem: OrderResponse): Boolean {
            return oldItem == newItem
        }
    }
}