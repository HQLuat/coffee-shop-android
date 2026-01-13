package vn.edu.hcmuaf.fit.ttltmobile.ui.home

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hcmuaf.fit.ttltmobile.R
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ViewholderCategoryBinding
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.ProductConstants

class CategoryAdapter(private val items: Array<String>) :
    RecyclerView.Adapter<CategoryAdapter.Viewholder>() {

    private var selectedPosition = -1

    class Viewholder(val binding: ViewholderCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding = ViewholderCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val label = items[position] // Đây là "Cà phê", "Trà"...

        holder.binding.titleCat.text = label

        holder.binding.root.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                val lastSelected = selectedPosition
                selectedPosition = currentPos
                notifyItemChanged(lastSelected)
                notifyItemChanged(selectedPosition)

                Handler(Looper.getMainLooper()).postDelayed({
                    val context = holder.itemView.context
                    val intent = Intent(context, ItemListActivity::class.java).apply {
                        // Lấy Label gửi đi để hiện tiêu đề, lấy Enum để lọc DB
                        val selectedLabel = items[currentPos]
                        val selectedEnum = ProductConstants.getCategoryEnum(selectedLabel)

                        putExtra("title", selectedLabel)
                        putExtra("enum", selectedEnum)
                    }
                    context.startActivity(intent)
                }, 200)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}