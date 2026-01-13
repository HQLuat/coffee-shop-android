package vn.edu.hcmuaf.fit.ttltmobile.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.ProductConstants
import vn.edu.hcmuaf.fit.ttltmobile.databinding.FragmentHomeBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var viewModel: MainViewModel

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        initCategory()
        loadAllProductData()

        binding.seeAllPopular.setOnClickListener {
            val intent = Intent(requireContext(), ItemListActivity::class.java)
            intent.putExtra("title", "Popular Coffees")
            intent.putExtra("enum", "ALL")
        }

        binding.seeAllSpecial.setOnClickListener {
            val intent = Intent(requireContext(), ItemListActivity::class.java)
            intent.putExtra("title", "Special For You")
            intent.putExtra("enum", "ALL")
            startActivity(intent)
        }
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.GONE
        binding.recyclerViewCategory.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = CategoryAdapter(ProductConstants.categoryLabels)
            isNestedScrollingEnabled = false
        }
    }

    private fun loadAllProductData() {
        binding.progressBarPopular.visibility = View.VISIBLE
        binding.progressBarSpecial.visibility = View.VISIBLE

        viewModel.getPopular().observe(viewLifecycleOwner) { items ->
            binding.progressBarPopular.visibility = View.GONE
            binding.progressBarSpecial.visibility = View.GONE

            if (items != null && items.isNotEmpty()) {
                val popularList = items.sortedByDescending { it.rating }.toMutableList()
                setupPopularRecyclerView(popularList)

                val specialList = items.toMutableList()
                specialList.shuffle()
                setupSpecialRecyclerView(specialList)
            }
        }
    }

    private fun setupPopularRecyclerView(items: MutableList<vn.edu.hcmuaf.fit.ttltmobile.data.model.ItemModel>) {
        binding.recyclerViewPopular.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = PopularAdapter(items)
            isNestedScrollingEnabled = false
        }
    }

    private fun setupSpecialRecyclerView(items: MutableList<vn.edu.hcmuaf.fit.ttltmobile.data.model.ItemModel>) {
        binding.recyclerViewSpecial.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = SpecialAdapter(items)
            isNestedScrollingEnabled = false
        }
    }
}