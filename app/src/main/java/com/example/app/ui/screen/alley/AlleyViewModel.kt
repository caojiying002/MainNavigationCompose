// ===== com/example/app/ui/screen/alley/AlleyViewModel.kt =====
package com.example.app.ui.screen.alley

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlleyItem(
    val id: String,
    val title: String,
    val description: String
)

sealed class AlleyIntent {
    object LoadAlleyData : AlleyIntent()
    data class OpenItem(val itemId: String) : AlleyIntent()
}

data class AlleyUiState(
    val items: List<AlleyItem> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class AlleyViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AlleyUiState())
    val uiState: StateFlow<AlleyUiState> = _uiState.asStateFlow()

    fun processIntent(intent: AlleyIntent) {
        when (intent) {
            is AlleyIntent.LoadAlleyData -> loadData()
            is AlleyIntent.OpenItem -> openItem(intent.itemId)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1200)

            val mockData = List(10) { index ->
                AlleyItem(
                    id = "alley_$index",
                    title = "神秘物品 ${index + 1}",
                    description = "这是一个来自暗巷深处的神秘物品..."
                )
            }

            _uiState.update {
                it.copy(items = mockData, isLoading = false)
            }
        }
    }

    private fun openItem(itemId: String) {
        // 处理打开物品逻辑
    }
}