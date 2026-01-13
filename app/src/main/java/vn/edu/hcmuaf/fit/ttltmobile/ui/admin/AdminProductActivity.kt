package vn.edu.hcmuaf.fit.ttltmobile.ui.admin

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.Product
import vn.edu.hcmuaf.fit.ttltmobile.data.repository.AdminProductRepository
import vn.edu.hcmuaf.fit.ttltmobile.data.repository.MainRepository
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityAdminProductBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity

class AdminProductActivity : BaseActivity<ActivityAdminProductBinding>() {

    private val adminRepo by lazy { AdminProductRepository(this) }

    private lateinit var adapter: AdminProductAdapter

    override fun getViewBinding() = ActivityAdminProductBinding.inflate(layoutInflater)

    override fun createView() {
        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        loadProductList()
    }

    private fun setupRecyclerView() {
        binding.rvAdminProduct.layoutManager = LinearLayoutManager(this)
        adapter = AdminProductAdapter(
            items = emptyList(),
            onItemClick = { product ->
                val intent = Intent(this, AdminProductDetailActivity::class.java)
                intent.putExtra("DATA", product)
                startActivity(intent)
            },
            onEdit = { product -> goToEditScreen(product) },
            onDelete = { product -> showDeleteConfirmDialog(product) }
        )
        binding.rvAdminProduct.adapter = adapter
    }

    private fun goToDetailScreen(product: Product) {
        val intent = Intent(this, AdminProductDetailActivity::class.java)
        intent.putExtra("DATA", product)
        startActivity(intent)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnAddProduct.setOnClickListener {
            val intent = Intent(this, AdminProductFormActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadProductList() {
        showLoading()
        adminRepo.getAllProducts().observe(this) { list ->
            hideLoading()
            if (list != null) {
                adapter.updateData(list)
            } else {
                showToast("Không thể tải danh sách sản phẩm")
            }
        }
    }

    private fun goToEditScreen(product: Product) {
        val intent = Intent(this, AdminProductFormActivity::class.java)
        intent.putExtra("DATA", product)
        startActivity(intent)
    }

    private fun showDeleteConfirmDialog(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sản phẩm '${product.name}' không?")
            .setPositiveButton("Xóa") { _, _ ->
                performDelete(product.id)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun performDelete(productId: Long) {
        showLoading()
        adminRepo.deleteProduct(productId).observe(this) { success ->
            hideLoading()
            if (success) {
                showToast("Đã xóa sản phẩm thành công")
                loadProductList()
            } else {
                showToast("Xóa thất bại! Vui lòng thử lại")
            }
        }
    }
}