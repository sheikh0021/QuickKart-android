package com.application.quickkartcustomer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.quickkartcustomer.ui.theme.OrangeAccent


@Composable
fun CartIconWithBadge(
    itemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
    ){
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Shopping Cart",
                tint = Color.White
            )
        }
        if (itemCount > 0) {
            Box(modifier= Modifier.size(18.dp).offset(x = 12.dp, y = (-8).dp).clip(CircleShape).background(
                OrangeAccent
            ).align(Alignment.TopEnd), contentAlignment = Alignment.Center){
                Text(
                    text = if (itemCount > 99) "99+" else itemCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(lineHeight = 12.sp)

                )
            }
        }
    }
}