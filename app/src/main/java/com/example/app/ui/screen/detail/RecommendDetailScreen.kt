package com.example.app.ui.screen.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendDetailScreen(
    itemId: String,
    onBack: () -> Unit,
    viewModel: RecommendDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    
    // 处理系统返回键
    BackHandler {
        onBack()
    }
    
    // 加载详情数据
    LaunchedEffect(itemId) {
        viewModel.loadDetail(itemId)
    }
    
    // 监听事件
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RecommendDetailEffect.ShowToast -> {
                    // 显示Toast
                }
                is RecommendDetailEffect.ShareContent -> {
                    // 触发系统分享
                }
                RecommendDetailEffect.NavigateBack -> {
                    onBack()
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "内容详情",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.processIntent(RecommendDetailIntent.Share) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "分享"
                        )
                    }
                    IconButton(
                        onClick = { viewModel.processIntent(RecommendDetailIntent.ToggleBookmark) }
                    ) {
                        Icon(
                            imageVector = if (uiState.isBookmarked) 
                                Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "收藏"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            // 底部操作栏
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 点赞按钮
                    IconButton(
                        onClick = { viewModel.processIntent(RecommendDetailIntent.ToggleLike) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (uiState.isLiked) 
                                    Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "点赞",
                                tint = if (uiState.isLiked) Color(0xFFE91E63) else Color(0xFF757575)
                            )
                            Text(
                                text = uiState.likeCount.toString(),
                                fontSize = 12.sp,
                                color = if (uiState.isLiked) Color(0xFFE91E63) else Color(0xFF757575)
                            )
                        }
                    }
                    
                    // 评论输入框
                    OutlinedTextField(
                        value = uiState.commentDraft,
                        onValueChange = { 
                            viewModel.processIntent(RecommendDetailIntent.UpdateCommentDraft(it))
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("写评论...", fontSize = 14.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    
                    // 发送按钮
                    IconButton(
                        onClick = { viewModel.processIntent(RecommendDetailIntent.SendComment) },
                        enabled = uiState.commentDraft.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "发送",
                            tint = if (uiState.commentDraft.isNotBlank()) 
                                Color(0xFF2196F3) else Color(0xFFBDBDBD)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2196F3))
                }
            }
            uiState.detail != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(scrollState)
                        .background(Color.White)
                ) {
                    // 文章内容
                    ArticleContent(detail = uiState.detail!!)
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = Color(0xFFE0E0E0)
                    )
                    
                    // 评论列表
                    CommentSection(
                        comments = uiState.comments,
                        onLikeComment = { commentId ->
                            viewModel.processIntent(RecommendDetailIntent.LikeComment(commentId))
                        }
                    )
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "加载失败",
                        color = Color(0xFF757575),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticleContent(detail: RecommendDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 标题
        Text(
            text = detail.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121),
            lineHeight = 28.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 作者信息
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 作者头像
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = detail.authorName.first().toString(),
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = detail.authorName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
                Text(
                    text = "${detail.publishTime} · ${detail.readCount}阅读",
                    fontSize = 13.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 关注按钮
            OutlinedButton(
                onClick = { /* 关注作者 */ },
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = if (detail.isFollowing) "已关注" else "关注",
                    fontSize = 13.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 正文内容
        Text(
            text = detail.content,
            fontSize = 16.sp,
            color = Color(0xFF424242),
            lineHeight = 24.sp,
            textAlign = TextAlign.Justify
        )
        
        // 图片列表
        if (detail.images.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            detail.images.forEach { _ ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF0F0F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "文章配图",
                        color = Color(0xFF9E9E9E),
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 标签
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            detail.tags.forEach { tag ->
                AssistChip(
                    onClick = { /* 点击标签 */ },
                    label = {
                        Text(
                            text = "#$tag",
                            fontSize = 12.sp
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFE3F2FD),
                        labelColor = Color(0xFF2196F3)
                    )
                )
            }
        }
    }
}

@Composable
private fun CommentSection(
    comments: List<Comment>,
    onLikeComment: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "评论 (${comments.size})",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        comments.forEach { comment ->
            CommentItem(
                comment = comment,
                onLike = { onLikeComment(comment.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // 底部留白
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun CommentItem(
    comment: Comment,
    onLike: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // 评论者头像
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFF3E0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.userName.first().toString(),
                color = Color(0xFFFF9800),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = comment.userName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
                Text(
                    text = comment.time,
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = comment.content,
                fontSize = 14.sp,
                color = Color(0xFF424242),
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onLike,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (comment.isLiked) 
                            Icons.Filled.ThumbUp else Icons.Default.ThumbUp,  // else Default.ThumbUpOffAlt
                        contentDescription = "点赞",
                        modifier = Modifier.size(16.dp),
                        tint = if (comment.isLiked) Color(0xFF2196F3) else Color(0xFF9E9E9E)
                    )
                }
                Text(
                    text = comment.likeCount.toString(),
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        }
    }
}