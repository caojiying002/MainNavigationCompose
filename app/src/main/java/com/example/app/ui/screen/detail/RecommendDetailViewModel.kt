package com.example.app.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// 数据模型
data class RecommendDetail(
    val id: String,
    val title: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String?,
    val publishTime: String,
    val readCount: Int,
    val images: List<String>,
    val tags: List<String>,
    val isFollowing: Boolean
)

data class Comment(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val content: String,
    val time: String,
    val likeCount: Int,
    val isLiked: Boolean
)

// MVI - Intent
sealed class RecommendDetailIntent {
    object ToggleLike : RecommendDetailIntent()
    object ToggleBookmark : RecommendDetailIntent()
    object Share : RecommendDetailIntent()
    object SendComment : RecommendDetailIntent()
    data class UpdateCommentDraft(val text: String) : RecommendDetailIntent()
    data class LikeComment(val commentId: String) : RecommendDetailIntent()
    data class FollowAuthor(val authorId: String) : RecommendDetailIntent()
}

// MVI - State
data class RecommendDetailUiState(
    val detail: RecommendDetail? = null,
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val isLiked: Boolean = false,
    val likeCount: Int = 0,
    val isBookmarked: Boolean = false,
    val commentDraft: String = "",
    val errorMessage: String? = null
)

// MVI - Effect
sealed class RecommendDetailEffect {
    data class ShowToast(val message: String) : RecommendDetailEffect()
    object ShareContent : RecommendDetailEffect()
    object NavigateBack : RecommendDetailEffect()
}

@HiltViewModel
class RecommendDetailViewModel @Inject constructor(
    // private val recommendRepository: RecommendRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RecommendDetailUiState())
    val uiState: StateFlow<RecommendDetailUiState> = _uiState.asStateFlow()
    
    private val _effect = MutableSharedFlow<RecommendDetailEffect>()
    val effect: SharedFlow<RecommendDetailEffect> = _effect.asSharedFlow()
    
    fun loadDetail(itemId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // 模拟加载详情
                delay(1000)
                
                // 实际项目：
                // val detail = recommendRepository.getDetail(itemId)
                // val comments = recommendRepository.getComments(itemId)
                
                val mockDetail = RecommendDetail(
                    id = itemId,
                    title = "深入理解Jetpack Compose的状态管理",
                    content = """
                        Jetpack Compose 是 Android 的现代工具包，用于构建原生 UI。它简化并加快了 Android 上的 UI 开发。
                        
                        在本文中，我们将深入探讨 Compose 中的状态管理机制，包括：
                        
                        1. remember 和 rememberSaveable 的使用
                        2. State 和 MutableState 的区别
                        3. ViewModel 在 Compose 中的应用
                        4. 状态提升的最佳实践
                        5. 副作用的处理
                        
                        状态管理是构建响应式 UI 的核心。在 Compose 中，当状态发生变化时，使用该状态的可组合函数会自动重新组合（重新执行），从而更新 UI。
                        
                        让我们从最基本的 remember 函数开始...
                    """.trimIndent(),
                    authorId = "author_001",
                    authorName = "技术达人",
                    authorAvatar = null,
                    publishTime = "2小时前",
                    readCount = 1234,
                    images = listOf("image1", "image2"),
                    tags = listOf("Android", "Compose", "状态管理"),
                    isFollowing = false
                )
                
                val mockComments = listOf(
                    Comment(
                        id = "c1",
                        userId = "u1",
                        userName = "用户A",
                        userAvatar = null,
                        content = "写得很详细，学到了很多！",
                        time = "1小时前",
                        likeCount = 12,
                        isLiked = false
                    ),
                    Comment(
                        id = "c2",
                        userId = "u2",
                        userName = "用户B",
                        userAvatar = null,
                        content = "请问有源码示例吗？",
                        time = "30分钟前",
                        likeCount = 3,
                        isLiked = true
                    )
                )
                
                _uiState.update {
                    it.copy(
                        detail = mockDetail,
                        comments = mockComments,
                        isLoading = false,
                        likeCount = 888,
                        isLiked = false,
                        isBookmarked = false
                    )
                }
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
                _effect.emit(RecommendDetailEffect.ShowToast("加载失败"))
            }
        }
    }
    
    fun processIntent(intent: RecommendDetailIntent) {
        when (intent) {
            is RecommendDetailIntent.ToggleLike -> toggleLike()
            is RecommendDetailIntent.ToggleBookmark -> toggleBookmark()
            is RecommendDetailIntent.Share -> shareContent()
            is RecommendDetailIntent.SendComment -> sendComment()
            is RecommendDetailIntent.UpdateCommentDraft -> updateCommentDraft(intent.text)
            is RecommendDetailIntent.LikeComment -> likeComment(intent.commentId)
            is RecommendDetailIntent.FollowAuthor -> followAuthor(intent.authorId)
        }
    }
    
    private fun toggleLike() {
        viewModelScope.launch {
            val newLikedState = !_uiState.value.isLiked
            val newLikeCount = if (newLikedState) {
                _uiState.value.likeCount + 1
            } else {
                _uiState.value.likeCount - 1
            }
            
            _uiState.update {
                it.copy(
                    isLiked = newLikedState,
                    likeCount = newLikeCount
                )
            }
            
            // 实际项目：recommendRepository.toggleLike(detail.id)
        }
    }
    
    private fun toggleBookmark() {
        viewModelScope.launch {
            val newBookmarkState = !_uiState.value.isBookmarked
            _uiState.update { it.copy(isBookmarked = newBookmarkState) }
            
            _effect.emit(
                RecommendDetailEffect.ShowToast(
                    if (newBookmarkState) "已收藏" else "取消收藏"
                )
            )
            
            // 实际项目：recommendRepository.toggleBookmark(detail.id)
        }
    }
    
    private fun shareContent() {
        viewModelScope.launch {
            _effect.emit(RecommendDetailEffect.ShareContent)
        }
    }
    
    private fun sendComment() {
        val commentText = _uiState.value.commentDraft.trim()
        if (commentText.isEmpty()) return
        
        viewModelScope.launch {
            // 模拟发送评论
            val newComment = Comment(
                id = "c_new_${System.currentTimeMillis()}",
                userId = "current_user",
                userName = "我",
                userAvatar = null,
                content = commentText,
                time = "刚刚",
                likeCount = 0,
                isLiked = false
            )
            
            _uiState.update {
                it.copy(
                    comments = listOf(newComment) + it.comments,
                    commentDraft = ""
                )
            }
            
            _effect.emit(RecommendDetailEffect.ShowToast("评论成功"))
            
            // 实际项目：recommendRepository.postComment(detail.id, commentText)
        }
    }
    
    private fun updateCommentDraft(text: String) {
        _uiState.update { it.copy(commentDraft = text) }
    }
    
    private fun likeComment(commentId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    comments = state.comments.map { comment ->
                        if (comment.id == commentId) {
                            comment.copy(
                                isLiked = !comment.isLiked,
                                likeCount = if (!comment.isLiked) {
                                    comment.likeCount + 1
                                } else {
                                    comment.likeCount - 1
                                }
                            )
                        } else comment
                    }
                )
            }
            
            // 实际项目：recommendRepository.toggleCommentLike(commentId)
        }
    }
    
    private fun followAuthor(authorId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.detail?.let { detail ->
                    state.copy(
                        detail = detail.copy(isFollowing = !detail.isFollowing)
                    )
                } ?: state
            }
            
            // 实际项目：userRepository.toggleFollow(authorId)
        }
    }
}