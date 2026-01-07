package vn.edu.hcmuaf.fit.ttltmobile.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import vn.edu.hcmuaf.fit.ttltmobile.databinding.FragmentCartBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseFragment

class CartFragment: BaseFragment<FragmentCartBinding>() {
    private lateinit var viewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCartBinding {
        return FragmentCartBinding.inflate(layoutInflater)

    }

    override fun setupView() {
        viewModel = ViewModelProvider(this)[CartViewModel::class.java]
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        viewModel.loadCart()
    }

    private fun setupListeners(){
        binding.apply {
            checkOutBtn.setOnClickListener{
                showToast("chức năng đang phát triển")
            }
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            items = mutableListOf(),
            onQuantityChange = { cartItemId, quantity, isIncrease ->
                if (isIncrease) {
                    viewModel.increaseQuantity(cartItemId, quantity)
                } else {
                    viewModel.decreaseQuantity(cartItemId, quantity)
                }
            },
            onRemoveItem = { cartItemId ->
                showRemoveItemDialog(cartItemId)
            }
        )

        binding.listView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }
    private fun showRemoveItemDialog(cartItemId: Long) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận")
            .setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.removeItem(cartItemId)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
    private fun observeViewModel() {
        viewModel.cartData.observe(this) { cart ->
            if (cart.isEmpty()) {
                showEmptyCart()
            } else {
                showCartWithItems(cart)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                showToast(message)
            }
        }

        viewModel.successMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                showToast(message)
            }
        }
    }
    private fun showEmptyCart() {
        binding.apply {
            listView.visibility = View.GONE
            totalFeetxt.text = "₫0"
            taxTxt.text = "₫0"
            deliveryTxt.text = "₫0"
            totalTxt.text = "₫0"
            checkOutBtn.isEnabled = false
        }
        showToast("Giỏ hàng trống")
    }
    private fun showCartWithItems(cart: vn.edu.hcmuaf.fit.ttltmobile.data.model.cart.CartResponse) {
        cartAdapter.updateItems(cart.items)

        val subtotal = cart.totalAmount
        val tax = subtotal.multiply(0.02.toBigDecimal())
        val delivery = 15000.toBigDecimal()
        val total = subtotal.add(tax).add(delivery)

        binding.apply {
            totalFeetxt.text = "₫${String.format("%,d", subtotal.toLong())}"
            taxTxt.text = "₫${String.format("%,d", tax.toLong())}"
            deliveryTxt.text = "₫${String.format("%,d", delivery.toLong())}"
            totalTxt.text = "₫${String.format("%,d", total.toLong())}"

            listView.visibility = View.VISIBLE
            checkOutBtn.isEnabled = true
        }
    }
}