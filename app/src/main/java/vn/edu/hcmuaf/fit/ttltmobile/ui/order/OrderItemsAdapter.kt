package vn.edu.hcmuaf.fit.ttltmobile.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.edu.hcmuaf.fit.ttltmobile.R
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ItemOrderProductBinding
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.OrderItemResponse

// Order Items Adapter
class OrderItemsAdapter : ListAdapter<OrderItemResponse, OrderItemsAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: ItemOrderProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            tvProductName.text = item.productName
            tvProductSize.text = "Số lượng: ${item.quantity}"
            tvProductPrice.text = "₫${String.format("%,d", item.price.toLong())}"
            tvProductQuantity.text = "x${item.quantity}"
            tvProductSubtotal.text = "₫${String.format("%,d", item.subtotal.toLong())}"

            // Use placeholder image since server doesn't provide imageUrl
            Glide.with(holder.itemView.context)
                .load(R.drawable.coffee)
                .placeholder(R.drawable.coffee)
                .error(R.drawable.coffee)
                .into(ivProductImage)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<OrderItemResponse>() {
        override fun areItemsTheSame(oldItem: OrderItemResponse, newItem: OrderItemResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderItemResponse, newItem: OrderItemResponse): Boolean {
            return oldItem == newItem
        }
    }
}