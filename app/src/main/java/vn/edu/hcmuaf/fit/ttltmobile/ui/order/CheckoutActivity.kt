package vn.edu.hcmuaf.fit.ttltmobile.ui.checkout

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityCheckoutBinding
import vn.edu.hcmuaf.fit.ttltmobile.ui.order.OrderDetailActivity
import java.math.BigDecimal

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var viewModel: CheckoutViewModel
    private var cartTotal: BigDecimal = BigDecimal.ZERO
    private var createdOrderId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[CheckoutViewModel::class.java]

        setupToolbar()
        setupListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {
        binding.btnPlaceOrder.setOnClickListener {
            placeOrder()
        }
    }

    private fun observeViewModel() {
        viewModel.cartData.observe(this) { cart ->
            cartTotal = cart.totalAmount
            updateUI()
        }

        viewModel.subtotal.observe(this) { subtotal ->
            binding.tvSubtotal.text = formatCurrency(subtotal)
            updateUI()
        }

        viewModel.tax.observe(this) { tax ->
            binding.tvTax.text = formatCurrency(tax)
            updateUI()
        }

        viewModel.delivery.observe(this) { delivery ->
            binding.tvDelivery.text = formatCurrency(delivery)
            updateUI()
        }

        viewModel.total.observe(this) { total ->
            binding.tvTotal.text = formatCurrency(total)
            binding.tvTotalBottom.text = formatCurrency(total)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }

        viewModel.successMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }

        viewModel.orderCreated.observe(this) { order ->
            createdOrderId = order.id
            Log.d("CheckoutActivity", "Order created: ${order.id}")
        }

        viewModel.zaloPayResponse.observe(this) { zpResponse ->
            // Open ZaloPay payment URL
            openZaloPayPayment(zpResponse.orderUrl)
        }

        viewModel.paymentVerified.observe(this) { isVerified ->
            if (isVerified) {
                // Thanh toán thành công - navigate to order detail
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()

                binding.root.postDelayed({
                    createdOrderId?.let { orderId ->
                        navigateToOrderDetail(orderId)
                    }
                }, 1500)
            }
        }
    }

    private fun navigateToOrderDetail(orderId: Long) {
        val intent = Intent(this, OrderDetailActivity::class.java)
        intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, orderId)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun updateUI() {
        // Can add more UI updates here if needed
    }

    private fun placeOrder() {
        val address = binding.etAddress.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val note = binding.etNote.text.toString().trim()

        val paymentMethod = when {
            binding.rbZaloPay.isChecked -> "ZALO_PAY"
            binding.rbCOD.isChecked -> "COD"
            else -> "ZALO_PAY"
        }

        viewModel.createOrder(address, phone, note.ifEmpty { null }, paymentMethod)
    }

    private fun openZaloPayPayment(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)

            // After payment, user will return to app
            // You can use deep link or check payment status
            Toast.makeText(this, "Vui lòng hoàn tất thanh toán", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Không thể mở ZaloPay: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatCurrency(amount: BigDecimal): String {
        return "₫${String.format("%,d", amount.toLong())}"
    }

    override fun onResume() {
        super.onResume()
        // Nếu có order ID và chưa verify thì tự động verify
        createdOrderId?.let { orderId ->
            Log.d("CheckoutActivity", "Returned from payment, verifying order $orderId")
            // Delay 1 giây để đảm bảo ZaloPay đã process xong
            binding.root.postDelayed({
                viewModel.verifyPayment(orderId)
            }, 1000)
        }
    }
}