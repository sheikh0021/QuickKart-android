package com.application.quickkartcustomer.presentation.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import coil.compose.rememberAsyncImagePainter
import com.application.quickkartcustomer.domain.model.Cart
import com.application.quickkartcustomer.domain.model.CartItem
import com.application.quickkartcustomer.ui.components.QuickKartButton
import com.application.quickkartcustomer.ui.navigation.Screen
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
){
    // Now using collectAsStateWithLifecycle for StateFlow
    val cart = viewModel.cart.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping Cart", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (cart.value.items.isNotEmpty()) {
                CartBottomBar(cart = cart.value, navController = navController)
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
                    CartContent(cart = cart.value, viewModel = viewModel)
                }
            }
        }
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
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom bar
    ) {
        items(cart.items) { item ->
            CartItemView(item = item, viewModel = viewModel)
        }
    }
}

@Composable
fun CartItemView(item: CartItem, viewModel: CartViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Product Image
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (item.productImage != null) {
                    Image(
                        painter = rememberAsyncImagePainter(item.productImage),
                        contentDescription = item.productName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = item.productName.first().toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )

                Text(
                    text = "₹${item.price}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    IconButton(
                        onClick = {
                            val newQuantity = item.quantity - 1
                            if (newQuantity <= 0) {
                                viewModel.removeFromCart(item.productId)
                            } else {
                                viewModel.updateQuantity(item.productId, newQuantity)
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text(
                            text = "-",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = item.quantity.toString(),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    IconButton(
                        onClick = {
                            val newQuantity = item.quantity + 1
                            if (newQuantity <= item.maxQuantity) {
                                viewModel.updateQuantity(item.productId, newQuantity)
                            }
                        },
                        modifier = Modifier.size(32.dp),
                        enabled = item.quantity < item.maxQuantity
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase quantity"
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(
                    onClick = { viewModel.removeFromCart(item.productId) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove item",
                        tint = Color.Red
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "₹${item.totalPrice}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun CartBottomBar(cart: Cart, navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total (${cart.totalItems} items)",
                    fontSize = 16.sp
                )
                Text(
                    text = "₹${cart.totalPrice}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            QuickKartButton(
                text = "Proceed to Checkout",
                onClick = { navController.navigate(Screen.Checkout.route) }
            )
        }
    }
}
