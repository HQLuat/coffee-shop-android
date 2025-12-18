package vn.edu.hcmuaf.fit.ttltmobile.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.edu.hcmuaf.fit.ttltmobile.activity.DetailActivity
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ViewholderPopularBinding
import vn.edu.hcmuaf.fit.ttltmobile.domain.ItemModel

class PopularAdapter(val items: MutableList<ItemModel>):
    RecyclerView.Adapter<PopularAdapter.Viewholder>() {
        lateinit var context: Context

    class Viewholder (val binding: ViewholderPopularBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularAdapter.Viewholder {
        context = parent.context
        val binding = ViewholderPopularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: PopularAdapter.Viewholder, position: Int) {
        holder.binding.titleTxt.text = items[position].title
        holder.binding.extraTxt.text = items[position].extra
        holder.binding.priceTxt.text = "$" + items[position].price.toString()

        Glide.with(context)
            .load(items[position].picUrl[0])
            .into(holder.binding.pic)

        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, DetailActivity::class.java).apply {
                    putExtra("object", items[position])
                }
            )
        }
    }

    override fun getItemCount(): Int = items.size
}