package vn.edu.hcmuaf.fit.ttltmobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import vn.edu.hcmuaf.fit.ttltmobile.domain.CategoryModel
import vn.edu.hcmuaf.fit.ttltmobile.domain.ItemModel
import vn.edu.hcmuaf.fit.ttltmobile.repository.MainRepository

class MainViewModel: ViewModel() {
    private val repository = MainRepository()

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        return repository.loadCategory()
    }

    fun loadPopular(): LiveData<MutableList<ItemModel>> {
        return repository.loadPopular()
    }

    fun loadSpecial(): LiveData<MutableList<ItemModel>> {
        return repository.loadSpecial()
    }

    fun loadItems(categoryId: String): LiveData<MutableList<ItemModel>> {
        return repository.loadCategoryItems(categoryId)
    }
}