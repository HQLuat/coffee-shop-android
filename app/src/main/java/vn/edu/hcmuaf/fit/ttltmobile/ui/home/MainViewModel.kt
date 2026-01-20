package vn.edu.hcmuaf.fit.ttltmobile.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import vn.edu.hcmuaf.fit.ttltmobile.data.repository.MainRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MainRepository = MainRepository(application)

    fun getPopular() = repository.loadPopular()

    fun getByCategory(cat: String) = repository.loadByCategory(cat)
}