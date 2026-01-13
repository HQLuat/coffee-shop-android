package vn.edu.hcmuaf.fit.ttltmobile.ui.order

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityOrderDetailBinding
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.OrderStatus
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var viewModel: OrderDetailViewModel
    private lateinit var orderItemsAdapter: OrderItemsAdapter
    private lateinit var refundAdapter: RefundAdapter

    private var orderId: Long = 0

    companion object {
        const val EXTRA_ORDER_ID = "order_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderId = intent.getLongExtra(EXTRA_ORDER_ID, 0)
        if (orderId == 0L) {
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[OrderDetailViewModel::class.java]

        setupToolbar()
        setupRecyclerViews()
        setupListeners()
        observeViewModel()

        viewModel.loadOrderDetail(orderId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerViews() {
        orderItemsAdapter = OrderItemsAdapter()
        binding.rvOrderItems.apply {
            layoutManager = LinearLayoutManager(this@OrderDetailActivity)
            adapter = orderItemsAdapter
        }

        refundAdapter = RefundAdapter()
        binding.rvRefunds.apply {
            layoutManager = LinearLayoutManager(this@OrderDetailActivity)
            adapter = refundAdapter
        }
    }

    private fun setupListeners() {
        binding.btnCancelOrder.setOnClickListener {
            showCancelOrderDialog()
        }

        binding.btnPayNow.setOnClickListener {
            viewModel.createZaloPayPayment(orderId)
        }

        binding.btnRefund.setOnClickListener {
            showRefundDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.orderDetail.observe(this) { order ->
            updateUI(order)
        }

        viewModel.refundHistory.observe(this) { refunds ->
            if (refunds.isNotEmpty()) {
                binding.llRefundSection.visibility = View.VISIBLE
                refundAdapter.submitList(refunds)
            } else {
                binding.llRefundSection.visibility = View.GONE
            }
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
                if (message.startsWith("ZALOPAY_URL:")) {
                    val url = message.substringAfter("ZALOPAY_URL:")
                    openZaloPayPayment(url)
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
                viewModel.clearMessages()
            }
        }
    }

    private fun updateUI(order: vn.edu.hcmuaf.fit.ttltmobile.data.model.order.OrderResponse) {
        binding.tvOrderCode.text = "Mã đơn: ${order.orderCode}"
        binding.tvOrderDate.text = "Ngày đặt: ${formatDate(order.createdAt)}"

        val statusText = order.statusDisplay ?: when (order.status) {
            OrderStatus.PENDING -> "Chờ xác nhận"
            OrderStatus.CONFIRMED -> "Đã xác nhận"
            OrderStatus.PREPARING -> "Đang chuẩn bị"
            OrderStatus.SHIPPING -> "Đang giao"
            OrderStatus.DELIVERED -> "Hoàn thành"
            OrderStatus.REFUNDED -> "Đã hoàn tiền"
            OrderStatus.CANCELLED -> "Đã hủy"
        }
        binding.tvOrderStatus.text = "Trạng thái: $statusText"

        val isPaid = order.isPaid()
        val paymentStatusText = when {
            order.status == OrderStatus.REFUNDED -> "Đã hoàn tiền"
            isPaid -> "Đã thanh toán"
            else -> "Chưa thanh toán"
        }
        binding.tvPaymentStatus.text = paymentStatusText

        val paymentColor = when {
            order.status == OrderStatus.REFUNDED -> android.R.color.holo_orange_dark
            isPaid -> android.R.color.holo_green_dark
            else -> android.R.color.holo_red_dark
        }
        binding.tvPaymentStatus.setTextColor(getColor(paymentColor))

        binding.tvDeliveryAddress.text = "Địa chỉ: ${order.deliveryAddress}"
        binding.tvPhoneNumber.text = "SĐT: ${order.phoneNumber}"

        if (!order.note.isNullOrEmpty()) {
            binding.tvNote.visibility = View.VISIBLE
            binding.tvNote.text = "Ghi chú: ${order.note}"
        } else {
            binding.tvNote.visibility = View.GONE
        }

        orderItemsAdapter.submitList(order.items)

        binding.tvItemsTotal.text = order.getTotalFormatted()
        binding.tvTotalAmount.text = order.getTotalFormatted()

        updateButtonsVisibility(order)
    }

    private fun updateButtonsVisibility(order: vn.edu.hcmuaf.fit.ttltmobile.data.model.order.OrderResponse) {
        val showCancel = order.canCancel()
        binding.btnCancelOrder.visibility = if (showCancel) View.VISIBLE else View.GONE

        val showPayNow = !order.isPaid() && order.status == OrderStatus.PENDING
        binding.btnPayNow.visibility = if (showPayNow) View.VISIBLE else View.GONE

        val showRefund = order.canRefund()
        binding.btnRefund.visibility = if (showRefund) View.VISIBLE else View.GONE
    }

    private fun showCancelOrderDialog() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận hủy đơn")
            .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này?")
            .setPositiveButton("Hủy đơn") { _, _ ->
                viewModel.cancelOrder(orderId)
            }
            .setNegativeButton("Đóng", null)
            .show()
    }

    private fun showRefundDialog() {
        val order = viewModel.orderDetail.value ?: return

        AlertDialog.Builder(this)
            .setTitle("Hoàn tiền")
            .setMessage("Bạn có muốn hoàn tiền toàn bộ đơn hàng?\n\nSố tiền: ${order.getTotalFormatted()}")
            .setPositiveButton("Hoàn tiền") { _, _ ->
                viewModel.refundOrder(
                    orderId = orderId,
                    amount = order.totalAmount,
                    description = "Hoàn tiền đơn hàng ${order.orderCode}"
                )
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun openZaloPayPayment(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
            Toast.makeText(this, "Vui lòng hoàn tất thanh toán", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Không thể mở ZaloPay: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadOrderDetail(orderId)
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopPolling()
    }
}