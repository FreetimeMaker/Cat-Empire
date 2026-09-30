package com.freetime.catempire.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ZoomiesAnimation(
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!active) return

    val transition = rememberInfiniteTransition(label = "zoomies")
    val x by transition.animateFloat(
        initialValue = -120f,
        targetValue = 320f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "zoomiesX",
    )

    Box(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "🐈💨",
            modifier = Modifier.offset(x = x.dp),
            style = MaterialTheme.typography.displaySmall,
        )
    }
}
