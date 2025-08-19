// ===== MessageViewModel.kt =====
package com.example.app.ui.screen.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// 消息数据模型
data class Message(
    val id: String,
    val senderName: String,
    val content: String,
    val time: String,
    val unreadCount: Int = 0
)

// MVI - Intent
sealed class MessageIntent {
    object LoadMessages : MessageIntent()
    data class OpenMessage(val messageId: String) : MessageIntent()
    object RefreshMessages : MessageIntent()
}

// MVI - State
data class MessageUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// MVI - Effect
sealed class MessageEffect {
    data class NavigateToChat(val messageId: String) : MessageEffect()
    data class ShowToast(val message: String) : MessageEffect()
}

@HiltViewModel
class MessageViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MessageUiState())
    val uiState: StateFlow<MessageUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<MessageEffect>()
    val effect: SharedFlow<MessageEffect> = _effect.asSharedFlow()

    fun processIntent(intent: MessageIntent) {
        when (intent) {
            is MessageIntent.LoadMessages -> loadMessages()
            is MessageIntent.OpenMessage -> openMessage(intent.messageId)
            is MessageIntent.RefreshMessages -> refreshMessages()
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // 模拟网络请求
                delay(1000)

                // 模拟数据
                val mockMessages = listOf(
                    Message("1", "系统通知", "您有新的优惠券可以领取", "10:30", 2),
                    Message("2", "张三", "今晚一起吃饭吗？", "昨天", 0),
                    Message("3", "李四", "文件已发送，请查收", "昨天", 1),
                    Message("4", "王五", "会议改到明天下午3点", "2天前", 0),
                    Message("5", "客服小助手", "您的订单已发货", "3天前", 0),
                    Message("6", "团队群", "项目进度更新", "一周前", 5)
                )

                _uiState.update {
                    it.copy(
                        messages = mockMessages,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
                _effect.emit(MessageEffect.ShowToast("加载失败：${e.message}"))
            }
        }
    }

    private fun openMessage(messageId: String) {
        viewModelScope.launch {
            // 标记消息为已读
            _uiState.update { state ->
                state.copy(
                    messages = state.messages.map { message ->
                        if (message.id == messageId) {
                            message.copy(unreadCount = 0)
                        } else {
                            message
                        }
                    }
                )
            }

            // 发送导航事件
            _effect.emit(MessageEffect.NavigateToChat(messageId))
        }
    }

    private fun refreshMessages() {
        loadMessages()
    }
}