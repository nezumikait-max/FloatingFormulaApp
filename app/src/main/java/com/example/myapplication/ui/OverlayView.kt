package com.example.myapplication.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.Formula
import com.example.myapplication.data.SettingsManager
import kotlin.math.roundToInt

@Composable
fun OverlayView(
    onClose: () -> Unit,
    onSpawnBubble: (Formula) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val yOffset by settingsManager.yOffsetFlow.collectAsState(initial = 0.5f)
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Box(
        modifier = modifier
            .fillMaxHeight()
            .wrapContentWidth()
    ) {
        // Transparent touch area at the right edge
        if (!isExpanded) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = screenHeight * yOffset - 32.dp)
                    .height(64.dp)
                    .width(24.dp)
                    .background(Color.Transparent)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            if (dragAmount.x < -10f) {
                                isExpanded = true
                            }
                        }
                    }
            ) {
                // A small handle indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(4.dp)
                        .height(64.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Gray.copy(alpha = 0.5f))
                )
            }
        }

        // Sliding tray
        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            ),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Favorite Formulas", style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = { isExpanded = false }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close Tray")
                        }
                    }

                    // For now using dummy data if we don't have access to ViewModel yet.
                    // Oh, we can try to use ViewModel but it requires application.
                    // Instead, let's inject a Flow or read from repository directly inside this composable.
                    // Or let's create an instance of MainViewModel using a factory.
                    val context = LocalContext.current
                    val viewModel: MainViewModel = viewModel(
                        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
                            context.applicationContext as android.app.Application
                        )
                    )
                    
                    val allFormulas by viewModel.allFormulas.collectAsState()
                    val favoriteFormulas = allFormulas.filter { it.isFavorite }

                    if (favoriteFormulas.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No favorite formulas yet.", style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            items(favoriteFormulas) { formula ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { 
                                            onSpawnBubble(formula)
                                            isExpanded = false
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(text = formula.topic, style = MaterialTheme.typography.labelSmall)
                                        Text(text = formula.title, style = MaterialTheme.typography.titleMedium)
                                        Text(text = formula.equation, style = MaterialTheme.typography.bodyMedium)
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
