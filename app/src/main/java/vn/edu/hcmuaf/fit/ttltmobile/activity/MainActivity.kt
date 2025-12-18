package vn.edu.hcmuaf.fit.ttltmobile.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import vn.edu.hcmuaf.fit.ttltmobile.adapter.CategoryAdapter
import vn.edu.hcmuaf.fit.ttltmobile.adapter.PopularAdapter
import vn.edu.hcmuaf.fit.ttltmobile.adapter.SpecialAdapter
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityMainBinding
import vn.edu.hcmuaf.fit.ttltmobile.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val viewModel = MainViewModel(

    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCategory()
        initPopular()
        initSpecial()
        initBottomMenu()
    }

    private fun initBottomMenu() {
        binding.cartBtn.setOnClickListener { startActivity(Intent(this, CartActivity::class.java)) }
    }

    private fun initSpecial() {
        binding.progressBarSpecial.visibility = View.VISIBLE
        viewModel.loadSpecial().observeForever {
            binding.recyclerViewSpecial.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.progressBarSpecial.visibility = View.GONE
            binding.recyclerViewSpecial.adapter = SpecialAdapter(it)
        }
        viewModel.loadSpecial()
    }

    private fun initPopular() {
        binding.progressBarPopular.visibility = View.VISIBLE
        viewModel.loadPopular().observeForever {
            binding.recyclerViewPopular.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.progressBarPopular.visibility = View.GONE
            binding.recyclerViewPopular.adapter = PopularAdapter(it)
        }
        viewModel.loadPopular()
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE
        viewModel.loadCategory().observeForever {
            binding.recyclerViewCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.progressBarCategory.visibility = View.GONE
            binding.recyclerViewCategory.adapter = CategoryAdapter(it)
        }
        viewModel.loadCategory()
    }
}