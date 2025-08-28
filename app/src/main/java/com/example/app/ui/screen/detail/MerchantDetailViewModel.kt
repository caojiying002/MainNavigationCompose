package com.example.app.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MerchantDetailUiState(
    val merchantName: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class MerchantDetailViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(MerchantDetailUiState())
    val uiState: StateFlow<MerchantDetailUiState> = _uiState.asStateFlow()
    
    fun loadMerchantDetail(merchantId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(1000) // 模拟加载
            _uiState.value = _uiState.value.copy(
                merchantName = "商家 #$merchantId",
                isLoading = false
            )
        }
    }
}