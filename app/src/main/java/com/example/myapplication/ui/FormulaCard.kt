package com.example.myapplication.ui

import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myapplication.data.Formula

@Composable
fun FormulaCard(
    formula: Formula,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var legendExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .width(300.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formula.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // KaTeX WebView
            val htmlData = getKaTeXHtml(formula.equation)
            
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.cacheMode = WebSettings.LOAD_NO_CACHE
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (formula.legend.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { legendExpanded = !legendExpanded }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Variables Legend",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = if (legendExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle Legend",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                AnimatedVisibility(visible = legendExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        val items = formula.legend.split("\n")
                        items.forEach { item ->
                            if (item.isNotBlank()) {
                                Text(
                                    text = "• $item",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getKaTeXHtml(formula: String): String {
    // Escape backslashes for JS string
    val escapedFormula = formula.replace("\\", "\\\\").replace("'", "\\'")
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <link rel="stylesheet" href="katex/katex.min.css">
            <script defer src="katex/katex.min.js"></script>
            <style>
                body {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    margin: 0;
                    padding: 16px;
                    background-color: transparent;
                }
                #math {
                    font-size: 1.5em;
                }
            </style>
        </head>
        <body>
            <div id="math"></div>
            <script>
                document.addEventListener("DOMContentLoaded", function() {
                    katex.render('$escapedFormula', document.getElementById('math'), {
                        throwOnError: false,
                        displayMode: true
                    });
                });
            </script>
        </body>
        </html>
    """.trimIndent()
}
