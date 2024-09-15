package com.example.pmu1

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable

@Composable
fun NewsScreen(viewModel: NewsViewModel = viewModel()) {
    val newsState by remember { viewModel.currentNews }

    Column(modifier = Modifier.fillMaxSize()) {
        val columns = 1
        val rows = 4
        for (row in 0 until rows) {
            Row(modifier = Modifier.weight(1f)) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < newsState.size) {
                        val newsItem = newsState[index]
                        val likeCount = viewModel.getLikeCount(newsItem)
                        NewsItemView(newsItem, likeCount) { viewModel.likeNews(newsItem) }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsItemView(newsItem: NewsItem, likeCount: Int, onLikeClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(0.9f)
                .background(Color.LightGray)
                .padding(16.dp)
        ) {
            Text(text = newsItem.content)
        }
        Box(
            modifier = Modifier
                .weight(0.1f)
                .background(Color.Gray)
                .padding(8.dp)
                .clickable(onClick = onLikeClick)
        ) {
            Text(text = "Лайки: $likeCount")
        }
    }
}