package com.example.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app.ui.screen.detail.*
import com.example.app.ui.screen.main.MainScreen

// 定义路由
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object MerchantDetail : Screen("merchant/{merchantId}") {
        fun createRoute(merchantId: String) = "merchant/$merchantId"
    }
    object RecommendDetail : Screen("recommend/{itemId}") {
        fun createRoute(itemId: String) = "recommend/$itemId"
    }
    object FollowingUserProfile : Screen("user/{userId}") {
        fun createRoute(userId: String) = "user/$userId"
    }
    object MessageChat : Screen("chat/{messageId}") {
        fun createRoute(messageId: String) = "chat/$messageId"
    }
    object AlleyItemDetail : Screen("alley/{itemId}") {
        fun createRoute(itemId: String) = "alley/$itemId"
    }
}

// 全局导航控制器
val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No NavController provided")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = Screen.Main.route
        ) {
            // 主页面（带底部导航）
            composable(Screen.Main.route) {
                MainScreen()
            }
            
            // 详情页面们
            composable(Screen.MerchantDetail.route) { backStackEntry ->
                val merchantId = backStackEntry.arguments?.getString("merchantId") ?: ""
                MerchantDetailScreen(
                    merchantId = merchantId,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.RecommendDetail.route) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                RecommendDetailScreen(
                    itemId = itemId,
                    onBack = { navController.popBackStack() }
                )
            }
            
            /*composable(Screen.FollowingUserProfile.route) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                UserProfileScreen(
                    userId = userId,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.MessageChat.route) { backStackEntry ->
                val messageId = backStackEntry.arguments?.getString("messageId") ?: ""
                ChatScreen(
                    messageId = messageId,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.AlleyItemDetail.route) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                AlleyItemDetailScreen(
                    itemId = itemId,
                    onBack = { navController.popBackStack() }
                )
            }*/
        }
    }
}