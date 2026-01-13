package vn.edu.hcmuaf.fit.ttltmobile.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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

        initPopular()
    }

    private fun initPopular() {
        binding.progressBarPopular.visibility = View.VISIBLE
        binding.recyclerViewPopular.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        viewModel.getPopular().observe(viewLifecycleOwner) { items ->
            binding.progressBarPopular.visibility = View.GONE
            if (!items.isNullOrEmpty()) {
                // Đổ list Product vào Adapter
                binding.recyclerViewPopular.adapter = PopularAdapter(items)
            }
        }
    }

//    private fun initSpecial() {
//        binding.progressBarSpecial.visibility = View.VISIBLE
//        binding.recyclerViewSpecial.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//
//        // Giả sử Special là những món có Rating thấp hơn hoặc Category khác
//        viewModel.getPopular().observe(viewLifecycleOwner) { items ->
//            binding.progressBarSpecial.visibility = View.GONE
//            if (!items.isNullOrEmpty()) {
//                // Bạn có thể lọc nhanh ở đây nếu chưa có hàm getSpecial trong Repository
//                val specialItems = items.filter { it.rating < 4.5 }.toMutableList()
//                binding.recyclerViewSpecial.adapter = SpecialAdapter(specialItems)
//            }
//        }
//    }

    private fun initCategory() {
        // Sau này bạn sẽ code phần hiện các nút Coffee, Tea... ở đây
        binding.progressBarCategory.visibility = View.GONE
    }
}