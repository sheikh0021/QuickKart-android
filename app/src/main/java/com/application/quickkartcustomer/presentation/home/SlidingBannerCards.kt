package com.application.quickkartcustomer.presentation.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import coil.compose.AsyncImage
import com.application.quickkartcustomer.ui.theme.PremiumOrange
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.delay

// Data class for promo cards
data class PromoCard(
    val id: Int,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val backgroundColor: Color,
    val actionText: String,
    val icon: String = "🚚" // Default delivery icon
)

@Composable
fun SlidingBannerCards(
    promoCards: List<PromoCard>,
    onCardClick: (PromoCard) -> Unit
) {
    val listState = rememberLazyListState()

    // Auto-scroll functionality - scrolls every 3.5 seconds
    LaunchedEffect(promoCards.size) {
        if (promoCards.size > 2) {
            while (true) {
                delay(3500) // 3.5 second intervals
                val currentIndex = listState.firstVisibleItemIndex
                val nextIndex = (currentIndex + 1) % promoCards.size
                listState.animateScrollToItem(nextIndex)
            }
        }
    }

    // Section without header (more compact)
    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 20.dp) // Include padding here
    ) {
        items(promoCards.size) { index ->
            val card = promoCards[index]
            SlidingPromoCard(
                card = card,
                index = index,
                onClick = { onCardClick(card) }
            )
        }
    }
}

@Composable
fun SlidingPromoCard(
    card: PromoCard,
    index: Int,
    onClick: () -> Unit
) {
    // Enhanced floating and scaling animations
    val transition = rememberInfiniteTransition()

    // Floating animation with different timing per card
    val floatOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800 + (index * 150), easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Scale animation for subtle breathing effect
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200 + (index * 200), easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .width(280.dp)
            .height(140.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = card.backgroundColor.copy(alpha = 0.3f)
            )
            .graphicsLayer {
                translationY = floatOffset
                scaleX = scale
                scaleY = scale
            }
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 3.dp
        ),
        colors = CardDefaults.cardColors(containerColor = card.backgroundColor)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left content section with text and button
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Icon and title row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = card.icon,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = card.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle
                Text(
                    text = card.subtitle,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action button
                Surface(
                    modifier = Modifier
                        .width(100.dp)
                        .height(32.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    shadowElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = card.actionText,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Right image section
            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = card.imageUrl,
                    contentDescription = card.title,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}