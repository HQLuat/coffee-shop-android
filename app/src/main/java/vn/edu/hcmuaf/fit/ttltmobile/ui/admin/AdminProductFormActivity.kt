package vn.edu.hcmuaf.fit.ttltmobile.ui.admin

import android.net.Uri
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.Product
import vn.edu.hcmuaf.fit.ttltmobile.data.model.product.ProductConstants
import vn.edu.hcmuaf.fit.ttltmobile.data.repository.AdminProductRepository
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityAdminProductFormBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity
import java.io.File
import java.io.FileOutputStream

class AdminProductFormActivity : BaseActivity<ActivityAdminProductFormBinding>() {

    private val adminRepo by lazy { AdminProductRepository(this) }
    private var currentProduct: Product? = null
    private var isEditMode = false
    private var selectedImageUri: Uri? = null

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.imgProduct.setImageURI(it)
            binding.layoutAddImage.visibility = View.GONE
        }
    }

    override fun getViewBinding() = ActivityAdminProductFormBinding.inflate(layoutInflater)

    override fun createView() {
        setupToolbar()
        setupDropdowns()
        checkIntentData()

        binding.cardSelectImage.setOnClickListener {
            getImage.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            validateAndCheckDuplicate()
        }
    }

    private fun setupToolbar() {
        binding.btnBackForm.setOnClickListener { finish() }
    }

    private fun setupDropdowns() {
        val catAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ProductConstants.categoryLabels)
        val sizeAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ProductConstants.sizeLabels)
        binding.autoCompleteCategory.setAdapter(catAdapter)
        binding.autoCompleteSize.setAdapter(sizeAdapter)
    }

    private fun checkIntentData() {
        currentProduct = intent.getSerializableExtra("DATA") as? Product
        if (currentProduct != null) {
            isEditMode = true
            binding.tvTitle.text = "Chỉnh sửa sản phẩm"
            fillData(currentProduct!!)
        } else {
            isEditMode = false
            binding.tvTitle.text = "Thêm sản phẩm mới"
        }
    }

    private fun fillData(product: Product) {
        binding.apply {
            edtName.setText(product.name)
            edtPrice.setText(product.price.toString())
            edtDesc.setText(product.description)

            autoCompleteCategory.setText(ProductConstants.getCategoryLabel(product.category), false)
            autoCompleteSize.setText(ProductConstants.getSizeLabel(product.size), false)

            if (!product.imageUrl.isNullOrEmpty()) {
                binding.layoutAddImage.visibility = View.GONE

                Glide.with(this@AdminProductFormActivity)
                    .load(product.imageUrl)
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(binding.imgProduct)
            }
        }
    }

    private fun validateAndCheckDuplicate() {
        val name = binding.edtName.text.toString().trim()
        val priceStr = binding.edtPrice.text.toString().trim()
        val description = binding.edtDesc.text.toString().trim()
        val selectedCatLabel = binding.autoCompleteCategory.text.toString()
        val selectedSizeLabel = binding.autoCompleteSize.text.toString()

        if (name.isEmpty() || priceStr.isEmpty() || selectedCatLabel.isEmpty() || selectedSizeLabel.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin")
            return
        }

        val categoryEnum = ProductConstants.getCategoryEnum(selectedCatLabel)
        val sizeEnum = ProductConstants.getSizeEnum(selectedSizeLabel)

        showLoading()
        adminRepo.getAllProducts().observe(this) { list ->
            if (list != null) {
                val isDuplicate = list.any {
                    it.name.equals(name, ignoreCase = true) &&
                            it.size.equals(sizeEnum, ignoreCase = true) &&
                            it.id != (currentProduct?.id ?: -1L)
                }

                if (isDuplicate) {
                    hideLoading()
                    showToast("Sản phẩm '$name' - Size $sizeEnum đã tồn tại!")
                } else {
                    proceedToSave(name, priceStr, description, categoryEnum, sizeEnum)
                }
            } else {
                hideLoading()
                showToast("Lỗi kiểm tra dữ liệu")
            }
        }
    }

    private fun proceedToSave(name: String, priceStr: String, desc: String, cat: String?, size: String?) {
        val productMap = mutableMapOf<String, Any>()
        productMap["name"] = name
        productMap["price"] = priceStr.toDoubleOrNull() ?: 0.0
        productMap["description"] = desc
        productMap["category"] = cat ?: ""
        productMap["size"] = size ?: ""

        val productJson = Gson().toJson(productMap)
        val productPart = productJson.toRequestBody("application/json".toMediaTypeOrNull())

        var filePart: MultipartBody.Part? = null
        selectedImageUri?.let { uri ->
            val file = getFileFromUri(uri)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
        }

        adminRepo.saveProductMultipart(
            id = if (isEditMode) currentProduct!!.id else null,
            productPart = productPart,
            filePart = filePart,
            isEdit = isEditMode
        ).observe(this) { success ->
            hideLoading()
            if (success) {
                showToast(if (isEditMode) "Cập nhật thành công" else "Thêm mới thành công")
                finish()
            } else {
                showToast("Thao tác thất bại")
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File {
        val tempFile = File(cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
}