package com.freetime.template.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.freetime.template.game.CatActivity
import com.freetime.template.game.OwnedCat

@Composable
fun CatActivityScene(
    cat: OwnedCat,
    typeEmoji: String,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "catActivity")
    val motion by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(850),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "catMotion",
    )

    val scene = when (cat.activity) {
        CatActivity.SLEEPING -> "💤  " + typeEmoji + "  🛏️"
        CatActivity.EATING -> typeEmoji + "  🍗"
        CatActivity.PLAYING -> typeEmoji + "  🧶"
        CatActivity.WATCHING_BIRDS -> typeEmoji + "  🪟  🐦"
        CatActivity.SITTING_IN_BOX -> "📦 " + typeEmoji
        CatActivity.EXPLORING -> "🐾  " + typeEmoji + "  🌿"
        CatActivity.ASKING_FOR_PETS -> typeEmoji + "  ❤️  🖐️"
        CatActivity.ZOOMIES -> typeEmoji + "  💨💨"
        CatActivity.CHAOS -> typeEmoji + "  🧻  💥"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.graphicsLayer {
                    translationX = when (cat.activity) {
                        CatActivity.ZOOMIES, CatActivity.EXPLORING -> motion * 6f
                        else -> motion
                    }
                    rotationZ = if (cat.activity == CatActivity.CHAOS) motion else 0f
                    scaleX = if (cat.activity == CatActivity.SLEEPING) 1f + motion / 100f else 1f
                    scaleY = if (cat.activity == CatActivity.SLEEPING) 1f + motion / 100f else 1f
                },
            ) {
                Text(scene, style = MaterialTheme.typography.displaySmall)
            }
            Text(cat.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                cat.activity.emoji + " " + cat.activity.label,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
    }
}
