package vn.edu.hcmuaf.fit.ttltmobile.data.model.admin

data class AdminMenuItem(
    val icon: String,
    val title: String,
    val subtitle: String,
    val action: () -> Unit
)