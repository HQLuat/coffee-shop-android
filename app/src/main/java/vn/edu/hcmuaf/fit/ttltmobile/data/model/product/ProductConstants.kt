package vn.edu.hcmuaf.fit.ttltmobile.data.model.product

object ProductConstants {
    // map category
    val categoryLabels = arrayOf("Cà phê", "Trà", "Trà sữa", "Đá xay", "Khác")
    val categoryEnums = arrayOf("COFFEE", "TEA", "MILKTEA", "FREEZE", "OTHER")

    fun getCategoryLabel(enumValue: String?): String {
        val index = categoryEnums.indexOf(enumValue)
        return if (index != -1) categoryLabels[index] else ""
    }

    fun getCategoryEnum(label: String): String {
        val index = categoryLabels.indexOf(label)
        return if (index != -1) categoryEnums[index] else "OTHER"
    }

    // map size
    val sizeLabels = arrayOf("Nhỏ (S)", "Vừa (M)", "Lớn (L)")
    val sizeEnums = arrayOf("S", "M", "L")

    fun getSizeLabel(enumValue: String?): String {
        val index = sizeEnums.indexOf(enumValue)
        return if (index != -1) sizeLabels[index] else ""
    }

    fun getSizeEnum(label: String): String {
        val index = sizeLabels.indexOf(label)
        return if (index != -1) sizeEnums[index] else "M"
    }
}