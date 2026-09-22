package com.johny.mediaverse.core.presentation.components

/**
 * Created by Johny on 7/6/26.
 * Copyright (c) 2026 Pathao Ltd. All rights reserved.
 */


import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedGradientText(
    text: String,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFF9C27B0),
        Color(0xFF00BCD4),
        Color(0xFFE91E63),
        Color(0xFF9C27B0)
    )
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GradientTextTransition")

    // Measured on a mid-range device (RMX3195): reading this at composition scope and
    // rebuilding the brush each frame costs ~17ms/frame. Deferring it into a
    // CompositingStrategy.Offscreen + BlendMode.SrcAtop draw removed the recomposition
    // but doubled frame time to ~34ms, so the recomposing version stays.
    // ponytail: per-frame recomposition of one isolated Text; revisit only if it
    // shows up in a real trace.
    val offsetAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "GradientOffset"
    )

    val gradientBrush = Brush.linearGradient(
        colors = colors,
        start = Offset(offsetAnimation, offsetAnimation),
        end = Offset(offsetAnimation + 500f, offsetAnimation + 500f)
    )

    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge.copy(
            brush = gradientBrush
        ),
        letterSpacing = 2.sp,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    )
}

@Composable
fun AnimatedSolidColorText(text: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "ColorPulse")

    val animatedColor = infiniteTransition.animateColor(
        initialValue = Color(0xFFFF5722),
        targetValue = Color(0xFF3F51B5),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ColorAnimation"
    )

    // ColorProducer overload: the color is read at draw time, not at composition.
    BasicText(
        text = text,
        style = LocalTextStyle.current.copy(
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        ),
        color = { animatedColor.value }
    )
}

@Preview
@Composable
fun PreviewScreen() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        AnimatedGradientText(text = "Discover Trending Movies")
    }
}

@Preview
@Composable
fun PreviewScreenOne() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        AnimatedSolidColorText(text = "Discover Trending Movies")
    }
}
