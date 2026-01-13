package vn.edu.hcmuaf.fit.ttltmobile.data.api.service

import retrofit2.Call
import retrofit2.http.*
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.*

interface OrderApiService {

    // Create order from cart
    @POST("orders")
    fun createOrder(@Body request: CreateOrderRequest): Call<OrderResponse>

    // Get order detail
    @GET("orders/{orderId}")
    fun getOrder(@Path("orderId") orderId: Long): Call<OrderResponse>

    // Get order history
    @GET("orders/history")
    fun getOrderHistory(): Call<List<OrderHistoryResponse>>

    // Reorder
    @POST("orders/{orderId}/reorder")
    fun reorder(@Path("orderId") orderId: Long): Call<OrderResponse>

    // Create ZaloPay payment
    @POST("orders/{orderId}/zalopay")
    fun createZaloPayPayment(@Path("orderId") orderId: Long): Call<ZaloPayResponse>

    // Query payment status
    @GET("orders/zalopay/query/{appTransId}")
    fun queryPaymentStatus(@Path("appTransId") appTransId: String): Call<Map<String, Any>>

    // Verify and update order after payment
    @POST("orders/{orderId}/zalopay/verify-and-update")
    fun verifyAndUpdateOrder(@Path("orderId") orderId: Long): Call<Map<String, Any>>

    // Cancel order
    @POST("orders/{orderId}/cancel")
    fun cancelOrder(@Path("orderId") orderId: Long): Call<OrderResponse>

    // Get payment info (debug)
    @GET("orders/{orderId}/payment-info")
    fun getOrderPaymentInfo(@Path("orderId") orderId: Long): Call<Map<String, Any>>

    // Refund order
    @POST("orders/{orderId}/zalopay/refund")
    fun refundOrder(
        @Path("orderId") orderId: Long,
        @Body request: RefundRequest
    ): Call<RefundResponse>

    // Query refund status
    @GET("orders/zalopay/refund/{refundId}")
    fun queryRefundStatus(@Path("refundId") refundId: String): Call<Map<String, Any>>

    // Get refund history
    @GET("orders/{orderId}/refunds")
    fun getRefundHistory(@Path("orderId") orderId: Long): Call<List<RefundHistoryResponse>>

    // Refund and cancel order
    @POST("orders/{orderId}/refund-and-cancel")
    fun refundAndCancelOrder(
        @Path("orderId") orderId: Long,
        @Body body: Map<String, String>
    ): Call<Map<String, Any>>
}

interface AdminOrderApiService {

    // Get all orders with pagination
    @GET("admin/orders")
    fun getAllOrders(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "createdAt",
        @Query("direction") direction: String = "desc"
    ): Call<PageResponse<OrderResponse>>

    // Get orders by status
    @GET("admin/orders/status/{status}")
    fun getOrdersByStatus(@Path("status") status: String): Call<List<OrderResponse>>

    // Search orders
    @GET("admin/orders/search")
    fun searchOrders(@Query("keyword") keyword: String): Call<List<OrderResponse>>

    // Get order details
    @GET("admin/orders/{orderId}")
    fun getOrderDetails(@Path("orderId") orderId: Long): Call<OrderResponse>

    // Update order status
    @PUT("admin/orders/{orderId}/status")
    fun updateOrderStatus(
        @Path("orderId") orderId: Long,
        @Body request: UpdateOrderStatusRequest
    ): Call<OrderResponse>

    // Delete order
    @DELETE("admin/orders/{orderId}")
    fun deleteOrder(@Path("orderId") orderId: Long): Call<Map<String, String>>

    // Get statistics
    @GET("admin/orders/statistics")
    fun getStatistics(
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?
    ): Call<OrderStatisticsResponse>

    // Get orders by user
    @GET("admin/orders/user/{userId}")
    fun getOrdersByUser(@Path("userId") userId: Long): Call<List<OrderResponse>>

    // Update delivery info
    @PUT("admin/orders/{orderId}/delivery")
    fun updateDeliveryInfo(
        @Path("orderId") orderId: Long,
        @Body request: UpdateDeliveryInfoRequest
    ): Call<OrderResponse>
}

// Page Response for pagination
data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val number: Int,
    val first: Boolean,
    val last: Boolean
)