package vn.edu.hcmuaf.fit.ttltmobile.ui.order

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityOrderHistoryBinding
import vn.edu.hcmuaf.fit.ttltmobile.data.model.order.OrderHistoryResponse

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var viewModel: OrderHistoryViewModel
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[OrderHistoryViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        viewModel.loadOrderHistory()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        orderHistoryAdapter = OrderHistoryAdapter { order ->
            navigateToOrderDetail(order.id)
        }

        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
            adapter = orderHistoryAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.orderHistory.observe(this) { orders ->
            if (orders.isEmpty()) {
                showEmptyState()
            } else {
                showOrders(orders)
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
    }

    private fun showEmptyState() {
        binding.rvOrders.visibility = View.GONE
        binding.tvEmptyState.visibility = View.VISIBLE
    }

    private fun showOrders(orders: List<OrderHistoryResponse>) {
        binding.rvOrders.visibility = View.VISIBLE
        binding.tvEmptyState.visibility = View.GONE
        orderHistoryAdapter.submitList(orders)
    }

    private fun navigateToOrderDetail(orderId: Long) {
        val intent = Intent(this, OrderDetailActivity::class.java)
        intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, orderId)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Reload orders when returning from detail
        viewModel.loadOrderHistory()
    }
}