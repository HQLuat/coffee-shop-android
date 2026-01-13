package vn.edu.hcmuaf.fit.ttltmobile.data.model.order

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

enum class PaymentMethod {
    @SerializedName("ZALO_PAY")
    ZALO_PAY,
    @SerializedName("COD")
    COD,
    @SerializedName("BANK_TRANSFER")
    BANK_TRANSFER;
    fun getDisplayName(): String {
        return when (this) {
            ZALO_PAY -> "ZaloPay"
            COD -> "Thanh toán khi nhận hàng"
            BANK_TRANSFER -> "Chuyển khoản ngân hàng"
        }
    }
}

enum class OrderStatus {
    @SerializedName("PENDING")
    PENDING,

    @SerializedName("CONFIRMED")
    CONFIRMED,

    @SerializedName("PREPARING")
    PREPARING,

    @SerializedName("SHIPPING")
    SHIPPING,

    @SerializedName("DELIVERED")
    DELIVERED,

    @SerializedName("CANCELLED")
    CANCELLED,

    @SerializedName("REFUNDED")
    REFUNDED;

    fun getDisplayName(): String {
        return when (this) {
            PENDING -> "Chờ xác nhận"
            CONFIRMED -> "Đã xác nhận"
            PREPARING -> "Đang chuẩn bị"
            SHIPPING -> "Đang giao"
            DELIVERED -> "Hoàn thành"
            CANCELLED -> "Đã hủy"
            REFUNDED -> "Đã hoàn tiền"
        }
    }
}

data class CartItemRequest(
    val productId: Long,
    val productName: String,
    val price: BigDecimal,
    val quantity: Int
)

data class CreateOrderRequest(
    val items: List<CartItemRequest>,
    val paymentMethod: PaymentMethod,
    val deliveryAddress: String,
    val phoneNumber: String,
    val note: String?
)

data class OrderItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val price: BigDecimal,
    val quantity: Int,
    val subtotal: BigDecimal
)

data class OrderResponse(
    val id: Long,
    val orderCode: String,
    val totalAmount: BigDecimal,
    val status: OrderStatus,
    val statusDisplay: String?,
    val paymentMethod: PaymentMethod,
    val paymentMethodDisplay: String?,
    val deliveryAddress: String,
    val phoneNumber: String,
    val note: String?,
    val createdAt: String,
    val confirmedAt: String?,
    val preparingAt: String?,
    val shippingAt: String?,
    val deliveredAt: String?,
    val items: List<OrderItemResponse>,

    // OPTIONAL fields for admin (server may not always include)
    val userId: Long? = null,
    val userEmail: String? = null
) {
    fun getTotalFormatted(): String = "₫${String.format("%,d", totalAmount.toLong())}"

    fun canCancel(): Boolean = status == OrderStatus.PENDING

    // Helper để check đã thanh toán chưa
    fun isPaid(): Boolean = status != OrderStatus.PENDING &&
            status != OrderStatus.CANCELLED &&
            status != OrderStatus.REFUNDED

    // Helper để check có thể refund không
    fun canRefund(): Boolean =
        status == OrderStatus.DELIVERED &&
                paymentMethod == PaymentMethod.ZALO_PAY

    // Helper để lấy email hiển thị
    fun getDisplayEmail(): String = userEmail ?: "N/A"
}

data class OrderHistoryResponse(
    val id: Long,
    val orderCode: String,
    val totalAmount: BigDecimal,
    val status: OrderStatus,
    val statusDisplay: String?,
    val paymentMethod: PaymentMethod,
    val paymentMethodDisplay: String?,
    val createdAt: String,
    val itemCount: Int
)

data class ZaloPayResponse(
    val orderUrl: String,
    val appTransId: String,
    val orderId: Long,
    val message: String?
)

data class RefundRequest(
    val orderId: Long,
    val amount: BigDecimal,
    val description: String
)

data class RefundResponse(
    val refundId: String,
    val orderId: Long,
    val refundAmount: BigDecimal,
    val message: String?,
    val returnCode: Int?,
    val returnMessage: String?
)

enum class RefundStatus {
    @SerializedName("REFUND_PENDING")
    REFUND_PENDING,

    @SerializedName("REFUND_PROCESSING")
    REFUND_PROCESSING,

    @SerializedName("REFUNDED")
    REFUNDED,

    @SerializedName("REFUND_FAILED")
    REFUND_FAILED;

    fun getDisplayName(): String {
        return when (this) {
            REFUND_PENDING -> "Chờ hoàn tiền"
            REFUND_PROCESSING -> "Đang xử lý hoàn tiền"
            REFUNDED -> "Đã hoàn tiền"
            REFUND_FAILED -> "Hoàn tiền thất bại"
        }
    }

    fun isCompleted(): Boolean {
        return this == REFUNDED || this == REFUND_FAILED
    }

    fun isProcessing(): Boolean {
        return this == REFUND_PENDING || this == REFUND_PROCESSING
    }
}
data class RefundHistoryResponse(
    val id: Long,
    val refundId: String,
    val orderId: Long,
    val orderCode: String?,
    val refundAmount: BigDecimal,
    val description: String?,
    val status: RefundStatus,
    val statusDisplay: String?,
    val returnCode: Int?,
    val returnMessage: String?,
    val createdAt: String,
    val processedAt: String?
)

data class UpdateOrderStatusRequest(
    val status: OrderStatus
)

data class UpdateDeliveryInfoRequest(
    val deliveryAddress: String,
    val phoneNumber: String,
    val note: String?
)

data class OrderStatisticsResponse(
    val totalOrders: Long,
    val pendingOrders: Long,
    val confirmedOrders: Long,
    val preparingOrders: Long,
    val shippingOrders: Long,
    val completedOrders: Long,
    val cancelledOrders: Long,
    val refundedOrders: Long,
    val totalRevenue: BigDecimal,
    val averageOrderValue: BigDecimal?,
    val startDate: String?,
    val endDate: String?
)