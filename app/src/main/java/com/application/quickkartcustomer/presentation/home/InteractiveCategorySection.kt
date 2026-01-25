package com.application.quickkartcustomer.presentation.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.application.quickkartcustomer.domain.model.Category
import com.application.quickkartcustomer.ui.navigation.Screen
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState

import kotlinx.coroutines.delay

@Composable
fun InteractiveCategorySection(
    categories: List<Category>,
    navController: NavController
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        // Enhanced section header
        Text(
            text = "Shop by Category",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(categories.size) { index ->
                val category = categories[index]

                val delay = index * 100
                val alpha by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 400,
                        delayMillis = delay,
                        easing = FastOutSlowInEasing
                    ),
                    label = "category_alpha"
                )
                val offsetY by animateFloatAsState(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 400,
                        delayMillis = delay,
                        easing = FastOutSlowInEasing
                    ),
                    label = "category_offset"
                )
                AnimatedCategoryCard(
category = category,
                    navController = navController,
                    alpha = alpha,
                    offsetY = offsetY
                )
            }
        }
    }
}
    @Composable
    @Suppress("DEPRECATION")
    fun AnimatedCategoryCard(
        category: Category,
        navController: NavController,
        alpha: Float,
        offsetY: Float
    ){
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.95f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "category_scale"
        )
        Card(
            modifier = Modifier.width(100.dp).graphicsLayer{
                this.alpha = alpha
                translationY = offsetY
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                onClick = {
                    navController.navigate(
                        Screen.ProductList.createRoute(category.id)
                    )
                }
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isPressed) 2.dp else 4.dp,
                pressedElevation = 2.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Enhanced icon container with gradient background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFF8F9FA),
                                    Color(0xFFE9ECEF)
                                )
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.name.first().toString().uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF495057)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category name with better typography
                Text(
                    text = category.name,
                    fontSize = 13.sp,
                    color = Color(0xFF212121),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Subtle "View All" text
                Text(
                    text = "View All",
                    fontSize = 11.sp,
                    color = Color(0xFF6C757D),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }