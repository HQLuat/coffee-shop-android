package vn.edu.hcmuaf.fit.ttltmobile.data.repository

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.AdminApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.admin.*

class AdminRepository(private val context: Context) {

    private val apiService: AdminApiService = ApiConfig.getAdminService(context)

    fun getAllUsers(
        page: Int,
        size: Int,
        sortBy: String,
        direction: String,
        callback: (Result<PagedUserResponse>) -> Unit
    ) {
        apiService.getAllUsers(page, size, sortBy, direction).enqueue(object : Callback<PagedUserResponse> {
            override fun onResponse(call: Call<PagedUserResponse>, response: Response<PagedUserResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Không thể tải danh sách user")))
                }
            }

            override fun onFailure(call: Call<PagedUserResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun searchUsers(keyword: String, callback: (Result<List<AdminUserResponse>>) -> Unit) {
        apiService.searchUsers(keyword).enqueue(object : Callback<List<AdminUserResponse>> {
            override fun onResponse(call: Call<List<AdminUserResponse>>, response: Response<List<AdminUserResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Không tìm thấy user")))
                }
            }

            override fun onFailure(call: Call<List<AdminUserResponse>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun getUserDetails(userId: Long, callback: (Result<AdminUserResponse>) -> Unit) {
        apiService.getUserDetails(userId).enqueue(object : Callback<AdminUserResponse> {
            override fun onResponse(call: Call<AdminUserResponse>, response: Response<AdminUserResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Không thể tải chi tiết user")))
                }
            }

            override fun onFailure(call: Call<AdminUserResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun toggleLockUser(userId: Long, locked: Boolean, callback: (Result<AdminUserResponse>) -> Unit) {
        val body = mapOf("locked" to locked)
        apiService.toggleLockUser(userId, body).enqueue(object : Callback<AdminUserResponse> {
            override fun onResponse(call: Call<AdminUserResponse>, response: Response<AdminUserResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Không thể thay đổi trạng thái khóa")))
                }
            }

            override fun onFailure(call: Call<AdminUserResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun changeUserRole(userId: Long, role: String, callback: (Result<AdminUserResponse>) -> Unit) {
        val body = mapOf("role" to role)
        apiService.changeUserRole(userId, body).enqueue(object : Callback<AdminUserResponse> {
            override fun onResponse(call: Call<AdminUserResponse>, response: Response<AdminUserResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Không thể thay đổi role")))
                }
            }

            override fun onFailure(call: Call<AdminUserResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun deleteUser(userId: Long, callback: (Result<String>) -> Unit) {
        apiService.deleteUser(userId).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    val message = response.body()?.get("message") ?: "Đã xóa user thành công"
                    callback(Result.success(message))
                } else {
                    callback(Result.failure(Exception("Không thể xóa user")))
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun getUserStatistics(callback: (Result<UserStatisticsResponse>) -> Unit) {
        apiService.getUserStatistics().enqueue(object : Callback<UserStatisticsResponse> {
            override fun onResponse(call: Call<UserStatisticsResponse>, response: Response<UserStatisticsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Không thể tải thống kê")))
                }
            }

            override fun onFailure(call: Call<UserStatisticsResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun resetUserPassword(userId: Long, newPassword: String, callback: (Result<String>) -> Unit) {
        val body = mapOf("newPassword" to newPassword)
        apiService.resetUserPassword(userId, body).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    val message = response.body()?.get("message") ?: "Đã reset mật khẩu thành công"
                    callback(Result.success(message))
                } else {
                    callback(Result.failure(Exception("Không thể reset mật khẩu")))
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}