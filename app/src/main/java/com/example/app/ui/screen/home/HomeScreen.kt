// com/example/app/ui/screen/home/HomeScreen.kt
package com.example.app.ui.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.app.ui.screen.home.following.FollowingScreen
import com.example.app.ui.screen.home.recommend.RecommendScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    isFirstTimeVisible: Boolean,
    viewModel: HomeViewModel
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    // 记录子页面是否已访问过
    val visitedSubPages = rememberSaveable { mutableSetOf<Int>() }

    // 监听页面切换，记录访问状态
    LaunchedEffect(pagerState.currentPage) {
        visitedSubPages.add(pagerState.currentPage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部Tab指示器
        HomeTabRow(
            selectedTabIndex = pagerState.currentPage,
            onTabSelected = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )

        // 页面内容 - 每个子页面都有独立的ViewModel
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // 判断是否首次访问：初始页面(0)或之前未访问过的页面
            val isFirstTime = (page == 0 && visitedSubPages.isEmpty()) ||
                    !visitedSubPages.contains(page)

            when (page) {
                0 -> RecommendScreen(
                    isFirstTimeVisible = isFirstTime,
                    viewModel = hiltViewModel() // 独立的RecommendViewModel
                )
                1 -> FollowingScreen(
                    isFirstTimeVisible = isFirstTime,
                    viewModel = hiltViewModel() // 独立的FollowingViewModel
                )
            }
        }
    }
}

@Composable
private fun HomeTabRow(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("推荐", "关注")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        tabs.forEachIndexed { index, title ->
            HomeTab(
                title = title,
                isSelected = selectedTabIndex == index,
                onClick = { onTabSelected(index) }
            )
            if (index < tabs.size - 1) {
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}

@Composable
private fun HomeTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = title,
            fontSize = if (isSelected) 18.sp else 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF2196F3) else Color(0xFF757575)
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(3.dp)
                    .background(
                        Color(0xFF2196F3),
                        RoundedCornerShape(1.5.dp)
                    )
            )
        }
    }
}