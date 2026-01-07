package vn.edu.hcmuaf.fit.ttltmobile.ui.cart
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.edu.hcmuaf.fit.ttltmobile.R
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ViewholderCartBinding
import vn.edu.hcmuaf.fit.ttltmobile.data.model.cart.CartItemResponse

class CartAdapter(
    private val items: MutableList<CartItemResponse>,
    private val onQuantityChange: (Long, Int, Boolean) -> Unit, // cartItemId, quantity, isIncrease
    private val onRemoveItem: (Long) -> Unit // cartItemId
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ViewholderCartBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ViewholderCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            titleTxt.text = item.productName

            feeEachitem.text = item.getPriceFormatted()

            numberItemTxt.text = item.quantity.toString()

            totalEachItem.text = item.getSubtotalFormatted()

//            Glide.with(holder.itemView.context)
//                .load(item.imageUrl)
//                .placeholder(R.drawable.coffee)
//                .error(R.drawable.coffee)
//                .into(picCart)

            plusEachItem.setOnClickListener {
                onQuantityChange(item.id, item.quantity, true)
            }

            minusEachItem.setOnClickListener {
                if (item.quantity > 1) {
                    onQuantityChange(item.id, item.quantity, false)
                }
            }

            removeItemBtn.setOnClickListener {
                onRemoveItem(item.id)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<CartItemResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}