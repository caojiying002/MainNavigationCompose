// com/example/app/ui/screen/main/MainScreen.kt
package com.example.app.ui.screen.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.app.ui.components.CustomBottomBar
import com.example.app.ui.screen.alley.AlleyScreen
import com.example.app.ui.screen.home.HomeScreen
import com.example.app.ui.screen.merchant.MerchantScreen
import com.example.app.ui.screen.message.MessageScreen
import com.example.app.ui.screen.profile.ProfileScreen

// 定义Tab枚举
enum class MainTab(val title: String, val icon: String) {
    HOME("首页", "home"),
    MESSAGE("信息", "message"),
    ALLEY("暗巷", "alley"),
    MERCHANT("商家", "merchant"),
    PROFILE("我的", "profile")
}

@Composable
fun MainScreen() {
    // 记录当前选中的Tab索引
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    // 记录每个Tab是否已经被访问过（用于懒加载）
    val visitedTabs = rememberSaveable { mutableSetOf<Int>() }

    // 当Tab切换时记录访问状态
    LaunchedEffect(selectedTabIndex) {
        visitedTabs.add(selectedTabIndex)
    }

    Scaffold(
        bottomBar = {
            CustomBottomBar(
                tabs = MainTab.values().toList(),
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index ->
                    selectedTabIndex = index
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 使用AnimatedContent实现页面切换动画
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "tab_transition"
            ) { targetIndex ->
                // 判断是否首次访问
                val isFirstTime = (targetIndex == 0 && visitedTabs.isEmpty()) ||
                        !visitedTabs.contains(targetIndex)

                when (targetIndex) {
                    0 -> HomeScreen(
                        isFirstTimeVisible = isFirstTime,
                        viewModel = hiltViewModel()
                    )
                    1 -> MessageScreen(
                        isFirstTimeVisible = isFirstTime,
                        viewModel = hiltViewModel()
                    )
                    2 -> AlleyScreen(
                        isFirstTimeVisible = isFirstTime,
                        viewModel = hiltViewModel()
                    )
                    3 -> MerchantScreen(
                        isFirstTimeVisible = isFirstTime,
                        viewModel = hiltViewModel()
                    )
                    4 -> ProfileScreen(
                        isFirstTimeVisible = isFirstTime,
                        viewModel = hiltViewModel()
                    )
                }
            }
        }
    }
}