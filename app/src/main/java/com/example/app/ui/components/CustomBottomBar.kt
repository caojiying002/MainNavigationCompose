// com/example/app/ui/components/CustomBottomBar.kt
package com.example.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.screen.main.MainTab

// 自定义颜色
private val SelectedColor = Color(0xFF2196F3)
private val UnselectedColor = Color(0xFF757575)
private val BottomBarBackground = Color(0xFFFAFAFA)

@Composable
fun CustomBottomBar(
    tabs: List<MainTab>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            )
            .background(BottomBarBackground)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, tab ->
            CustomTabItem(
                tab = tab,
                isSelected = selectedTabIndex == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}

@Composable
private fun RowScope.CustomTabItem(
    tab: MainTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // 动画效果
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val color by animateColorAsState(
        targetValue = if (isSelected) SelectedColor else UnselectedColor,
        animationSpec = spring(),
        label = "color"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = SelectedColor.copy(alpha = 0.3f)),
                onClick = onClick
            )
            .padding(vertical = 8.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 图标（这里用Box模拟，实际项目中替换为真实图标）
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = color.copy(alpha = if (isSelected) 0.2f else 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // 实际项目中这里应该用真实的图标资源
            // Icon(
            //     painter = painterResource(id = getIconResource(tab.icon)),
            //     contentDescription = tab.title,
            //     tint = color,
            //     modifier = Modifier.size(20.dp)
            // )
            Text(
                text = tab.title.first().toString(),
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 文字
        Text(
            text = tab.title,
            color = color,
            fontSize = if (isSelected) 12.sp else 11.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )

        // 选中指示器
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(3.dp)
                    .background(
                        color = SelectedColor,
                        shape = RoundedCornerShape(1.5.dp)
                    )
            )
        }
    }
}