package vn.edu.hcmuaf.fit.ttltmobile.data.model.admin

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val number: Int,
    val last: Boolean
)