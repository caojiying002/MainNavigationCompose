// ===== com/example/app/ui/screen/home/following/FollowingScreen.kt =====
package com.example.app.ui.screen.home.following

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowingScreen(
    isFirstTimeVisible: Boolean,
    viewModel: FollowingViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    // 首次可见时加载数据
    LaunchedEffect(isFirstTimeVisible) {
        if (isFirstTimeVisible) {
            viewModel.processIntent(FollowingIntent.LoadData)
        }
    }

    // 监听滚动到底部，触发加载更多
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex to totalItemsNumber
        }.collect { (lastVisibleItemIndex, totalItemsNumber) ->
            if (lastVisibleItemIndex >= totalItemsNumber - 3 && totalItemsNumber > 0) {
                viewModel.processIntent(FollowingIntent.LoadMore)
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { viewModel.processIntent(FollowingIntent.Refresh) },
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading && uiState.posts.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2196F3))
                }
            }
            uiState.posts.isEmpty() -> {
                EmptyState(onRefresh = {
                    viewModel.processIntent(FollowingIntent.Refresh)
                })
            }
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = uiState.posts,
                        key = { it.id }
                    ) { post ->
                        FollowingPostCard(
                            post = post,
                            onFollowClick = {
                                if (post.isFollowing) {
                                    viewModel.processIntent(FollowingIntent.UnfollowUser(post.userId))
                                } else {
                                    viewModel.processIntent(FollowingIntent.FollowUser(post.userId))
                                }
                            }
                        )
                    }

                    // 加载更多指示器
                    if (uiState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color(0xFF2196F3)
                                )
                            }
                        }
                    }

                    // 没有更多数据提示
                    if (!uiState.hasMoreData && uiState.posts.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "没有更多动态了",
                                    color = Color(0xFF9E9E9E),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FollowingPostCard(
    post: FollowingPost,
    onFollowClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 用户信息行
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 头像
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.userName.first().toString(),
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 用户名和时间
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.userName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF212121)
                    )
                    Text(
                        text = post.publishTime,
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }

                // 关注按钮
                OutlinedButton(
                    onClick = onFollowClick,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    colors = if (post.isFollowing) {
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF757575)
                        )
                    } else {
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF2196F3),
                            contentColor = Color.White
                        )
                    }
                ) {
                    Text(
                        text = if (post.isFollowing) "已关注" else "关注",
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 内容
            Text(
                text = post.content,
                fontSize = 14.sp,
                color = Color(0xFF424242),
                lineHeight = 20.sp,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )

            // 图片区域（如果有）
            if (post.images.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    post.images.take(3).forEach { _ ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF0F0F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "图片",
                                color = Color(0xFF9E9E9E),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 互动数据
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "${post.likeCount} 赞",
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )
                Text(
                    text = "${post.commentCount} 评论",
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(onRefresh: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "还没有关注任何人",
                color = Color(0xFF757575),
                fontSize = 16.sp
            )
            Text(
                text = "关注感兴趣的人，查看他们的最新动态",
                color = Color(0xFF9E9E9E),
                fontSize = 14.sp
            )
            Button(
                onClick = onRefresh,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Text("发现用户")
            }
        }
    }
}