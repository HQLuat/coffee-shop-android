package vn.edu.hcmuaf.fit.ttltmobile.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import vn.edu.hcmuaf.fit.ttltmobile.databinding.FragmentHomeBinding
import vn.edu.hcmuaf.fit.ttltmobile.ui.home.PopularAdapter
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
        initSpecial()

        // Tạm thời ẩn thanh loading của Category vì làm sau
        binding.progressBarCategory.visibility = View.GONE
    }

    private fun initPopular() {
        binding.progressBarPopular.visibility = View.VISIBLE
        viewModel.getPopular().observe(viewLifecycleOwner) { items ->
            binding.progressBarPopular.visibility = View.GONE
            if (items != null) {
                binding.recyclerViewPopular.layoutManager = LinearLayoutManager(
                    requireContext(), LinearLayoutManager.HORIZONTAL, false
                )
                binding.recyclerViewPopular.adapter = PopularAdapter(items)
            }
        }
    }

    private fun initSpecial() {
        binding.progressBarSpecial.visibility = View.VISIBLE
        viewModel.getPopular().observe(viewLifecycleOwner) { items ->
            binding.progressBarSpecial.visibility = View.GONE
            if (items != null) {
                // Sử dụng SpecialAdapter bạn đã định nghĩa
                binding.recyclerViewSpecial.layoutManager = LinearLayoutManager(
                    requireContext(), LinearLayoutManager.HORIZONTAL, false
                )
                binding.recyclerViewSpecial.adapter = SpecialAdapter(items)
            }
        }
    }
}