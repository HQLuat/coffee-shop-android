package vn.edu.hcmuaf.fit.ttltmobile.data.api.service

import retrofit2.Call
import retrofit2.http.*
import vn.edu.hcmuaf.fit.ttltmobile.data.model.admin.*

interface AdminApiService {

    // Lấy danh sách user với phân trang
    @GET("admin/users")
    fun getAllUsers(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "createdAt",
        @Query("direction") direction: String = "desc"
    ): Call<PagedUserResponse>

    // Tìm kiếm user
    @GET("admin/users/search")
    fun searchUsers(@Query("keyword") keyword: String): Call<List<AdminUserResponse>>

    // Lấy chi tiết user
    @GET("admin/users/{userId}")
    fun getUserDetails(@Path("userId") userId: Long): Call<AdminUserResponse>

    // Lấy user theo role
    @GET("admin/users/role/{role}")
    fun getUsersByRole(@Path("role") role: String): Call<List<AdminUserResponse>>

    // Lấy user bị khóa
    @GET("admin/users/locked")
    fun getLockedUsers(): Call<List<AdminUserResponse>>

    // Lấy user chưa xác thực
    @GET("admin/users/unverified")
    fun getUnverifiedUsers(): Call<List<AdminUserResponse>>

    // Tạo user mới
    @POST("admin/users")
    fun createUser(@Body request: AdminCreateUserRequest): Call<AdminUserResponse>

    // Cập nhật user
    @PUT("admin/users/{userId}")
    fun updateUser(
        @Path("userId") userId: Long,
        @Body request: AdminUpdateUserRequest
    ): Call<AdminUserResponse>

    // Thay đổi role
    @PUT("admin/users/{userId}/role")
    fun changeUserRole(
        @Path("userId") userId: Long,
        @Body body: Map<String, String>
    ): Call<AdminUserResponse>

    // Khóa/mở khóa user
    @PUT("admin/users/{userId}/lock")
    fun toggleLockUser(
        @Path("userId") userId: Long,
        @Body body: Map<String, Boolean>
    ): Call<AdminUserResponse>

    // Xác thực email user
    @PUT("admin/users/{userId}/verify")
    fun verifyUserEmail(@Path("userId") userId: Long): Call<AdminUserResponse>

    // Reset mật khẩu
    @POST("admin/users/{userId}/reset-password")
    fun resetUserPassword(
        @Path("userId") userId: Long,
        @Body body: Map<String, String>
    ): Call<Map<String, String>>

    // Xóa user
    @DELETE("admin/users/{userId}")
    fun deleteUser(@Path("userId") userId: Long): Call<Map<String, String>>

    // Lấy thống kê
    @GET("admin/users/statistics")
    fun getUserStatistics(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Call<UserStatisticsResponse>
}