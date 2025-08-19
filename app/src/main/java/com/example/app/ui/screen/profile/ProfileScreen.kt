package com.example.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileScreen(
    isFirstTimeVisible: Boolean,
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(isFirstTimeVisible) {
        if (isFirstTimeVisible) {
            viewModel.processIntent(ProfileIntent.LoadProfile)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // 用户信息卡片
        ProfileHeader(uiState.userInfo)

        Spacer(modifier = Modifier.height(12.dp))

        // 功能列表
        ProfileMenuSection(
            title = "我的服务",
            items = listOf(
                ProfileMenuItem("我的订单", Icons.Default.ShoppingCart),
                ProfileMenuItem("我的收藏", Icons.Default.Favorite),
                //ProfileMenuItem("浏览历史", Icons.Default.History),
                //ProfileMenuItem("我的钱包", Icons.Default.AccountBalanceWallet)
            ),
            onItemClick = { item ->
                viewModel.processIntent(ProfileIntent.OpenMenuItem(item.title))
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ProfileMenuSection(
            title = "其他",
            items = listOf(
                ProfileMenuItem("设置", Icons.Default.Settings),
                //ProfileMenuItem("帮助与反馈", Icons.Default.Help),
                ProfileMenuItem("关于我们", Icons.Default.Info)
            ),
            onItemClick = { item ->
                viewModel.processIntent(ProfileIntent.OpenMenuItem(item.title))
            }
        )
    }
}

@Composable
private fun ProfileHeader(userInfo: UserInfo?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE1F5FE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInfo?.name?.first()?.toString() ?: "U",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0288D1)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = userInfo?.name ?: "用户未登录",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userInfo?.phone ?: "点击登录账号",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuSection(
    title: String,
    items: List<ProfileMenuItem>,
    onItemClick: (ProfileMenuItem) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Column {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                fontWeight = FontWeight.Medium
            )

            items.forEach { item ->
                ProfileMenuRow(
                    item = item,
                    onClick = { onItemClick(item) }
                )
                if (item != items.last()) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = Color(0xFFE0E0E0)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(
    item: ProfileMenuItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = Color(0xFF757575),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    color = Color(0xFF212121)
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFBDBDBD),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

data class ProfileMenuItem(
    val title: String,
    val icon: ImageVector
)

