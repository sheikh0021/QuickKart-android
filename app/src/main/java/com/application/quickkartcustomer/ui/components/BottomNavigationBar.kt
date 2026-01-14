package com.application.quickkartcustomer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.quickkartcustomer.R

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector? = null,
    val drawableResId: Int? = null
){
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Categories : BottomNavItem("categories", "Categories", drawableResId = R.drawable.category)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
    object More : BottomNavItem("more", "More", Icons.Default.MoreVert)
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
        BottomNavItem.Profile,
        BottomNavItem.More
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                BottomNavItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = {onItemClick(item.route)}
                )
            }
        }
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