package vn.edu.hcmuaf.fit.ttltmobile.ui.admin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hcmuaf.fit.ttltmobile.R
import vn.edu.hcmuaf.fit.ttltmobile.data.model.admin.AdminReviewResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AdminReviewAdapter(
    private val reviews: List<AdminReviewResponse>,
    private val onDeleteClick: (AdminReviewResponse) -> Unit
) : RecyclerView.Adapter<AdminReviewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUserName: TextView = itemView.findViewById(R.id.txtUserName)
        val txtUserEmail: TextView = itemView.findViewById(R.id.txtUserEmail)
        val txtProductName: TextView = itemView.findViewById(R.id.txtProductName)
        val txtComment: TextView = itemView.findViewById(R.id.txtComment)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val ratingStarContainer: LinearLayout = itemView.findViewById(R.id.ratingStarContainer)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]
        val context = holder.itemView.context

        holder.txtUserName.text = review.userName
        holder.txtUserEmail.text = review.userEmail
        holder.txtProductName.text = "Sản phẩm: ${review.productName}"
        holder.txtComment.text = review.comment

        // Hiển thị sao
        holder.ratingStarContainer.removeAllViews()
        for (i in 1..5) {
            val star = ImageView(context).apply {
                val size = dpToPx(18, context)
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = dpToPx(3, context)
                }
                setImageResource(R.drawable.star)
                alpha = if (i <= review.rating) 1.0f else 0.3f
                setColorFilter(Color.parseColor("#FFD700"))
            }
            holder.ratingStarContainer.addView(star)
        }

        // Format ngày
        try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale("vi", "VN"))
            val dateTime = LocalDateTime.parse(review.createdAt.toString())
            holder.txtDate.text = dateTime.format(formatter)
        } catch (e: Exception) {
            holder.txtDate.text = review.createdAt.toString().substring(0, 16)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(review)
        }
    }

    override fun getItemCount(): Int = reviews.size

    private fun dpToPx(dp: Int, context: android.content.Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}