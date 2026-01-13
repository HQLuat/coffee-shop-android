package vn.edu.hcmuaf.fit.ttltmobile.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.edu.hcmuaf.fit.ttltmobile.ui.productDetail.DetailActivity
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ViewholderSpecialBinding
import vn.edu.hcmuaf.fit.ttltmobile.data.model.ItemModel

class SpecialAdapter(private val items: MutableList<ItemModel>) :
    RecyclerView.Adapter<SpecialAdapter.Viewholder>() {

    // Khối init chạy ngay khi Adapter được tạo
    init {
        items.shuffle() // Xáo trộn danh sách ngẫu nhiên ngay từ đầu
    }

    class Viewholder(val binding: ViewholderSpecialBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        // Lấy context trực tiếp từ parent để an toàn hơn lateinit
        val binding = ViewholderSpecialBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.binding.apply {
            titleTxt.text = item.title
            priceTxt.text = "$${item.price}"
            ratingBar.rating = item.rating.toFloat()

            Glide.with(context)
                .load(item.picUrl[0])
                .into(picMain)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("object", item)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    // Hàm này dùng nếu bạn muốn xáo trộn lại danh sách thủ công (ví dụ khi vuốt để làm mới)
    fun randomizeItems() {
        items.shuffle()
        notifyDataSetChanged()
    }
}