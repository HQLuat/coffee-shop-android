package vn.edu.hcmuaf.fit.ttltmobile.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.Product
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.ProductConstants
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ViewholderAdminProductBinding

class AdminProductAdapter(
    private var items: List<Product>,
    private val onItemClick: (Product) -> Unit,
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderAdminProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ViewholderAdminProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvName.text = item.name
            tvPrice.text = String.format("%,.0fđ", item.price)
            val categoryLabel = ProductConstants.getCategoryLabel(item.category)
            val sizeDisplay = item.size
            tvCategory.text = "$categoryLabel - Size $sizeDisplay"
            Glide.with(root.context).load(item.imageUrl).into(ivProduct)

            root.setOnClickListener { onItemClick(item) }
            btnEdit.setOnClickListener { onEdit(item) }
            btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<Product>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}