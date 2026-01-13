package vn.edu.hcmuaf.fit.ttltmobile.ui.home

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ViewholderSizeBinding
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import vn.edu.hcmuaf.fit.ttltmobile.R

class SizeAdapter(
    private val allPossibleSizes: List<String>,
    private val availableSizes: List<String>?, // Cho phép null để tránh crash
    private val onSizeSelected: (String, Boolean) -> Unit
) : RecyclerView.Adapter<SizeAdapter.Viewholder>() {

    inner class Viewholder(val binding: ViewholderSizeBinding) : RecyclerView.ViewHolder(binding.root)
    private var selectedPosition = -1
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        context = parent.context
        val binding = ViewholderSizeBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val sizeValue = allPossibleSizes[position].trim().uppercase()

        // CHUẨN HÓA DANH SÁCH TỪ DB: Xóa khoảng trắng và viết hoa hết
        val normalizedDbSizes = availableSizes?.map { it.trim().uppercase() } ?: emptyList()

        // KIỂM TRA: Size này có thực sự nằm trong danh sách DB trả về không
        val isExistInDb = normalizedDbSizes.contains(sizeValue)

        // UI Size minh họa
        val imageSize = when (position) {
            0 -> 45.dpToPx(context)
            1 -> 55.dpToPx(context)
            2 -> 65.dpToPx(context)
            else -> 70.dpToPx(context)
        }
        holder.binding.img.layoutParams.width = imageSize
        holder.binding.img.layoutParams.height = imageSize

        if (!isExistInDb) {
            // TRẠNG THÁI HẾT HÀNG
            holder.binding.root.alpha = 0.2f // Mờ hơn để phân biệt rõ
            holder.binding.img.setBackgroundResource(R.drawable.stroke_bg)
            holder.binding.root.setOnClickListener {
                onSizeSelected(sizeValue, false)
            }
        } else {
            // TRẠNG THÁI CÒN HÀNG
            holder.binding.root.alpha = 1.0f
            holder.binding.root.setOnClickListener {
                val oldPos = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(oldPos)
                notifyItemChanged(selectedPosition)
                onSizeSelected(sizeValue, true)
            }

            if (selectedPosition == position) {
                holder.binding.img.setBackgroundResource(R.drawable.orange_bg)
            } else {
                holder.binding.img.setBackgroundResource(R.drawable.stroke_bg)
            }
        }
    }

    override fun getItemCount(): Int = allPossibleSizes.size
    private fun Int.dpToPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()
}