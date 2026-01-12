package com.application.quickkartcustomer.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.quickkartcustomer.ui.theme.DarkBlue
import com.application.quickkartcustomer.ui.theme.TextGray


@Composable
fun HomeSearchBar(
    placeholder: String = "Search products or store",
    onClick: ()-> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
){
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clip(
            RoundedCornerShape(12.dp)).background(DarkBlue).clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color.White,
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = placeholder,
            color = TextGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal

        )
    }
}