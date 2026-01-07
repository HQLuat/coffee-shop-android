package vn.edu.hcmuaf.fit.ttltmobile.data.model.cart
import java.math.BigDecimal


data class AddToCartRequest(
    val productId: Long,
    val quantity: Int
)


data class UpdateCartItemRequest(
    val quantity: Int
)


data class CartItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val imageUrl: String,
    val category: String,
    val size: String,
    val price: BigDecimal,
    val quantity: Int,
    val subtotal: BigDecimal
) {

    fun getPriceFormatted(): String = "₫${String.format("%,d", price.toLong())}"
    fun getSubtotalFormatted(): String = "₫${String.format("%,d", subtotal.toLong())}"
}


data class CartResponse(
    val id: Long,
    val items: List<CartItemResponse>,
    val totalAmount: BigDecimal,
    val totalItems: Int,
    val message: String?
) {
    fun getTotalFormatted(): String = "₫${String.format("%,d", totalAmount.toLong())}"

    fun isEmpty(): Boolean = items.isEmpty()
}