package com.example.mynotes.data

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage

/**
 * Экран отображения новостей с поддержкой выбора источника (Космос / Россия),
 * надежных заглушек для картинок, PullToRefreshBox, офлайн-бейджа и кэширования (Лабораторная работа №10).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(
    newsSource: String = "Космос",
    onBack: () -> Unit,
    onPostClick: (Int) -> Unit
) {
    val viewModel: PostsViewModel = viewModel(factory = viewModelFactory {
        initializer { PostsViewModel(newsSource) }
    })

    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

    // Перезагружаем новости при изменении источника в настройках
    LaunchedEffect(newsSource) {
        viewModel.load(isRefresh = false, source = newsSource)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(if (newsSource == "Россия") "Новости России" else "Spaceflight News (Космос)")
                        if (isOffline) {
                            Surface(
                                color = MaterialTheme.colorScheme.error,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "ОФЛАЙН",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.load(isRefresh = true, source = newsSource) }, enabled = !isRefreshing) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.load(isRefresh = true, source = newsSource) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is PostsUiState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is PostsUiState.Error -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                            Button(onClick = { viewModel.load(source = newsSource) }) {
                                Text("Повторить")
                            }
                        }
                    }
                    is PostsUiState.Success -> {
                        if (state.posts.isEmpty()) {
                            Text("Нет доступных новостей", style = MaterialTheme.typography.bodyLarge)
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.posts, key = { it.id }) { post ->
                                    val imageUrl = post.imageUrl.ifBlank { "https://picsum.photos/id/${(post.id % 50) + 10}/400/300" }
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onPostClick(post.id) },
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            AsyncImage(
                                                model = imageUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(MaterialTheme.shapes.small),
                                                contentScale = ContentScale.Crop
                                            )
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = post.title,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = post.body,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
