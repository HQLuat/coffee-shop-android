package vn.edu.hcmuaf.fit.ttltmobile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ViewholderCartBinding
import vn.edu.hcmuaf.fit.ttltmobile.domain.ItemModel
import vn.edu.hcmuaf.fit.ttltmobile.helper.ChangeNumberItemsListener
import vn.edu.hcmuaf.fit.ttltmobile.helper.ManagmentCart

class CartAdapter (private val listItemSelected: ArrayList<ItemModel>,
    context: Context,
    val changeNumberItemsListener: ChangeNumberItemsListener?=null):
RecyclerView.Adapter<CartAdapter.Viewholder>(){
    class Viewholder(val binding: ViewholderCartBinding):
        RecyclerView.ViewHolder(binding.root)

    private  val managmentCart = ManagmentCart(context)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartAdapter.Viewholder {
        val binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: CartAdapter.Viewholder, position: Int) {
        val item = listItemSelected[position]

        holder.binding.titleTxt.text = item.title
        holder.binding.feeEachitem.text = "$${item.price}"
        holder.binding.totalEachItem.text = "$${Math.round(item.numberInCart * item.price)}"
        holder.binding.numberItemTxt.text = item.numberInCart.toString()

        Glide.with(holder.itemView.context)
            .load(item.picUrl[0])
            .apply(RequestOptions().transform(CenterCrop()))
            .into(holder.binding.picCart)

        holder.binding.plusEachItem.setOnClickListener {
            managmentCart.plusItem(listItemSelected, position, object : ChangeNumberItemsListener{
                override fun onChanged() {
                   notifyDataSetChanged()
                    changeNumberItemsListener?.onChanged()
                }
            })
        }

        holder.binding.minusEachItem.setOnClickListener {
            managmentCart.minusItem(listItemSelected, position, object : ChangeNumberItemsListener{
                override fun onChanged() {
                    notifyDataSetChanged()
                    changeNumberItemsListener?.onChanged()
                }
            })
        }

        holder.binding.removeItemBtn.setOnClickListener {
            managmentCart.romoveItem(
                listItemSelected,
                position,
                object  : ChangeNumberItemsListener {
                    override fun onChanged() {
                        notifyDataSetChanged()
                        changeNumberItemsListener?.onChanged()
                    }

                }
            )
        }
    }

    override fun getItemCount(): Int = listItemSelected.size
}