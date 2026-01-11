package vn.edu.hcmuaf.fit.ttltmobile.ui.productDetail

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import vn.edu.hcmuaf.fit.ttltmobile.R
import vn.edu.hcmuaf.fit.ttltmobile.data.model.ReviewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ReviewAdapter(
    private val reviews: MutableList<ReviewModel>,
    private val currentUserId: Long,
    private val onEditClick: (ReviewModel) -> Unit,
    private val onDeleteClick: (ReviewModel) -> Unit,
    private val onSaveEdit: (ReviewModel, Int, String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var editingPosition: Int = -1

    companion object {
        const val VIEW_TYPE_NORMAL = 0
        const val VIEW_TYPE_EDITING = 1
    }

    inner class NormalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ShapeableImageView = itemView.findViewById(R.id.imgAvatar)
        val txtReviewerName: TextView = itemView.findViewById(R.id.txtReviewerName)
        val ratingStarContainer: LinearLayout = itemView.findViewById(R.id.ratingStarContainer)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtComment: TextView = itemView.findViewById(R.id.txtComment)
        val actionButtonsLayout: LinearLayout = itemView.findViewById(R.id.actionButtonsLayout)
        val btnEditReview: ImageButton = itemView.findViewById(R.id.btnEditReview)
        val btnDeleteReview: ImageButton = itemView.findViewById(R.id.btnDeleteReview)
    }

    inner class EditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val editStarsContainer: LinearLayout = itemView.findViewById(R.id.editStarsContainer)
        val edtEditComment: EditText = itemView.findViewById(R.id.edtEditComment)
        val btnCancelEdit: Button = itemView.findViewById(R.id.btnCancelEdit)
        val btnSaveEdit: Button = itemView.findViewById(R.id.btnSaveEdit)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == editingPosition) VIEW_TYPE_EDITING else VIEW_TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_EDITING) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_review_edit, parent, false)
            EditViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_review, parent, false)
            NormalViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val review = reviews[position]

        if (holder is NormalViewHolder) {
            bindNormalView(holder, review, position)
        } else if (holder is EditViewHolder) {
            bindEditView(holder, review, position)
        }
    }

    private fun bindNormalView(holder: NormalViewHolder, review: ReviewModel, position: Int) {
        val context = holder.itemView.context

        holder.txtReviewerName.text = review.reviewerName ?: "Khách hàng"
        holder.txtComment.text = review.comment.trim()

        // Hiển thị sao
        holder.ratingStarContainer.removeAllViews()
        for (i in 1..5) {
            val star = ImageView(context).apply {
                val size = dpToPx(20, context)
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = dpToPx(4, context)
                }
                setImageResource(R.drawable.star)
                alpha = if (i <= review.rating) 1.0f else 0.3f
                setColorFilter(Color.parseColor("#FFD700"))
            }
            holder.ratingStarContainer.addView(star)
        }

        // Format ngày
        try {
            val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("vi", "VN"))
            val dateTime = LocalDateTime.parse(review.createdAt, inputFormat)
            holder.txtDate.text = dateTime.format(outputFormat)
        } catch (e: Exception) {
            holder.txtDate.text = review.createdAt.substring(0, 10)
        }

        // Hiển thị nút sửa/xóa
        if (review.userId == currentUserId) {
            holder.actionButtonsLayout.visibility = View.VISIBLE

            holder.btnEditReview.setOnClickListener {
                editingPosition = position
                notifyItemChanged(position)
            }

            holder.btnDeleteReview.setOnClickListener {
                onDeleteClick(review)
            }
        } else {
            holder.actionButtonsLayout.visibility = View.GONE
        }
    }

    private fun bindEditView(holder: EditViewHolder, review: ReviewModel, position: Int) {
        val context = holder.itemView.context
        var editRating = review.rating

        // Fill dữ liệu
        holder.edtEditComment.setText(review.comment)

        // Tạo sao
        holder.editStarsContainer.removeAllViews()
        val stars = mutableListOf<ImageView>()
        for (i in 1..5) {
            val star = ImageView(context).apply {
                val size = dpToPx(30, context)
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = dpToPx(4, context)
                }
                setImageResource(R.drawable.star)
                alpha = if (i <= editRating) 1.0f else 0.3f
                setColorFilter(Color.parseColor("#FFD700"))

                setOnClickListener {
                    editRating = i
                    updateStars(stars, editRating)
                }
            }
            stars.add(star)
            holder.editStarsContainer.addView(star)
        }

        // Nút Hủy
        holder.btnCancelEdit.setOnClickListener {
            editingPosition = -1
            notifyItemChanged(position)
        }

        // Nút Lưu
        holder.btnSaveEdit.setOnClickListener {
            val newComment = holder.edtEditComment.text.toString().trim()
            if (newComment.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onSaveEdit(review, editRating, newComment)
            editingPosition = -1
        }
    }

    private fun updateStars(stars: List<ImageView>, rating: Int) {
        stars.forEachIndexed { index, star ->
            star.alpha = if (index < rating) 1.0f else 0.3f
        }
    }

    override fun getItemCount(): Int = reviews.size

    private fun dpToPx(dp: Int, context: android.content.Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}