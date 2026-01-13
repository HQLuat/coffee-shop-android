package vn.edu.hcmuaf.fit.ttltmobile.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hcmuaf.fit.ttltmobile.data.model.admin.AdminMenuItem
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ItemAdminMenuBinding

class AdminMenuAdapter(
    private val items: List<AdminMenuItem>
) : RecyclerView.Adapter<AdminMenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(val binding: ItemAdminMenuBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemAdminMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            ivMenuIcon.setImageResource(item.icon)
            tvMenuTitle.text = item.title
            tvMenuSubtitle.text = item.subtitle

            root.setOnClickListener {
                item.action.invoke()
            }
        }
    }

    override fun getItemCount(): Int = items.size
}