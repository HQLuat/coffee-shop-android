package vn.edu.hcmuaf.fit.ttltmobile.utils.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import vn.edu.hcmuaf.fit.ttltmobile.R

// <VB : ViewBinding> giúp Activity con tự định nghĩa loại binding của nó
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    // Biến binding dùng chung cho mọi Activity con
    protected lateinit var binding: VB

    // Dialog loading dùng chung
    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Ép kiểu Activity con phải nạp Layout vào đây
        binding = getViewBinding()
        setContentView(binding.root)

        // 2. Khởi tạo sẵn cái loading dialog (chưa hiện lên)
        initLoadingDialog()

        // 3. Gọi hàm initView để Activity con viết logic vào đó
        createView()
    }

    // Hàm trừu tượng: Activity con BẮT BUỘC phải định nghĩa binding của nó
    abstract fun getViewBinding(): VB

    // Hàm trừu tượng: Nơi viết code logic chính (thay vì viết tràn lan trong onCreate)
    abstract fun createView()

    // --- CÁC HÀM TIỆN ÍCH DÙNG CHUNG (HELPER) ---

    private fun initLoadingDialog() {
        loadingDialog = Dialog(this)
        loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Nhớ tạo file layout_loading.xml ở bước 1 nhé
        loadingDialog?.setContentView(R.layout.layout_loading)
        loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog?.setCancelable(false) // Không cho bấm ra ngoài để tắt
    }

    fun showLoading() {
        if (loadingDialog != null && !loadingDialog!!.isShowing) {
            loadingDialog?.show()
        }
    }

    fun hideLoading() {
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog?.dismiss()
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}