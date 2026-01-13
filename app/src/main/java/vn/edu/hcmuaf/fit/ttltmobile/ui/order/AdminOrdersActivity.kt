package vn.edu.hcmuaf.fit.ttltmobile.ui.admin.orders

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityAdminOrdersBinding
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.OrderResponse
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.OrderStatus
import vn.edu.hcmuaf.fit.ttltmobile.ui.order.OrderDetailActivity

class AdminOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminOrdersBinding
    private lateinit var viewModel: AdminOrderViewModel
    private lateinit var orderAdapter: AdminOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[AdminOrderViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        orderAdapter = AdminOrderAdapter(
            onOrderClick = { order ->
                navigateToOrderDetail(order.id)
            },
            onUpdateStatus = { order ->
                showUpdateStatusDialog(order)
            },
            onDeleteOrder = { order ->
                showDeleteOrderDialog(order)
            }
        )

        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(this@AdminOrdersActivity)
            adapter = orderAdapter
        }
    }

    private fun setupListeners() {
        // Swipe to refresh
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        // Search
        binding.etSearch.addTextChangedListener { text ->
            val keyword = text.toString()
            if (keyword.length >= 3 || keyword.isEmpty()) {
                viewModel.searchOrders(keyword)
            }
        }

        // Filter button
        binding.btnFilter.setOnClickListener {
            // TODO: Show filter dialog
            Toast.makeText(this, "Filter coming soon", Toast.LENGTH_SHORT).show()
        }

        // Status chips
        binding.chipGroupStatus.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) {
                viewModel.loadAllOrders()
                return@setOnCheckedStateChangeListener
            }

            val checkedId = checkedIds.first()
            val status = when (checkedId) {
                binding.chipAll.id -> null
                binding.chipPending.id -> OrderStatus.PENDING
                binding.chipConfirmed.id -> OrderStatus.CONFIRMED
                binding.chipDelivering.id -> OrderStatus.SHIPPING
                binding.chipCancelled.id -> OrderStatus.CANCELLED
                binding.chipCancelled.id -> OrderStatus.REFUNDED
                else -> null
            }

            viewModel.loadOrdersByStatus(status)
        }
    }

    private fun observeViewModel() {
        viewModel.orders.observe(this) { orders ->
            if (orders.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.rvOrders.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.GONE
                binding.rvOrders.visibility = View.VISIBLE
                orderAdapter.submitList(orders)
            }
        }

        viewModel.statistics.observe(this) { stats ->
            binding.tvTotalOrders.text = stats.totalOrders.toString()
            binding.tvPendingOrders.text = stats.pendingOrders.toString()
            binding.tvTotalRevenue.text = "₫${String.format("%,d", stats.totalRevenue.toLong())}"
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                if (!binding.swipeRefresh.isRefreshing) {
                    binding.loadingOverlay.visibility = View.VISIBLE
                }
            } else {
                binding.loadingOverlay.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
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
    }

    private fun showUpdateStatusDialog(order: OrderResponse) {
        val statuses = OrderStatus.values()
        val statusNames = statuses.map { getStatusText(it) }.toTypedArray()
        val currentIndex = statuses.indexOf(order.status)

        AlertDialog.Builder(this)
            .setTitle("Cập nhật trạng thái đơn hàng")
            .setSingleChoiceItems(statusNames, currentIndex) { dialog, which ->
                val newStatus = statuses[which]
                if (newStatus != order.status) {
                    viewModel.updateOrderStatus(order.id, newStatus)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showDeleteOrderDialog(order: OrderResponse) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa đơn hàng ${order.orderCode}?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.deleteOrder(order.id)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun navigateToOrderDetail(orderId: Long) {
        val intent = Intent(this, OrderDetailActivity::class.java)
        intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, orderId)
        startActivity(intent)
    }

    private fun getStatusText(status: OrderStatus): String {
        return when (status) {
            OrderStatus.PENDING -> "Đang chờ"
            OrderStatus.CONFIRMED -> "Đã xác nhận"
            OrderStatus.PREPARING -> "Đang chuẩn bị"
            OrderStatus.SHIPPING -> "Đang giao"
            OrderStatus.DELIVERED -> "Đã giao"
            OrderStatus.CANCELLED -> "Đã hủy"
            OrderStatus.REFUNDED -> TODO()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}