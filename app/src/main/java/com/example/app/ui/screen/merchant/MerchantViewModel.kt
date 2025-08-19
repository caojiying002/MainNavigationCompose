// ===== com/example/app/ui/screen/merchant/MerchantViewModel.kt =====
package com.example.app.ui.screen.merchant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Merchant(
    val id: String,
    val name: String,
    val category: String,
    val rating: Float,
    val distance: String,
    val promotion: String
)

sealed class MerchantIntent {
    object LoadMerchants : MerchantIntent()
    data class OpenMerchant(val merchantId: String) : MerchantIntent()
    data class SearchMerchant(val query: String) : MerchantIntent()
}

data class MerchantUiState(
    val merchants: List<Merchant> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = ""
)

@HiltViewModel
class MerchantViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MerchantUiState())
    val uiState: StateFlow<MerchantUiState> = _uiState.asStateFlow()

    fun processIntent(intent: MerchantIntent) {
        when (intent) {
            is MerchantIntent.LoadMerchants -> loadMerchants()
            is MerchantIntent.OpenMerchant -> openMerchant(intent.merchantId)
            is MerchantIntent.SearchMerchant -> searchMerchant(intent.query)
        }
    }

    private fun loadMerchants() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000)

            val mockData = listOf(
                Merchant("1", "老王烧烤", "烧烤", 4.8f, "500m", "满50减10"),
                Merchant("2", "张姐火锅", "火锅", 4.5f, "1.2km", "88折优惠"),
                Merchant("3", "东北饺子馆", "中餐", 4.6f, "800m", "新客立减15"),
                Merchant("4", "星巴克", "咖啡", 4.3f, "300m", "买一送一"),
                Merchant("5", "麦当劳", "快餐", 4.2f, "1.5km", "早餐特惠")
            )

            _uiState.update {
                it.copy(merchants = mockData, isLoading = false)
            }
        }
    }

    private fun openMerchant(merchantId: String) {
        // 处理打开商家详情
    }

    private fun searchMerchant(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        // 实现搜索逻辑
    }
}