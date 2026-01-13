package vn.edu.hcmuaf.fit.ttltmobile.data.repository

import android.content.Context
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.AdminOrderApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.PageResponse
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.*

class AdminOrderRepository(context: Context) {

    private val adminOrderService: AdminOrderApiService = ApiConfig.getAdminOrderService(context)

    // Get All Orders with Pagination
    fun getAllOrders(
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "createdAt",
        direction: String = "desc",
        onSuccess: (PageResponse<OrderResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        adminOrderService.getAllOrders(page, size, sortBy, direction)
            .enqueue(object : Callback<PageResponse<OrderResponse>> {
                override fun onResponse(
                    call: Call<PageResponse<OrderResponse>>,
                    response: Response<PageResponse<OrderResponse>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { onSuccess(it) }
                    } else {
                        onError("Không thể tải danh sách đơn hàng")
                        Log.e("AdminOrderRepo", "Get all orders failed: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<PageResponse<OrderResponse>>, t: Throwable) {
                    onError("Lỗi kết nối: ${t.message}")
                    Log.e("AdminOrderRepo", "Get all orders error", t)
                }
            })
    }

    // Get Orders by Status
    fun getOrdersByStatus(
        status: String,
        onSuccess: (List<OrderResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        adminOrderService.getOrdersByStatus(status)
            .enqueue(object : Callback<List<OrderResponse>> {
                override fun onResponse(
                    call: Call<List<OrderResponse>>,
                    response: Response<List<OrderResponse>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { onSuccess(it) } ?: onSuccess(emptyList())
                    } else {
                        onError("Không thể tải đơn hàng theo trạng thái")
                        Log.e("AdminOrderRepo", "Get by status failed: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<OrderResponse>>, t: Throwable) {
                    onError("Lỗi kết nối: ${t.message}")
                    Log.e("AdminOrderRepo", "Get by status error", t)
                }
            })
    }

    // Search Orders
    fun searchOrders(
        keyword: String,
        onSuccess: (List<OrderResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        adminOrderService.searchOrders(keyword)
            .enqueue(object : Callback<List<OrderResponse>> {
                override fun onResponse(
                    call: Call<List<OrderResponse>>,
                    response: Response<List<OrderResponse>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { onSuccess(it) } ?: onSuccess(emptyList())
                    } else {
                        onError("Không thể tìm kiếm đơn hàng")
                        Log.e("AdminOrderRepo", "Search failed: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<OrderResponse>>, t: Throwable) {
                    onError("Lỗi kết nối: ${t.message}")
                    Log.e("AdminOrderRepo", "Search error", t)
                }
            })
    }

    // Get Order Details
    fun getOrderDetails(
        orderId: Long,
        onSuccess: (OrderResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        adminOrderService.getOrderDetails(orderId)
            .enqueue(object : Callback<OrderResponse> {
                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { onSuccess(it) }
                    } else {
                        onError("Không thể tải chi tiết đơn hàng")
                        Log.e("AdminOrderRepo", "Get details failed: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    onError("Lỗi kết nối: ${t.message}")
                    Log.e("AdminOrderRepo", "Get details error", t)
                }
            })
    }

    // Update Order Status
    fun updateOrderStatus(
        orderId: Long,
        status: OrderStatus,
        onSuccess: (OrderResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = UpdateOrderStatusRequest(status)

        adminOrderService.updateOrderStatus(orderId, request)
            .enqueue(object : Callback<OrderResponse> {
                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { onSuccess(it) }
                    } else {
                        onError("Không thể cập nhật trạng thái đơn hàng")
                        Log.e("AdminOrderRepo", "Update status failed: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    onError("Lỗi kết nối: ${t.message}")
                    Log.e("AdminOrderRepo", "Update status error", t)
                }
            })
    }

    // Delete Order
    fun deleteOrder(
        orderId: Long,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        adminOrderService.deleteOrder(orderId)
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(
                    call: Call<Map<String, String>>,
                    response: Response<Map<String, String>>
                ) {
                    if (response.isSuccessful) {
                        val message = response.body()?.get("message") ?: "Đã xóa đơn hàng"
                        onSuccess(message)
                    } else {
                        onError("Không thể xóa đơn hàng")
                        Log.e("AdminOrderRepo", "Delete failed: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    onError("Lỗi kết nối: ${t.message}")
                    Log.e("AdminOrderRepo", "Delete error", t)
                }
            })
    }

    // Get Statistics
    fun getStatistics(
        startDate: String? = null,
        endDate: String? = null,
        onSuccess: (OrderStatisticsResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        adminOrderService.getStatistics(startDate, endDate)
            .enqueue(object : Callback<OrderStatisticsResponse> {
                override fun onResponse(
                    call: Call<OrderStatisticsResponse>,
                    response: Response<OrderStatisticsResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { onSuccess(it) }
                    } else {
                        onError("Không thể tải thống kê")
                        Log.e("AdminOrderRepo", "Get stats failed: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<OrderStatisticsResponse>, t: Throwable) {
                    onError("Lỗi kết nối: ${t.message}")
                    Log.e("AdminOrderRepo", "Get stats error", t)
                }
            })
    }

    // Get Orders by User
    fun getOrdersByUser(
        userId: Long,
        onSuccess: (List<OrderResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        adminOrderService.getOrdersByUser(userId)
            .enqueue(object : Callback<List<OrderResponse>> {
                override fun onResponse(
                    call: Call<List<OrderResponse>>,
                    response: Response<List<OrderResponse>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { onSuccess(it) } ?: onSuccess(emptyList())
                    } else {
                        onError("Không thể tải đơn hàng của người dùng")
                        Log.e("AdminOrderRepo", "Get by user failed: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<OrderResponse>>, t: Throwable) {
                    onError("Lỗi kết nối: ${t.message}")
                    Log.e("AdminOrderRepo", "Get by user error", t)
                }
            })
    }

    // Update Delivery Info
    fun updateDeliveryInfo(
        orderId: Long,
        deliveryAddress: String,
        phoneNumber: String,
        note: String?,
        onSuccess: (OrderResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = UpdateDeliveryInfoRequest(deliveryAddress, phoneNumber, note)

        adminOrderService.updateDeliveryInfo(orderId, request)
            .enqueue(object : Callback<OrderResponse> {
                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { onSuccess(it) }
                    } else {
                        onError("Không thể cập nhật thông tin giao hàng")
                        Log.e("AdminOrderRepo", "Update delivery failed: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    onError("Lỗi kết nối: ${t.message}")
                    Log.e("AdminOrderRepo", "Update delivery error", t)
                }
            })
    }
}