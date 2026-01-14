package com.application.quickkartcustomer.presentation.cart


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.quickkartcustomer.domain.model.Cart
import com.application.quickkartcustomer.domain.model.CartItem
import com.application.quickkartcustomer.ui.navigation.Screen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.application.quickkartcustomer.ui.theme.DarkBlue
import com.application.quickkartcustomer.ui.theme.LightGray
import com.application.quickkartcustomer.ui.theme.TextGray


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val cart = viewModel.cart.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val deliveryFee = 2.00
    Scaffold(
        bottomBar = {
            if (cart.value.items.isNotEmpty()) {
                CartBottomBar(
                    cart = cart.value,
                    deliveryFee = deliveryFee,
                    navController = navController
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading.value -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                cart.value.items.isEmpty() -> {
                    EmptyCartView()
                }
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        CartHeader(
                            itemCount = cart.value.totalItems,
                            onBackClick = { navController.navigateUp() }
                        )
                        CartContent(
                            cart = cart.value,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartHeader(
    itemCount: Int,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(LightGray)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = TextGray,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Shopping Cart ($itemCount)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
    }
}

@Composable
fun EmptyCartView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Empty Cart",
            modifier = Modifier.size(100.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your cart is empty",
            fontSize = 18.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add some products to get started",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun CartContent(cart: Cart, viewModel: CartViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(cart.items) { index, item ->
            CartItemView(item = item, viewModel = viewModel)
            if (index < cart.items.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )
            }
        }
    }
}

@Composable
fun CartItemView(item: CartItem, viewModel: CartViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (item.productImage != null && item.productImage.isNotEmpty()) {
                AsyncImage(
                    model = item.productImage,
                    contentDescription = item.productName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = item.productName.first().toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.productName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$${String.format("%.2f", item.price)}",
                fontSize = 14.sp,
                color = TextGray
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(LightGray)
                    .clickable(onClick = {
                        val newQuantity = item.quantity - 1
                        if (newQuantity <= 0) {
                            viewModel.removeFromCart(item.productId)
                        } else {
                            viewModel.updateQuantity(item.productId, newQuantity)
                        }
                    }),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "-",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextGray
                )
            }
            Text(
                text = item.quantity.toString(),
                fontSize = 16.sp,
                color = Color(0xFF212121),
                modifier = Modifier.width(24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(LightGray)
                    .clickable(onClick = {
                        val newQuantity = item.quantity + 1
                        if (newQuantity <= item.maxQuantity) {
                            viewModel.updateQuantity(item.productId, newQuantity)
                        }
                    }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = TextGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun CartBottomBar(
    cart: Cart,
    deliveryFee: Double,
    navController: NavController
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal",
                    fontSize = 14.sp,
                    color = TextGray
                )
                Text(
                    text = "$${String.format("%.2f", cart.totalPrice)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery",
                    fontSize = 14.sp,
                    color = TextGray
                )
                Text(
                    text = "$${String.format("%.2f", deliveryFee)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = "$${String.format("%.2f", cart.totalPrice + deliveryFee)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate(Screen.Checkout.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkBlue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Proceed To checkout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}