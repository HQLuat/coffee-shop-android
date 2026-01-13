package vn.edu.hcmuaf.fit.ttltmobile.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.Product
import vn.edu.hcmuaf.fit.ttltmobile.data.repository.MainRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MainRepository(application.applicationContext)

    fun getPopular(): LiveData<MutableList<Product>> {
        return repository.loadPopular()
    }
}