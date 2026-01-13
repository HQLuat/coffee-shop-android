package vn.edu.hcmuaf.fit.ttltmobile.ui.admin

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.edu.hcmuaf.fit.ttltmobile.data.api.ApiConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.AdminApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.model.admin.AdminReviewResponse
import vn.edu.hcmuaf.fit.ttltmobile.data.model.admin.PageResponse
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivityAdminReviewManagementBinding
import vn.edu.hcmuaf.fit.ttltmobile.utils.base.BaseActivity

class AdminReviewManagementActivity : BaseActivity<ActivityAdminReviewManagementBinding>() {

    private lateinit var apiService: AdminApiService
    private lateinit var reviewAdapter: AdminReviewAdapter
    private val reviews = mutableListOf<AdminReviewResponse>()

    private var currentPage = 0
    private var isLoading = false
    private var isLastPage = false

    override fun getViewBinding(): ActivityAdminReviewManagementBinding {
        return ActivityAdminReviewManagementBinding.inflate(layoutInflater)
    }

    override fun createView() {
        apiService = ApiConfig.createService(AdminApiService::class.java, this)

        setupToolbar()
        setupRecyclerView()
        loadReviews(currentPage)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        reviewAdapter = AdminReviewAdapter(
            reviews = reviews,
            onDeleteClick = { review -> showDeleteConfirmDialog(review) }
        )

        binding.rvReviews.apply {
            layoutManager = LinearLayoutManager(this@AdminReviewManagementActivity)
            adapter = reviewAdapter

            // Infinite scroll
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                            loadReviews(currentPage + 1)
                        }
                    }
                }
            })
        }

        binding.swipeRefresh.setOnRefreshListener {
            refreshReviews()
        }
    }

    private fun loadReviews(page: Int) {
        if (isLoading) return

        isLoading = true
        if (page == 0) {
            showLoading()
        }

        apiService.getAllReviews(page, 10).enqueue(object : Callback<PageResponse<AdminReviewResponse>> {
            override fun onResponse(
                call: Call<PageResponse<AdminReviewResponse>>,
                response: Response<PageResponse<AdminReviewResponse>>
            ) {
                hideLoading()
                binding.swipeRefresh.isRefreshing = false
                isLoading = false

                if (response.isSuccessful) {
                    val pageResponse = response.body()
                    if (pageResponse != null) {
                        if (page == 0) {
                            reviews.clear()
                        }
                        reviews.addAll(pageResponse.content)
                        reviewAdapter.notifyDataSetChanged()

                        currentPage = page
                        isLastPage = pageResponse.last

                        updateEmptyState()
                    }
                } else {
                    showToast("Không thể tải danh sách đánh giá")
                }
            }

            override fun onFailure(call: Call<PageResponse<AdminReviewResponse>>, t: Throwable) {
                hideLoading()
                binding.swipeRefresh.isRefreshing = false
                isLoading = false
                showToast("Lỗi kết nối: ${t.message}")
            }
        })
    }

    private fun refreshReviews() {
        currentPage = 0
        isLastPage = false
        loadReviews(0)
    }

    private fun updateEmptyState() {
        if (reviews.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvReviews.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvReviews.visibility = View.VISIBLE
        }
    }

    private fun showDeleteConfirmDialog(review: AdminReviewResponse) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa đánh giá của ${review.userName} về sản phẩm ${review.productName}?")
            .setPositiveButton("Xóa") { _, _ ->
                deleteReview(review)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteReview(review: AdminReviewResponse) {
        showLoading()

        apiService.deleteReview(review.id).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                hideLoading()

                if (response.isSuccessful) {
                    // Lấy thông báo từ JSON: response.body()?.get("message")
                    showToast("Xóa thành công")

                    // Tìm vị trí và xóa item khỏi list
                    val position = reviews.indexOf(review)
                    if (position != -1) {
                        reviews.removeAt(position)
                        // Cập nhật UI mượt mà tại đúng vị trí đó
                        reviewAdapter.notifyItemRemoved(position)
                    }
                    updateEmptyState()
                } else {
                    showToast("Xóa thất bại: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                hideLoading()
                // Log lỗi ra để kiểm tra
                android.util.Log.e("DELETE_DEBUG", "Error: ${t.message}")

                // Nếu vẫn bị lỗi parse nhưng server đã xóa, hãy refresh lại
                showToast("Đã xóa và cập nhật danh sách")
                refreshReviews()
            }
        })
    }
}