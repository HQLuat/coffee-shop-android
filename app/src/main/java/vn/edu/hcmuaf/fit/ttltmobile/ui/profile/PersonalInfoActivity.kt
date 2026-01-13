package vn.edu.hcmuaf.fit.ttltmobile.ui.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.R
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.AuthApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.auth.UserProfile
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityPersonalInfoBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.MultipartHelper
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity
import java.text.SimpleDateFormat
import java.util.Locale

class PersonalInfoActivity : BaseActivity<ActivityPersonalInfoBinding>() {

    private val apiService: AuthApiService by lazy {
        ApiConfig.getAuthService(this)
    }

    private var isEditMode = false
    private var currentUserProfile: UserProfile? = null
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Validate image
            if (!MultipartHelper.isValidImageFile(this, it)) {
                showToast("Vui lòng chọn file ảnh hợp lệ")
                return@registerForActivityResult
            }

            // Check file size (max 5MB)
            val fileSizeMB = MultipartHelper.getFileSizeInMB(this, it)
            if (fileSizeMB > 5) {
                showToast("Kích thước ảnh không được vượt quá 5MB")
                return@registerForActivityResult
            }

            selectedImageUri = it

            // Display selected image
            Glide.with(this)
                .load(it)
                .circleCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(binding.ivAvatar)
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            showToast("Cần cấp quyền để chọn ảnh")
        }
    }

    override fun getViewBinding(): ActivityPersonalInfoBinding {
        return ActivityPersonalInfoBinding.inflate(layoutInflater)
    }

    override fun createView() {
        setupClickListeners()
        loadUserInfo()

        // Phone's back button
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isEditMode) {
                    toggleEditMode()
                } else {
                    finish()
                }
            }
        })
    }

    private fun setupClickListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                if (isEditMode) {
                    toggleEditMode()
                } else {
                    finish()
                }
            }

            btnEditSave.setOnClickListener {
                if (isEditMode) {
                    saveUserInfo()
                } else {
                    toggleEditMode()
                }
            }

            // Click to change avatar
            ivAvatar.setOnClickListener {
                if (isEditMode) {
                    checkPermissionAndPickImage()
                }
            }

            btnChangeAvatar.setOnClickListener {
                if (isEditMode) {
                    checkPermissionAndPickImage()
                }
            }
        }
    }

    private fun getRequiredPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    private fun checkPermissionAndPickImage() {
        val permission = getRequiredPermission()
        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                showToast("Ứng dụng cần quyền truy cập thư viện để thay đổi ảnh đại diện.")
                permissionLauncher.launch(permission)
            }
            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }

    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    private fun loadUserInfo() {
        showLoading()

        apiService.getProfile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                hideLoading()

                if (response.isSuccessful) {
                    val userInfo = response.body()
                    if (userInfo != null) {
                        currentUserProfile = userInfo
                        displayUserInfo(userInfo)
                        updateLocalStorage(userInfo)
                    } else {
                        showToast("Không thể tải thông tin người dùng")
                    }
                } else {
                    if (response.code() == 401 || response.code() == 403) {
                        return
                    }

                    try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.optString("message", "Lấy thông tin người dùng thất bại")
                            showToast(errorMessage)
                        } else {
                            showToast("Lỗi: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        showToast("Lỗi lấy thông tin người dùng")
                    }
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                hideLoading()
                Log.e("PersonalInfoActivity", "Load info error: ${t.message}", t)
                showToast("Không thể kết nối đến server")
            }
        })
    }

    private fun displayUserInfo(userProfile: UserProfile) {
        binding.apply {
            edtFullName.setText(userProfile.fullName)
            tvEmail.text = userProfile.email
            edtPhoneNumber.setText(userProfile.phoneNumber)
            edtAddress.setText(userProfile.address)
            tvRole.text = when(userProfile.role) {
                "USER" -> "Người dùng"
                "ADMIN" -> "Quản trị viên"
                else -> userProfile.role
            }

            // Format created date
            tvCreatedAt.text = userProfile.createdAt?.let { formatDate(it) }

            // Load avatar
            userProfile.avatarUrl?.let { loadAvatar(it) }
        }
    }

    private fun loadAvatar(url: String) {
        Glide.with(this)
            .load(url)
            .circleCrop()
            .placeholder(R.drawable.ic_user_placeholder)
            .error(R.drawable.ic_user_placeholder)
            .into(binding.ivAvatar)
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode

        binding.apply {
            edtFullName.isEnabled = isEditMode
            edtPhoneNumber.isEnabled = isEditMode
            edtAddress.isEnabled = isEditMode
            btnChangeAvatar.visibility = if (isEditMode) android.view.View.VISIBLE else android.view.View.GONE

            if (isEditMode) {
                btnEditSave.text = "Lưu thay đổi"
                btnEditSave.setBackgroundColor(ContextCompat.getColor(this@PersonalInfoActivity, R.color.brown))
                edtFullName.requestFocus()
            } else {
                btnEditSave.text = "Chỉnh sửa"
                btnEditSave.setBackgroundColor(ContextCompat.getColor(this@PersonalInfoActivity, R.color.red))
                currentUserProfile?.let { displayUserInfo(it) }
                selectedImageUri = null
            }
        }
    }

    private fun saveUserInfo() {
        binding.apply {
            val newFullName = edtFullName.text.toString().trim()
            val newPhoneNumber = edtPhoneNumber.text.toString().trim()
            val newAddress = edtAddress.text.toString().trim()

            // Validate
            if (newFullName.isEmpty()) {
                edtFullName.error = "Vui lòng nhập họ và tên"
                edtFullName.requestFocus()
                return
            }
            if (newFullName.length < 2) {
                edtFullName.error = "Họ tên phải có ít nhất 2 ký tự"
                edtFullName.requestFocus()
                return
            }
            if (newPhoneNumber.isEmpty()) {
                edtPhoneNumber.error = "Vui lòng nhập số điện thoại"
                edtPhoneNumber.requestFocus()
                return
            }
            if (newAddress.isEmpty()) {
                edtAddress.error = "Vui lòng nhập địa chỉ"
                edtAddress.requestFocus()
                return
            }

            showLoading()
            callApiUpdateProfile(newFullName, newPhoneNumber, newAddress)
        }
    }

    private fun callApiUpdateProfile(fullName: String, phoneNumber: String, address: String) {
        // Create requestBody
        val fullNameBody = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneNumberBody = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
        val addressBody = address.toRequestBody("text/plain".toMediaTypeOrNull())

        // Create MultipartBody.Part for avatar
        val avatarPart: MultipartBody.Part? = selectedImageUri?.let { uri ->
            try {
                val file = MultipartHelper.uriToFile(this, uri)
                if (file != null) {
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("avatar", file.name, requestFile)
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("PersonalInfo", "Error creating file from URI: ${e.message}")
                null
            }
        }

        // Call API
        apiService.updateProfile(
            fullName = fullNameBody,
            phoneNumber = phoneNumberBody,
            address = addressBody,
            avatar = avatarPart,
            deleteAvatar = null
        ).enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                hideLoading()

                if (response.isSuccessful) {
                    val updatedProfile = response.body()
                    if (updatedProfile != null) {
                        currentUserProfile = updatedProfile
                        displayUserInfo(updatedProfile)
                        updateLocalStorage(updatedProfile)
                        toggleEditMode()
                        showToast(updatedProfile.message ?: "Cập nhật thành công")
                    } else {
                        showToast("Dữ liệu trả về rỗng")
                    }
                } else {
                    try {
                        val errorJson = response.errorBody()?.string()
                        val jsonObject = JSONObject(errorJson ?: "{}")
                        val errorMessage = jsonObject.optString("message", "Lỗi khi cập nhật")
                        showToast(errorMessage)
                    } catch (e: Exception) {
                        showToast("Lỗi khi cập nhật: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                hideLoading()
                Log.e("PersonalInfoActivity", "Lỗi kết nối: ${t.message}", t)
                showToast("Không thể kết nối đến server")
            }
        })
    }

    // Helpers
    private fun updateLocalStorage(userProfile: UserProfile) {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("full_name", userProfile.fullName)
            putString("phone_number", userProfile.phoneNumber)
            putString("address", userProfile.address)
            putString("avatar_url", userProfile.avatarUrl)
            apply()
        }
    }
}