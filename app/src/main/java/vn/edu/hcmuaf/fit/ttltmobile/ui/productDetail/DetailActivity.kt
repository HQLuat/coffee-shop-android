package vn.edu.hcmuaf.fit.ttltmobile.ui.productDetail

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import eightbitlab.com.blurview.RenderScriptBlur
import vn.edu.hcmuaf.fit.ttltmobile.R
import vn.edu.hcmuaf.fit.ttltmobile.data.model.ItemModel
import vn.edu.hcmuaf.fit.ttltmobile.data.model.ReviewModel
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.Product
import vn.edu.hcmuaf.fit.ttltmobile.data.repository.MainRepository
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityDetailBinding
import vn.edu.hcmuaf.fit.ttltmobile.ui.home.SizeAdapter
import vn.edu.hcmuaf.fit.ttltmobile.utils.ManagmentCart
import vn.edu.hcmuaf.fit.ttltmobile.utils.TokenManager
import vn.edu.hcmuaf.fit.ttltmobile.ui.auth.LoginActivity
import java.text.NumberFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemModel
    private lateinit var repository: MainRepository
    private lateinit var tokenManager: TokenManager
    private var selectedRating: Int = 0
    private var hasUserReviewed: Boolean = false

    // THAY ĐỔI: Lưu danh sách variants
    private var productVariants: List<Product> = emptyList()
    private var selectedVariant: Product? = null
    private var quantity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = MainRepository(this)
        tokenManager = TokenManager(this)

        if (tokenManager.isTokenExpired()) {
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show()
            tokenManager.clearTokens()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        item = intent.getSerializableExtra("object") as ItemModel

        loadProductVariants()  // THAY ĐỔI: Load variants trước
        setBlurEffect()
        initReviews()
        setupReviewForm()
        setupReviewsToggle()
        setupQuantityButtons()
    }

    // HÀM MỚI: Load tất cả variants của sản phẩm
    private fun loadProductVariants() {
        repository.loadProductVariants(item.id).observe(this, Observer { variants ->
            if (variants.isEmpty()) {
                Toast.makeText(this, "Không thể tải thông tin sản phẩm", Toast.LENGTH_SHORT).show()
                finish()
                return@Observer
            }

            productVariants = variants

            // Hiển thị thông tin chung (từ variant đầu tiên)
            val firstVariant = variants.first()
            binding.apply {
                titleTxt.text = firstVariant.name
                descriptionTxt.text = firstVariant.description
                extraTxt.text = firstVariant.category

                Glide.with(this@DetailActivity)
                    .load(firstVariant.imageUrl)
                    .into(picMain)
            }

            // Setup size list với các variants
            initSizeList()
        })
    }

    private fun initSizeList() {
        val allPossibleSizes = listOf("S", "M", "L")
        val availableSizes = productVariants.map { it.size.uppercase() }

        binding.sizeList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.sizeList.adapter = SizeAdapter(
            allPossibleSizes,
            availableSizes
        ) { size, isAvailable ->
            if (isAvailable) {
                // Tìm variant tương ứng với size đã chọn
                selectedVariant = productVariants.find { it.size.uppercase() == size.uppercase() }
                updatePriceForSelectedVariant()
                binding.addToCartBtn.isEnabled = true
                binding.addToCartBtn.alpha = 1.0f
            } else {
                selectedVariant = null
                binding.priceTxt.text = "Size $size không có sẵn"
                binding.priceTxt.setTextColor(Color.GRAY)
                binding.addToCartBtn.isEnabled = false
                binding.addToCartBtn.alpha = 0.5f
                Toast.makeText(this, "Size $size hiện không có sẵn", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupQuantityButtons() {
        binding.apply {
            numberItemTxt.text = quantity.toString()

            backBtn.setOnClickListener { finish() }

            plusCart.setOnClickListener {
                quantity++
                numberItemTxt.text = quantity.toString()
                updatePriceForSelectedVariant()
            }

            minusCart.setOnClickListener {
                if (quantity > 1) {
                    quantity--
                    numberItemTxt.text = quantity.toString()
                    updatePriceForSelectedVariant()
                }
            }

            addToCartBtn.setOnClickListener { addToCart() }
        }
    }

    private fun updatePriceForSelectedVariant() {
        if (selectedVariant != null) {
            val totalPrice = selectedVariant!!.price * quantity
            binding.priceTxt.text = formatPrice(totalPrice)
            binding.priceTxt.setTextColor(getColor(R.color.orange))
        }
    }

    private fun addToCart() {
        if (selectedVariant == null) {
            Toast.makeText(this, "Vui lòng chọn kích cỡ", Toast.LENGTH_SHORT).show()
            return
        }

        // Gửi ID của variant đã chọn (không phải ID sản phẩm gốc)
        repository.addToCart(selectedVariant!!.id, quantity).observe(this, Observer { result ->
            val (success, message) = result
            if (success) {
                Toast.makeText(this, "Đã thêm ${selectedVariant!!.size} vào giỏ hàng!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, message ?: "Thêm vào giỏ hàng thất bại", Toast.LENGTH_LONG).show()
            }
        })
    }

    // ... CÁC HÀM REVIEW GIỮ NGUYÊN ...

    private fun setupReviewsToggle() {
        var isExpanded = false
        binding.reviewsHeaderLayout.setOnClickListener {
            isExpanded = !isExpanded
            if (isExpanded) {
                binding.reviewsContainer.visibility = View.VISIBLE
                binding.imgExpandIcon.setImageResource(R.drawable.ic_expand_less)
            } else {
                binding.reviewsContainer.visibility = View.GONE
                binding.imgExpandIcon.setImageResource(R.drawable.ic_expand_more)
            }
        }
    }

    private fun setupReviewForm() {
        val stars = listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)
        stars.forEachIndexed { index, star ->
            star.setOnClickListener {
                selectedRating = index + 1
                updateStarSelection(stars, selectedRating)
            }
        }

        binding.submitReviewBtn.setOnClickListener {
            val comment = binding.reviewTextInput.text.toString().trim()
            if (selectedRating == 0) {
                Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (comment.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập nội dung bình luận", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            repository.postReview(item.id, selectedRating, comment).observe(this, Observer { result ->
                val (success, errorMessage) = result
                if (success) {
                    Toast.makeText(this, "Gửi đánh giá thành công!", Toast.LENGTH_SHORT).show()
                    binding.reviewFormCard.visibility = View.GONE
                    binding.thankYouCard.visibility = View.VISIBLE
                    hasUserReviewed = true
                    initReviews()
                } else {
                    if (errorMessage?.contains("đã đánh giá") == true) {
                        Toast.makeText(this, "Bạn đã đánh giá sản phẩm này rồi", Toast.LENGTH_LONG).show()
                        binding.reviewFormCard.visibility = View.GONE
                        binding.thankYouCard.visibility = View.VISIBLE
                        hasUserReviewed = true
                    } else {
                        Toast.makeText(this, errorMessage ?: "Gửi đánh giá thất bại", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }

    private fun updateStarSelection(stars: List<ImageView>, rating: Int) {
        stars.forEachIndexed { index, star ->
            star.alpha = if (index < rating) 1.0f else 0.3f
        }
    }

    private fun initReviews() {
        if (item.id == 0L) return
        val currentUserId = tokenManager.getUserId()

        repository.loadReviews(item.id).observe(this, Observer { reviews ->
            hasUserReviewed = reviews.any { it.userId == currentUserId }

            if (hasUserReviewed) {
                binding.reviewFormCard.visibility = View.GONE
                binding.thankYouCard.visibility = View.VISIBLE
            } else {
                binding.reviewFormCard.visibility = View.VISIBLE
                binding.thankYouCard.visibility = View.GONE
            }

            binding.txtReviewCount.text = "(${reviews.size})"

            if (reviews.isEmpty()) {
                binding.emptyReviewsLayout.visibility = View.VISIBLE
                binding.reviewsList.visibility = View.GONE
                binding.ratingTxt.text = "0.0"
                binding.totalRatingCountTxt.text = "(0 đánh giá)"
            } else {
                binding.emptyReviewsLayout.visibility = View.GONE
                binding.reviewsList.visibility = View.VISIBLE
                binding.reviewsList.layoutManager = LinearLayoutManager(this)
                binding.reviewsList.adapter = ReviewAdapter(
                    reviews.toMutableList(),
                    currentUserId,
                    onEditClick = {},
                    onDeleteClick = { review -> handleDeleteReview(review) },
                    onSaveEdit = { review, newRating, newComment ->
                        handleSaveEdit(review, newRating, newComment)
                    }
                )
                calculateAverage(reviews)
            }
        })
    }

    private fun handleSaveEdit(review: ReviewModel, newRating: Int, newComment: String) {
        repository.updateReview(review.id, newRating, newComment).observe(this, Observer { result ->
            val (success, errorMessage) = result
            if (success) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                initReviews()
            } else {
                Toast.makeText(this, errorMessage ?: "Cập nhật thất bại", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun handleDeleteReview(review: ReviewModel) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa đánh giá này?")
            .setPositiveButton("Xóa") { _, _ ->
                repository.deleteReview(review.id).observe(this, Observer { result ->
                    val (success, errorMessage) = result
                    if (success) {
                        Toast.makeText(this, "Xóa đánh giá thành công!", Toast.LENGTH_SHORT).show()
                        hasUserReviewed = false
                        binding.reviewFormCard.visibility = View.VISIBLE
                        binding.thankYouCard.visibility = View.GONE
                        initReviews()
                    } else {
                        Toast.makeText(this, errorMessage ?: "Xóa thất bại", Toast.LENGTH_LONG).show()
                    }
                })
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun calculateAverage(reviews: List<ReviewModel>) {
        if (reviews.isEmpty()) return
        val average = reviews.map { it.rating.toDouble() }.average()
        binding.ratingTxt.text = String.format("%.1f", average)
        binding.totalRatingCountTxt.text = "(${reviews.size} đánh giá)"

        binding.averageStarsContainer.removeAllViews()
        for (i in 1..5) {
            val star = ImageView(this)
            val params = LinearLayout.LayoutParams(20.dpToPx(), 20.dpToPx())
            params.marginStart = 4.dpToPx()
            star.layoutParams = params
            star.setImageResource(R.drawable.star)
            star.alpha = if (i <= average) 1.0f else 0.3f
            star.setColorFilter(Color.parseColor("#FFD700"))
            binding.averageStarsContainer.addView(star)
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    private fun setBlurEffect() {
        val radius = 10f
        val decorView = this.window.decorView
        val rootView = decorView.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background
        binding.blurView.setupWith(rootView, RenderScriptBlur(this))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(radius)
        binding.blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND)
        binding.blurView.setClipToOutline(true)
    }

    private fun formatPrice(price: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        return "${formatter.format(price)}đ"
    }
}