package vn.edu.hcmuaf.fit.ttltmobile.ui.profile

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.R
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.auth.UpdateUserProfileRequest
import vn.edu.hcmuaf.fit.ttltmobile.data.model.auth.UserProfile
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityPersonalInfoBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity
import java.text.SimpleDateFormat
import java.util.Locale

class PersonalInfoActivity : BaseActivity<ActivityPersonalInfoBinding>() {

    private val apiService: ApiService by lazy {
        ApiConfig.createService(ApiService::class.java, this)
    }

    private var isEditMode = false
    private var currentUserProfile: UserProfile? = null

    override fun getViewBinding(): ActivityPersonalInfoBinding {
        return ActivityPersonalInfoBinding.inflate(layoutInflater)
    }

    override fun createView() {
        setupClickListeners()
        loadUserInfo()
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
        }
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
                    showToast(response.body()?.message ?: "Lỗi khi tải thông tin người dùng")
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
        }
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

            if (isEditMode) {
                btnEditSave.text = "Lưu thay đổi"
                btnEditSave.setBackgroundColor(ContextCompat.getColor(this@PersonalInfoActivity, R.color.brown))
                edtFullName.requestFocus()
            } else {
                btnEditSave.text = "Chỉnh sửa"
                btnEditSave.setBackgroundColor(ContextCompat.getColor(this@PersonalInfoActivity, R.color.red))
                currentUserProfile?.let { displayUserInfo(it) }
            }
        }
    }

    private fun saveUserInfo() {
        showLoading()

        binding.apply {
            val newFullName = binding.edtFullName.text.toString().trim()
            val newPhoneNumber = binding.edtPhoneNumber.text.toString().trim()
            val newAddress = binding.edtAddress.text.toString().trim()

            // check full name
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

            // check phone number
            if (newPhoneNumber.isEmpty()) {
                edtPhoneNumber.error = "Vui lòng nhập số điện thoại"
                edtPhoneNumber.requestFocus()
                return
            }

            // check address
            if (newAddress.isEmpty()) {
                edtAddress.error = "Vui lòng nhập địa chỉ"
                edtAddress.requestFocus()
                return
            }

            val request = UpdateUserProfileRequest(newFullName, newPhoneNumber, newAddress)
            apiService.updateProfile(request).enqueue(object : Callback<UserProfile> {
                override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                    hideLoading()

                    if (response.isSuccessful) {
                        val updatedProfile = response.body()
                        if (updatedProfile != null) {
                            currentUserProfile = updatedProfile
                            displayUserInfo(updatedProfile)
                            updateLocalStorage(updatedProfile)
                            showToast(updatedProfile.message ?: "Cập nhật thông tin người dùng thành công")
                        } else {
                            showToast("Không thể tải thông tin người dùng mới")
                        }
                    } else {
                        showToast(response.body()?.message ?: "Lỗi khi cập nhật thông tin người dùng")
                    }
                }

                override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                    hideLoading()
                    Log.e("PersonalInfoActivity", "Update info error: ${t.message}", t)
                    showToast("Không thể kết nối đến server")
                }
            })
        }

        toggleEditMode()
    }

    private fun updateLocalStorage(userProfile: UserProfile) {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("full_name", userProfile.fullName)
            putString("phone_number", userProfile.phoneNumber)
            putString("address", userProfile.address)
            apply()
        }
    }
}