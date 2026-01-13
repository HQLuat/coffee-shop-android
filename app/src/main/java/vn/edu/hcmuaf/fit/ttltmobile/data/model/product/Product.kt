package vn.edu.hcmuaf.fit.ttltmobile.data.model.product

import java.io.Serializable

data class Product(
    val id: Long,
    val name: String,
    val price: Double,
    val description: String?,
    val imageUrl: String,
    val category: String,
    val size: String,
    val rating: Double,

    // cho gio hang
    var numberInCart: Int = 0
) : Serializable