package vn.edu.hcmuaf.fit.ttltmobile.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.Product
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ViewholderPopularBinding
import vn.edu.hcmuaf.fit.ttltmobile.ui.productDetail.DetailActivity

class PopularAdapter(private val items: MutableList<Product>) :
    RecyclerView.Adapter<PopularAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderPopularBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding = ViewholderPopularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        // 1. Hiển thị Tên sản phẩm
        holder.binding.titleTxt.text = item.name

        // 2. Hiển thị Giá (Định dạng có dấu chấm phân cách, ví dụ: 50.000đ)
        holder.binding.priceTxt.text = String.format("%,.0fđ", item.price)

        // 3. Sử dụng extraTxt để hiển thị Rating
        // Giúp khách hàng thấy ngay đánh giá mà không cần bấm vào xem
        holder.binding.extraTxt.text = "${item.description}"

        // 4. Load ảnh bằng Glide
        // Thêm placeholder để tránh bị trắng hình khi mạng chậm
        Glide.with(context)
            .load(item.imageUrl)
            .placeholder(android.R.drawable.progress_horizontal)
            .error(android.R.drawable.stat_notify_error)
            .into(holder.binding.pic)

        // 5. Sự kiện Click sang màn hình Chi tiết
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            // Lưu ý: Object 'item' (Product) phải được implement Serializable
            // để truyền dữ liệu đi không bị crash
            intent.putExtra("object", item)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}