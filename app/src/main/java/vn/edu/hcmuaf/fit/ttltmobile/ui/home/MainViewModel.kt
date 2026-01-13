package vn.edu.hcmuaf.fit.ttltmobile.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import vn.edu.hcmuaf.fit.ttltmobile.data.repository.MainRepository

// Đổi từ ViewModel sang AndroidViewModel để lấy được context (application)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Tự khởi tạo Repository bên trong luôn
    private val repository: MainRepository = MainRepository(application)

    fun getPopular() = repository.loadPopular()

    fun getByCategory(cat: String) = repository.loadByCategory(cat)
}