package com.application.quickkartcustomer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.quickkartcustomer.ui.theme.TextGray


@Composable
fun DeliveryInfoSection(
    deliveryAddress: String,
    deliveryTime: String = "1 Hour",
    onAddressClick: () -> Unit = {},
    onTimeClick: () -> Unit = {},
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f).clickable(onClick = onAddressClick)
        ) {
            Text(
                text = "DELIVERY TO",
                fontSize = 12.sp,
                color = TextGray,
                fontWeight = FontWeight.Normal
            )
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = deliveryAddress,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Change Address",
                    tint = Color.White,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Column(
            modifier  = Modifier.weight(1f).clickable(onClick = onTimeClick)
        ) {
            Text(
                text = "WITHIN",
                fontSize = 11.sp,
                color = TextGray,
                fontWeight = FontWeight.Normal
            )
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = deliveryTime,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Change Time",
                    tint = Color.White,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}