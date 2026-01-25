package com.application.quickkartcustomer.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex


@Composable
fun EnhancedHomeHeader(
    userName: String,
    cartItemCount: Int,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier,
    deliveryAddress: String,
    deliveryItem: String,
    onAddressClick: () -> Unit,
    onTimeClick: () -> Unit
){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),  // Added padding for better alignment
        verticalArrangement = Arrangement.spacedBy(12.dp)  // Reduced spacing slightly
    ) {
        // Top row with greeting and cart
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hey, $userName 👋🏻",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Ready for your quick delivery ?",
                    fontSize = 16.sp,  // Increased from 14.sp
                    color = Color.White.copy(alpha = 0.9f),  // Slightly more visible
                    fontWeight = FontWeight.Medium
                )
            }

            EnhancedCartIconWithBadge(
                itemCount = cartItemCount,
                onClick = onCartClick
            )
        }
    }
}

@Composable
fun LocationTimeChip(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
){
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
    ){
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
            Text(
                text = text,
                fontSize = 13.sp,  // Increased from 12.sp
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EnhancedCartIconWithBadge(
    itemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    val badgeScale by animateFloatAsState(
        targetValue = if (itemCount > 0) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f)
    )
    Box(
        modifier = Modifier.size(44.dp).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ){
        CircleShape
        Box(modifier = Modifier.size(44.dp).background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center){
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = "Cart",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }

        if (itemCount > 0) {
            Badge(modifier = Modifier.align(Alignment.TopEnd).scale(badgeScale).offset(x = 6.dp, y = (-2).dp),
                containerColor = Color(0XFFFF6B35)
            ) {
                Text(
                    text = itemCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}