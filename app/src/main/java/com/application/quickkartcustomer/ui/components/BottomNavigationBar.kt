package com.application.quickkartcustomer.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.application.quickkartcustomer.R
import com.application.quickkartcustomer.ui.theme.Surface

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector? = null,
    val drawableResId: Int? = null
){
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Categories : BottomNavItem("categories", "Categories", drawableResId = R.drawable.category)
    object Orders : BottomNavItem("order_tracking", "Orders", Icons.Default.List)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}

@Composable
fun QuickKartBottomNavigation(
    currentRoute: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
){
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Categories,
        BottomNavItem.Orders,
        BottomNavItem.Profile
    )

    Box(
        modifier = modifier
            .fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(70.dp).shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(35.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(35.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    FloatingNavItem(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = {onItemClick(item.route)}
                    )
                }
            }
        }

    }
}
@Composable
fun FloatingNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
){
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f)
    )
    val bounce by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f)
    )

    Column(
        modifier = Modifier.size(60.dp).scale(scale * bounce).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center){
            if (item.icon != null){
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    modifier = Modifier.size(24.dp),
                    tint = if (isSelected) Color(0xFF1A237E) else Color(0xFF6C757D)
                )
            }else if (item.drawableResId != null) {
                Icon(
                    painter = painterResource(id = item.drawableResId),
                    contentDescription = item.label,
                    tint = if (isSelected) Color(0xFF1A237E) else Color(0xFF6C757D),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = item.label,
            fontSize = 11.sp,
            fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF1A237E) else Color(0xFF6C757D),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun BottomNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (isSelected) {
            // Floating selected item with elevation
            Card(
                modifier = Modifier
                    .size(56.dp)
                    .padding(bottom = 4.dp),
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A237E))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.icon != null) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = Color(0xFFF9B023), // Dark yellow for selected
                            modifier = Modifier.size(28.dp)
                        )
                    } else if (item.drawableResId != null) {
                        Icon(
                            painter = painterResource(id = item.drawableResId),
                            contentDescription = item.label,
                            tint = Color(0xFFF9B023), // Dark yellow for selected
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
            Text(
                text = item.label,
                fontSize = 11.sp,
                color = Color(0xFF000000),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
        } else {
            // Normal inactive item
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (item.icon != null) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = Color(0xFFBDBDBD), // Lighter gray for inactive (grayscale effect)
                        modifier = Modifier.size(24.dp)
                    )
                } else if (item.drawableResId != null) {
                    Icon(
                        painter = painterResource(id = item.drawableResId),
                        contentDescription = item.label,
                        tint = Color(0xFFBDBDBD), // Lighter gray for inactive (grayscale effect)
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = item.label,
                fontSize = 10.sp,
                color = Color(0xFFBDBDBD), // Lighter gray for inactive text
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}