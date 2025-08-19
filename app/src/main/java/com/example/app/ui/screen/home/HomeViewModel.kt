package com.example.app.ui.screen.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// HomeViewModel现在只负责管理Tab切换相关的逻辑（如果有的话）
// 实际的数据加载逻辑都在子页面的ViewModel中
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    // 如果需要在Tab之间共享数据或状态，可以在这里管理
    // 目前保持简单，主要逻辑都在子页面中
}