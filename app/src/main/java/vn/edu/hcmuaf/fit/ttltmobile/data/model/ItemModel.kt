package vn.edu.hcmuaf.fit.ttltmobile.data.model

import java.io.Serializable

data class ItemModel(
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var extra: String = "",  // e.g., category or extra info
    var picUrl: MutableList<String> = mutableListOf(),
    var numberInCart: Int = 0
) : Serializable