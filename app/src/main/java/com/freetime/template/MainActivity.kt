package com.freetime.template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.freetime.design.AppTheme
import com.freetime.design.ThemeMode
import com.freetime.template.ui.GameScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme(
                themeMode = ThemeMode.AUTO_TIME,
                lightHour = 7,
                darkHour = 19,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.background,
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.30f),
                                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.18f),
                                ),
                            ),
                        ),
                ) {
                    GameScreen()
                }
            }
        }
    }
}
