package com.example.app.ui.screen.home.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// MVI - Intent
sealed class RecommendIntent {
    object LoadData : RecommendIntent()
    object Refresh : RecommendIntent()
    object LoadMore : RecommendIntent()
    data class LikeItem(val itemId: String) : RecommendIntent()
    data class ShareItem(val itemId: String) : RecommendIntent()
}

// MVI - State
data class RecommendUiState(
    val items: List<RecommendItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreData: Boolean = true,
    val errorMessage: String? = null
)

// MVI - Effect
sealed class RecommendEffect {
    data class ShowToast(val message: String) : RecommendEffect()
    data class NavigateToDetail(val itemId: String) : RecommendEffect()
}

// 数据模型
data class RecommendItem(
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val likeCount: Int,
    val isLiked: Boolean,
    val imageUrl: String? = null,
    val publishTime: String
)

@HiltViewModel
class RecommendViewModel @Inject constructor(
    // private val recommendRepository: RecommendRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecommendUiState())
    val uiState: StateFlow<RecommendUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<RecommendEffect>()
    val effect: SharedFlow<RecommendEffect> = _effect.asSharedFlow()

    private var currentPage = 0
    private val pageSize = 20

    fun processIntent(intent: RecommendIntent) {
        when (intent) {
            is RecommendIntent.LoadData -> loadInitialData()
            is RecommendIntent.Refresh -> refresh()
            is RecommendIntent.LoadMore -> loadMore()
            is RecommendIntent.LikeItem -> toggleLike(intent.itemId)
            is RecommendIntent.ShareItem -> shareItem(intent.itemId)
        }
    }

    private fun loadInitialData() {
        // 避免重复加载
        if (_uiState.value.items.isNotEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // 模拟网络请求
                delay(1500)

                // 实际项目调用: val response = recommendRepository.getRecommendList(0, pageSize)

                val mockData = generateMockData(0)

                _uiState.update {
                    it.copy(
                        items = mockData,
                        isLoading = false,
                        hasMoreData = mockData.size >= pageSize
                    )
                }

                currentPage = 0

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
                _effect.emit(RecommendEffect.ShowToast("加载失败：${e.message}"))
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            try {
                delay(1000)

                val mockData = generateMockData(0)

                _uiState.update {
                    it.copy(
                        items = mockData,
                        isRefreshing = false,
                        hasMoreData = true
                    )
                }

                currentPage = 0
                _effect.emit(RecommendEffect.ShowToast("刷新成功"))

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    private fun loadMore() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMoreData) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            try {
                delay(1000)

                val nextPage = currentPage + 1
                val moreData = generateMockData(nextPage)

                _uiState.update { state ->
                    state.copy(
                        items = state.items + moreData,
                        isLoadingMore = false,
                        hasMoreData = moreData.size >= pageSize
                    )
                }

                currentPage = nextPage

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    private fun toggleLike(itemId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    items = state.items.map { item ->
                        if (item.id == itemId) {
                            item.copy(
                                isLiked = !item.isLiked,
                                likeCount = if (item.isLiked) item.likeCount - 1 else item.likeCount + 1
                            )
                        } else item
                    }
                )
            }

            // 实际项目中调用API: recommendRepository.toggleLike(itemId)
        }
    }

    private fun shareItem(itemId: String) {
        viewModelScope.launch {
            _effect.emit(RecommendEffect.ShowToast("分享功能开发中"))
        }
    }

    private fun generateMockData(page: Int): List<RecommendItem> {
        val startIndex = page * pageSize
        return List(pageSize) { index ->
            val itemIndex = startIndex + index
            RecommendItem(
                id = "recommend_$itemIndex",
                title = "推荐内容 ${itemIndex + 1}",
                content = "这是一条精心推荐的内容，基于你的兴趣偏好和浏览历史...",
                author = "作者${(itemIndex % 10) + 1}",
                likeCount = (100..999).random(),
                isLiked = false,
                publishTime = "${(1..24).random()}小时前"
            )
        }
    }
}

