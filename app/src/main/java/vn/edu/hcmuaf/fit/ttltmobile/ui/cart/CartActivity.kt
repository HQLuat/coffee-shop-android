package vn.edu.hcmuaf.fit.ttltmobile.ui.cart

import androidx.appcompat.app.AppCompatActivity

//import android.os.Bundle
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityCartBinding
//import vn.edu.hcmuaf.fit.ttltmobile.utils.ChangeNumberItemsListener
//import vn.edu.hcmuaf.fit.ttltmobile.utils.ManagmentCart

class CartActivity : AppCompatActivity() {
//    lateinit var binding: ActivityCartBinding
//    lateinit var managmentCart: ManagmentCart
//    private var tax: Double = 0.0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        binding = ActivityCartBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        managmentCart = ManagmentCart(this)
//
//        calculateCart()
//        setVariable()
//        initCartList()
//    }
//
//    private fun initCartList() {
//        binding.apply {
//            listView.layoutManager =
//                LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL, false)
//            listView.adapter = CartAdapter(
//                managmentCart.getListCart(),
//                this@CartActivity,
//                object : ChangeNumberItemsListener{
//                    override fun onChanged() {
//                        calculateCart()
//                    }
//
//                }
//            )
//        }
//    }
//
//    private fun setVariable() {
//        binding.backBtn.setOnClickListener { finish() }
//    }
//
//    private fun calculateCart() {
//        val percentTax = 0.02
//        val delivery = 15
//        tax = Math.round((managmentCart.getTotalFee() * percentTax) * 100) / 100.0
//        val total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100
//        val itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100
//        binding.apply {
//            totalFeetxt.text = "$$itemTotal"
//            taxTxt.text = "$$tax"
//            deliveryTxt.text = "$$delivery"
//            totalTxt.text = "$$total"
//        }
//    }
}