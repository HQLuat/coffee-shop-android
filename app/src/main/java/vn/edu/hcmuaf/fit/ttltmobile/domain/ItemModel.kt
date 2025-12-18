package vn.edu.hcmuaf.fit.ttltmobile.domain

import java.io.Serializable

class ItemModel(
    var title: String = "",
    var description: String = "",
    val picUrl: ArrayList<String> = ArrayList(),
    val price: Double = 0.0,
    var rating: Double = 0.0,
    var numberInCart: Int = 0,
    var extra: String = ""
) : Serializable