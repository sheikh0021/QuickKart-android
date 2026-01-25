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
import com.application.quickkartcustomer.domain.model.Product
import com.application.quickkartcustomer.ui.theme.OrangeAccent
import com.application.quickkartcustomer.ui.theme.PremiumOrange
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.delay

@Composable
fun AnimatedProductCarousel(
    products: List<Product>,
    title: String = "Trending Now",
    onProductClick: (Product) -> Unit
) {
    val listState = rememberLazyListState()

    // Auto-scroll functionality - scrolls every 4 seconds
    LaunchedEffect(products.size) {
        if (products.size > 3) {
            while (true) {
                delay(4000) // 4 second intervals
                val currentIndex = listState.firstVisibleItemIndex
                val nextIndex = (currentIndex + 1) % products.size
                listState.animateScrollToItem(nextIndex)
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        // Section header with icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            // Optional: Add trending icon
            Text(
                text = "🔥",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Product carousel with improved spacing
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            items(products.size) { index ->
                val product = products[index]
                AnimatedProductCard(
                    product = product,
                    index = index,
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}

@Composable
fun AnimatedProductCard(
    product: Product,
    index: Int,
    onClick: () -> Unit
) {
    // Staggered floating animation for each card
    val transition = rememberInfiniteTransition()

    // Different animation delays for each card
    val floatOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500 + (index * 200), easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Subtle scale animation
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000 + (index * 150), easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .graphicsLayer {
                translationY = floatOffset
                scaleX = scale
                scaleY = scale
            }
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Product image section with price badge
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Product image
                AsyncImage(
                    model = product.image ?: "",
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Price badge overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            color = PremiumOrange,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "₹${product.price}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Product details section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Product name
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Product description
                Text(
                    text = product.description ?: "",
                    fontSize = 12.sp,
                    color = Color(0xFF6C757D),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Store indicator (optional)
                Text(
                    text = "From ${product.storeName ?: "Store"}",
                    fontSize = 10.sp,
                    color = Color(0xFF9E9E9E),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}