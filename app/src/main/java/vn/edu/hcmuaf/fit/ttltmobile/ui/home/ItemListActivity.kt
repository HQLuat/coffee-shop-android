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
    private var id: String = ""
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

    private fun initList() {
        binding.apply {
            progressBar.visibility = View.VISIBLE

            viewModel.getPopular().observe(this@ItemListActivity, Observer { items ->
                if (items != null) {
                    listView.layoutManager = GridLayoutManager(this@ItemListActivity, 2)
                    listView.adapter = PopularAdapter(items)
                }
                progressBar.visibility = View.GONE
            })

            backBtn.setOnClickListener { finish() }
        }
    }

    private fun getBundle() {
        id = intent.getStringExtra("id") ?: ""
        title = intent.getStringExtra("title") ?: "Sản phẩm"

        binding.categoryTxt.text = title
    }
}