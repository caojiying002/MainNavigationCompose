// ===== com/example/app/ui/screen/home/following/FollowingViewModel.kt =====
package com.example.app.ui.screen.home.following

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// MVI - Intent
sealed class FollowingIntent {
    object LoadData : FollowingIntent()
    object Refresh : FollowingIntent()
    object LoadMore : FollowingIntent()
    data class FollowUser(val userId: String) : FollowingIntent()
    data class UnfollowUser(val userId: String) : FollowingIntent()
}

// MVI - State
data class FollowingUiState(
    val posts: List<FollowingPost> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreData: Boolean = true,
    val errorMessage: String? = null
)

// MVI - Effect
sealed class FollowingEffect {
    data class ShowToast(val message: String) : FollowingEffect()
    data class NavigateToUserProfile(val userId: String) : FollowingEffect()
    data class NavigateToPostDetail(val postId: String) : FollowingEffect()
}

// 数据模型
data class FollowingPost(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val content: String,
    val images: List<String>,
    val isFollowing: Boolean,
    val commentCount: Int,
    val likeCount: Int,
    val publishTime: String
)

@HiltViewModel
class FollowingViewModel @Inject constructor(
    // private val followingRepository: FollowingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowingUiState())
    val uiState: StateFlow<FollowingUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<FollowingEffect>()
    val effect: SharedFlow<FollowingEffect> = _effect.asSharedFlow()

    private var currentPage = 0
    private val pageSize = 15

    fun processIntent(intent: FollowingIntent) {
        when (intent) {
            is FollowingIntent.LoadData -> loadInitialData()
            is FollowingIntent.Refresh -> refresh()
            is FollowingIntent.LoadMore -> loadMore()
            is FollowingIntent.FollowUser -> followUser(intent.userId)
            is FollowingIntent.UnfollowUser -> unfollowUser(intent.userId)
        }
    }

    private fun loadInitialData() {
        // 避免重复加载
        if (_uiState.value.posts.isNotEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                delay(1500)

                // 实际项目: val response = followingRepository.getFollowingPosts(0, pageSize)

                val mockData = generateMockPosts(0)

                _uiState.update {
                    it.copy(
                        posts = mockData,
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
                _effect.emit(FollowingEffect.ShowToast("加载失败：${e.message}"))
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            try {
                delay(1000)

                val mockData = generateMockPosts(0)

                _uiState.update {
                    it.copy(
                        posts = mockData,
                        isRefreshing = false,
                        hasMoreData = true
                    )
                }

                currentPage = 0
                _effect.emit(FollowingEffect.ShowToast("刷新成功"))

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
                val moreData = generateMockPosts(nextPage)

                _uiState.update { state ->
                    state.copy(
                        posts = state.posts + moreData,
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

    private fun followUser(userId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    posts = state.posts.map { post ->
                        if (post.userId == userId) {
                            post.copy(isFollowing = true)
                        } else post
                    }
                )
            }

            // 实际项目: followingRepository.followUser(userId)
            _effect.emit(FollowingEffect.ShowToast("关注成功"))
        }
    }

    private fun unfollowUser(userId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    posts = state.posts.map { post ->
                        if (post.userId == userId) {
                            post.copy(isFollowing = false)
                        } else post
                    }
                )
            }

            // 实际项目: followingRepository.unfollowUser(userId)
            _effect.emit(FollowingEffect.ShowToast("取消关注"))
        }
    }

    private fun generateMockPosts(page: Int): List<FollowingPost> {
        val startIndex = page * pageSize
        return List(pageSize) { index ->
            val itemIndex = startIndex + index
            val userId = "user_${(itemIndex % 20) + 1}"
            FollowingPost(
                id = "post_$itemIndex",
                userId = userId,
                userName = "用户${(itemIndex % 20) + 1}",
                userAvatar = null,
                content = "这是我关注的用户发布的动态内容 #${itemIndex + 1}，分享一些有趣的见闻...",
                images = if (itemIndex % 3 == 0) listOf("image1", "image2") else emptyList(),
                isFollowing = true,
                commentCount = (0..99).random(),
                likeCount = (10..999).random(),
                publishTime = "${(1..48).random()}小时前"
            )
        }
    }
}