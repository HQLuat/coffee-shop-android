package vn.edu.hcmuaf.fit.ttltmobile.ui.home

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider // Thêm import này
import androidx.recyclerview.widget.GridLayoutManager
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityItemListBinding
import vn.edu.hcmuaf.fit.ttltmobile.ui.home.PopularAdapter

class ItemListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemListBinding
    private lateinit var viewModel: MainViewModel

    // Đổi 'id' thành 'categoryEnum' cho đúng bản chất dữ liệu
    private var categoryEnum: String = ""
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        getBundle()
        initList()
    }

    private fun getBundle() {
        // Lấy 'enum' (ví dụ: COFFEE) mà CategoryAdapter đã truyền qua
        categoryEnum = intent.getStringExtra("enum") ?: ""
        title = intent.getStringExtra("title") ?: "Sản phẩm"

        binding.categoryTxt.text = title
    }

    private fun initList() {
        binding.apply {
            progressBar.visibility = View.VISIBLE

            // GỌI HÀM NÀY ĐỂ LỌC THEO CATEGORY
            viewModel.getByCategory(categoryEnum).observe(this@ItemListActivity) { items ->
                progressBar.visibility = View.GONE
                if (items != null) {
                    listView.layoutManager = GridLayoutManager(this@ItemListActivity, 2)
                    listView.adapter = PopularAdapter(items)
                }
            }

            backBtn.setOnClickListener { finish() }
        }
    }
}