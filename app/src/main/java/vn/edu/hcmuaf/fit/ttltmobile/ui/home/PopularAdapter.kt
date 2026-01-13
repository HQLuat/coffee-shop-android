package vn.edu.hcmuaf.fit.ttltmobile.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.edu.hcmuaf.fit.ttltmobile.ui.productDetail.DetailActivity
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ViewholderPopularBinding
import vn.edu.hcmuaf.fit.ttltmobile.data.model.ItemModel

class PopularAdapter(private val items: MutableList<ItemModel>) :
    RecyclerView.Adapter<PopularAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderPopularBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding = ViewholderPopularBinding.inflate(
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
            extraTxt.text = item.extra
            priceTxt.text = "$${item.price}"

            Glide.with(context)
                .load(item.picUrl[0])
                .into(pic)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("object", item)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

}