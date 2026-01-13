package vn.edu.hcmuaf.fit.ttltmobile.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ItemRefundBinding
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.RefundHistoryResponse
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.RefundStatus
import java.text.SimpleDateFormat
import java.util.*
class RefundAdapter : ListAdapter<RefundHistoryResponse, RefundAdapter.ViewHolder>(RefundDiffCallback()) {

    inner class ViewHolder(val binding: ItemRefundBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRefundBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val refund = getItem(position)

        with(holder.binding) {
            tvRefundId.text = "Mã hoàn tiền: ${refund.refundId}"
            tvRefundAmount.text = "₫${String.format("%,d", refund.refundAmount.toLong())}"
            tvRefundStatus.text = refund.status.getDisplayName()
            tvRefundDate.text = formatDate(refund.createdAt)
            tvRefundDescription.text = refund.description ?: "Hoàn tiền đơn hàng"

            val statusColor = when (refund.status) {
                RefundStatus.REFUNDED -> android.R.color.holo_green_dark
                RefundStatus.REFUND_PENDING,
                RefundStatus.REFUND_PROCESSING -> android.R.color.holo_orange_dark
                RefundStatus.REFUND_FAILED -> android.R.color.holo_red_dark
            }
            tvRefundStatus.setTextColor(holder.itemView.context.getColor(statusColor))
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

    class RefundDiffCallback : DiffUtil.ItemCallback<RefundHistoryResponse>() {
        override fun areItemsTheSame(oldItem: RefundHistoryResponse, newItem: RefundHistoryResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RefundHistoryResponse, newItem: RefundHistoryResponse): Boolean {
            return oldItem == newItem
        }
    }
}