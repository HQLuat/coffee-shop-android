package vn.edu.hcmuaf.fit.ttltmobile.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vn.edu.hcmuaf.fit.ttltmobile.data.model.admin.*
import vn.edu.hcmuaf.fit.ttltmobile.data.repository.AdminRepository

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AdminRepository(application)

    private val _userList = MutableLiveData<PagedUserResponse>()
    val userList: LiveData<PagedUserResponse> = _userList

    private val _searchResults = MutableLiveData<List<AdminUserResponse>>()
    val searchResults: LiveData<List<AdminUserResponse>> = _searchResults

    private val _userDetails = MutableLiveData<AdminUserResponse>()
    val userDetails: LiveData<AdminUserResponse> = _userDetails

    private val _statistics = MutableLiveData<UserStatisticsResponse>()
    val statistics: LiveData<UserStatisticsResponse> = _statistics

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    fun loadUsers(page: Int = 0, size: Int = 10, sortBy: String = "createdAt", direction: String = "desc") {
        _isLoading.value = true
        _errorMessage.value = ""
        _successMessage.value = ""

        repository.getAllUsers(page, size, sortBy, direction) { result ->
            _isLoading.value = false
            result.onSuccess { pagedResponse ->
                _userList.value = pagedResponse
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Lỗi khi tải danh sách user"
            }
        }
    }

    fun searchUsers(keyword: String) {
        if (keyword.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        _isLoading.value = true
        _errorMessage.value = ""

        repository.searchUsers(keyword) { result ->
            _isLoading.value = false
            result.onSuccess { users ->
                _searchResults.value = users
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Không tìm thấy user"
            }
        }
    }

    fun loadUserDetails(userId: Long) {
        _isLoading.value = true
        _errorMessage.value = ""

        repository.getUserDetails(userId) { result ->
            _isLoading.value = false
            result.onSuccess { user ->
                _userDetails.value = user
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Không thể tải chi tiết user"
            }
        }
    }

    fun toggleLockUser(userId: Long, locked: Boolean) {
        _isLoading.value = true
        _errorMessage.value = ""
        _successMessage.value = ""

        repository.toggleLockUser(userId, locked) { result ->
            _isLoading.value = false
            result.onSuccess { user ->
                _userDetails.value = user
                _successMessage.value = if (locked) "Đã khóa user" else "Đã mở khóa user"
                loadUsers()
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Không thể thay đổi trạng thái"
            }
        }
    }

    fun changeUserRole(userId: Long, role: String) {
        _isLoading.value = true
        _errorMessage.value = ""
        _successMessage.value = ""

        repository.changeUserRole(userId, role) { result ->
            _isLoading.value = false
            result.onSuccess { user ->
                _userDetails.value = user
                _successMessage.value = "Đã thay đổi role thành công"
                loadUsers()
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Không thể thay đổi role"
            }
        }
    }

    fun deleteUser(userId: Long) {
        _isLoading.value = true
        _errorMessage.value = ""
        _successMessage.value = ""

        repository.deleteUser(userId) { result ->
            _isLoading.value = false
            result.onSuccess { message ->
                _successMessage.value = message
                loadUsers()
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Không thể xóa user"
            }
        }
    }

    fun resetPassword(userId: Long, newPassword: String) {
        _isLoading.value = true
        _errorMessage.value = ""
        _successMessage.value = ""

        repository.resetUserPassword(userId, newPassword) { result ->
            _isLoading.value = false
            result.onSuccess { message ->
                _successMessage.value = message
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Không thể reset mật khẩu"
            }
        }
    }

    fun loadStatistics() {
        _isLoading.value = true
        _errorMessage.value = ""

        repository.getUserStatistics { result ->
            _isLoading.value = false
            result.onSuccess { stats ->
                _statistics.value = stats
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Không thể tải thống kê"
            }
        }
    }
}