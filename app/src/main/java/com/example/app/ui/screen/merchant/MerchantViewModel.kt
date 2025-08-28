package com.example.app.ui.screen.merchant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// 数据模型
data class Merchant(
    val id: String,
    val name: String,
    val category: String,
    val rating: Float,
    val distance: String,
    val promotion: String,
    val imageUrl: String? = null
)

// MVI - Intent
sealed class MerchantIntent {
    object LoadMerchants : MerchantIntent()
    data class OpenMerchant(val merchantId: String) : MerchantIntent()
    data class SearchMerchant(val query: String) : MerchantIntent()
    object RefreshMerchants : MerchantIntent()
    data class FilterByCategory(val category: String) : MerchantIntent()
}

// MVI - State
data class MerchantUiState(
    val merchants: List<Merchant> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val errorMessage: String? = null
)

// MVI - Effect
sealed class MerchantEffect {
    data class NavigateToDetail(val merchantId: String) : MerchantEffect()
    data class ShowToast(val message: String) : MerchantEffect()
}

@HiltViewModel
class MerchantViewModel @Inject constructor(
    // private val merchantRepository: MerchantRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MerchantUiState())
    val uiState: StateFlow<MerchantUiState> = _uiState.asStateFlow()
    
    private val _effect = MutableSharedFlow<MerchantEffect>()
    val effect: SharedFlow<MerchantEffect> = _effect.asSharedFlow()
    
    // 全部商家列表（用于搜索过滤）
    private var allMerchants: List<Merchant> = emptyList()
    
    fun processIntent(intent: MerchantIntent) {
        when (intent) {
            is MerchantIntent.LoadMerchants -> loadMerchants()
            is MerchantIntent.OpenMerchant -> openMerchant(intent.merchantId)
            is MerchantIntent.SearchMerchant -> searchMerchant(intent.query)
            is MerchantIntent.RefreshMerchants -> refreshMerchants()
            is MerchantIntent.FilterByCategory -> filterByCategory(intent.category)
        }
    }
    
    private fun loadMerchants() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // 模拟网络请求
                delay(1000)
                
                // 实际项目中：val merchants = merchantRepository.getNearbyMerchants()
                
                val mockData = generateMockMerchants()
                allMerchants = mockData
                
                _uiState.update { 
                    it.copy(
                        merchants = mockData, 
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
                _effect.emit(MerchantEffect.ShowToast("加载失败：${e.message}"))
            }
        }
    }
    
    private fun openMerchant(merchantId: String) {
        viewModelScope.launch {
            // 记录用户点击行为（可选）
            // merchantRepository.recordClick(merchantId)
            
            // 发送导航事件
            _effect.emit(MerchantEffect.NavigateToDetail(merchantId))
        }
    }
    
    private fun searchMerchant(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        
        if (query.isEmpty()) {
            _uiState.update { it.copy(merchants = allMerchants) }
        } else {
            val filteredMerchants = allMerchants.filter { merchant ->
                merchant.name.contains(query, ignoreCase = true) ||
                merchant.category.contains(query, ignoreCase = true)
            }
            _uiState.update { it.copy(merchants = filteredMerchants) }
        }
    }
    
    private fun refreshMerchants() {
        loadMerchants()
    }
    
    private fun filterByCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        
        val filteredMerchants = if (category.isEmpty()) {
            allMerchants
        } else {
            allMerchants.filter { it.category == category }
        }
        
        _uiState.update { it.copy(merchants = filteredMerchants) }
    }
    
    private fun generateMockMerchants(): List<Merchant> {
        return listOf(
            Merchant(
                id = "m001",
                name = "老王烧烤",
                category = "烧烤",
                rating = 4.8f,
                distance = "500m",
                promotion = "满50减10"
            ),
            Merchant(
                id = "m002",
                name = "张姐火锅",
                category = "火锅",
                rating = 4.5f,
                distance = "1.2km",
                promotion = "88折优惠"
            ),
            Merchant(
                id = "m003",
                name = "东北饺子馆",
                category = "中餐",
                rating = 4.6f,
                distance = "800m",
                promotion = "新客立减15"
            ),
            Merchant(
                id = "m004",
                name = "星巴克",
                category = "咖啡",
                rating = 4.3f,
                distance = "300m",
                promotion = "买一送一"
            ),
            Merchant(
                id = "m005",
                name = "麦当劳",
                category = "快餐",
                rating = 4.2f,
                distance = "1.5km",
                promotion = "早餐特惠"
            ),
            Merchant(
                id = "m006",
                name = "海底捞",
                category = "火锅",
                rating = 4.9f,
                distance = "2.3km",
                promotion = "生日8折"
            ),
            Merchant(
                id = "m007",
                name = "必胜客",
                category = "西餐",
                rating = 4.1f,
                distance = "900m",
                promotion = "工作日午餐优惠"
            ),
            Merchant(
                id = "m008",
                name = "兰州拉面",
                category = "面食",
                rating = 4.4f,
                distance = "600m",
                promotion = ""
            ),
            Merchant(
                id = "m009",
                name = "CoCo奶茶",
                category = "饮品",
                rating = 4.0f,
                distance = "400m",
                promotion = "第二杯半价"
            ),
            Merchant(
                id = "m010",
                name = "沙县小吃",
                category = "快餐",
                rating = 3.9f,
                distance = "350m",
                promotion = ""
            )
        )
    }
}