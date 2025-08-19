package com.example.app.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserInfo(
    val id: String,
    val name: String,
    val phone: String
)

sealed class ProfileIntent {
    object LoadProfile : ProfileIntent()
    data class OpenMenuItem(val menuName: String) : ProfileIntent()
    object Logout : ProfileIntent()
}

data class ProfileUiState(
    val userInfo: UserInfo? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun processIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadProfile -> loadProfile()
            is ProfileIntent.OpenMenuItem -> openMenuItem(intent.menuName)
            is ProfileIntent.Logout -> logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(800)

            val mockUser = UserInfo(
                id = "user_001",
                name = "张三",
                phone = "138****8888"
            )

            _uiState.update {
                it.copy(userInfo = mockUser, isLoading = false)
            }
        }
    }

    private fun openMenuItem(menuName: String) {
        // 处理菜单点击
    }

    private fun logout() {
        _uiState.update { it.copy(userInfo = null) }
    }
}