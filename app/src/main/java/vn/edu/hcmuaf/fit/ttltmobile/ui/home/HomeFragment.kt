package vn.edu.hcmuaf.fit.ttltmobile.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.edu.hcmuaf.fit.ttltmobile.databinding.FragmentHomeBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel = MainViewModel()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        initCategory()
        initPopular()
        initSpecial()
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE
    }

    private fun initPopular() {
        binding.progressBarPopular.visibility = View.VISIBLE
    }

    private fun initSpecial() {
        binding.progressBarSpecial.visibility = View.VISIBLE
    }
}