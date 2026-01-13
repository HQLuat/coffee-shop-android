package vn.edu.hcmuaf.fit.ttltmobile.ui.admin

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.Product
import vn.edu.hcmuaf.fit.ttltmobile.data.repository.AdminProductRepository
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityAdminProductDetailBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity

class AdminProductDetailActivity : BaseActivity<ActivityAdminProductDetailBinding>() {

    private val adminRepo by lazy { AdminProductRepository(this) }
    private var product: Product? = null
    private var productId: Long = -1

    override fun getViewBinding() = ActivityAdminProductDetailBinding.inflate(layoutInflater)

    override fun createView() {
        product = intent.getSerializableExtra("DATA") as? Product

        if (product == null) {
            showToast("Không tìm thấy thông tin sản phẩm")
            finish()
            return
        }

        productId = product!!.id
        setupUI()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        if (productId != -1L) {
            refreshProductData()
        }
    }

    private fun refreshProductData() {
        adminRepo.getProductById(productId).observe(this) { updatedProduct ->
            if (updatedProduct != null) {
                this.product = updatedProduct
                setupUI()
            }
        }
    }

    private fun setupUI() {
        product?.let { p ->
            binding.apply {
                tvDetailName.text = p.name
                tvDetailPrice.text = String.format("%,.0fđ", p.price)
                tvDetailDescription.text = p.description
                tvDetailSize.text = "Size ${p.size}"

                Glide.with(this@AdminProductDetailActivity)
                    .load(p.imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ivProductDetail)
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnEditProduct.setOnClickListener {
            val intent = Intent(this, AdminProductFormActivity::class.java)
            intent.putExtra("DATA", product)
            startActivity(intent)
        }

        binding.btnDeleteProduct.setOnClickListener {
            product?.let { showDeleteConfirmDialog(it) }
        }
    }

    private fun showDeleteConfirmDialog(p: Product) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sản phẩm '${p.name}' không?")
            .setPositiveButton("Xóa") { _, _ -> performDelete(p.id) }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun performDelete(productId: Long) {
        showLoading()
        adminRepo.deleteProduct(productId).observe(this) { success ->
            hideLoading()
            if (success) {
                showToast("Đã xóa sản phẩm thành công")
                finish()
            } else {
                showToast("Xóa thất bại! Kiểm tra lại đơn hàng liên quan.")
            }
        }
    }
}