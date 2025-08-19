// ===== com/example/app/ui/screen/alley/AlleyScreen.kt =====
package com.example.app.ui.screen.alley

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AlleyScreen(
    isFirstTimeVisible: Boolean,
    viewModel: AlleyViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(isFirstTimeVisible) {
        if (isFirstTimeVisible) {
            viewModel.processIntent(AlleyIntent.LoadAlleyData)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A)) // 暗黑主题
    ) {
        // 顶部标题
        AlleyTopBar()

        // 内容区域
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF9C27B0)
                    )
                }
            }
            uiState.items.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暗巷空无一物",
                        color = Color(0xFF757575),
                        fontSize = 16.sp
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.items.size) { index ->
                        AlleyItem(
                            item = uiState.items[index],
                            onClick = {
                                viewModel.processIntent(
                                    AlleyIntent.OpenItem(uiState.items[index].id)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlleyTopBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF2A2A2A)
    ) {
        Text(
            text = "暗巷",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9C27B0),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AlleyItem(
    item: AlleyItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 神秘图标占位
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Color(0xFF9C27B0).copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "?",
                    fontSize = 48.sp,
                    color = Color(0xFF9C27B0),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                maxLines = 1
            )

            Text(
                text = item.description,
                fontSize = 12.sp,
                color = Color(0xFF757575),
                maxLines = 2
            )
        }
    }
}







