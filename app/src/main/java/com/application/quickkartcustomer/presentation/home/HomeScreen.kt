package com.application.quickkartcustomer.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.application.quickkartcustomer.ui.navigation.Screen
import com.application.quickkartcustomer.ui.navigation.getBottomNavRoute
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.application.quickkartcustomer.domain.model.Store
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.application.quickkartcustomer.core.util.PreferencesManager
import com.application.quickkartcustomer.domain.model.Banner
import com.application.quickkartcustomer.domain.model.Category
import com.application.quickkartcustomer.presentation.cart.CartViewModel
import com.application.quickkartcustomer.ui.components.DeliveryInfoSection
import com.application.quickkartcustomer.ui.components.HomeHeader
import com.application.quickkartcustomer.ui.components.QuickKartBottomNavigation
import com.application.quickkartcustomer.ui.theme.DarkBlue
import com.application.quickkartcustomer.ui.theme.OrangeAccent
import com.application.quickkartcustomer.ui.theme.Beige


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context)}
    val user = preferencesManager.getUser()
    val homeData by viewModel.homeData.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val cart by cartViewModel.cart.collectAsState()
    val cartItemCount = cart.totalItems

    LaunchedEffect(Unit) {
        if (!preferencesManager.isLoggedIn()){
            navController.navigate(Screen.Login.route){
                popUpTo(Screen.Home.route) {inclusive = true}
            }
        }
    }
    
    LaunchedEffect(error) {
        val errorMessage = error
        if (errorMessage != null && (errorMessage.contains("401") || errorMessage.contains("Unauthorized") || errorMessage.contains("Authentication failed"))) {
            // Clear stored authentication data
            preferencesManager.clearData()
            // Navigate to login screen and clear back stack
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val userName = user?.firstName?.takeIf { it.isNotEmpty() }
        ?: user?.username?.takeIf { it.isNotEmpty() }
        ?: "User"

    val deliveryAddress = user?.let { "${it.firstName} ${it.lastName}" }
        ?: "Green Way 3000, Sydney"

    Scaffold(
        bottomBar = {
            // Get current route for bottom navigation highlighting
            val navHostController = navController as NavHostController
            val navBackStackEntry by navHostController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val bottomNavRoute = getBottomNavRoute(currentRoute)
            
            QuickKartBottomNavigation(
                currentRoute = bottomNavRoute,
                onItemClick = {route ->
                    when (route) {
                        Screen.Home.route -> {
                            if (currentRoute != Screen.Home.route) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = false }
                                }
                            }
                        }
                        Screen.Categories.route -> {
                            navController.navigate(Screen.Categories.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                        Screen.Profile.route -> {
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                        Screen.OrderTracking.route -> {
                            navController.navigate(Screen.OrderTracking.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                    }
                }
            )
        },
    ) {paddingValues ->
        Box(modifier = Modifier.fillMaxSize()){
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = error ?: "Unknown Error", color = Color.Red)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {viewModel.loadHomeData() }) {
                            Text("Retry")
                        }
                    }
                }
                homeData != null -> {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        //banner
                        item {
                            Column(
                                modifier = Modifier.background(DarkBlue)
                            ) {
                                HomeHeader(
                                    userName = userName,
                                    cartItemCount = cartItemCount,
                                    onCartClick = {navController.navigate(Screen.Cart.route)}
                                )
                                DeliveryInfoSection(
                                    deliveryAddress = deliveryAddress,
                                    deliveryTime = "1 Hour",
                                    onAddressClick = {
                                        //for this implementation pending ....
                                    },
                                    onTimeClick = {
                                        //this too pending ....
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        if (homeData!!.banners.isNotEmpty()) {
                            item {
                                BannerSection(banners = homeData!!.banners)
                            }
                        }
                        item {
                            RecommendedProductsSection(
                                products = emptyList(),
                                onProductClick = {product ->
                                    navController.navigate(Screen.ProductList.createRoute(product.store))
                                },
                                onAddToCart = {product ->
                                    cartViewModel.addToCart(product, 1)
                                }
                            )
                        }
                        //categories
                        if (homeData!!.categories.isNotEmpty()){
                            item {
                                CategorySection(
                                    categories = homeData!!.categories,
                                    navController = navController
                                )
                            }
                        }
                        //store section
                        if (homeData!!.stores.isNotEmpty()){
                            item {
                                StoreSection(
                                    stores = homeData!!.stores,
                                    navController = navController
                                )
                            }
                        }
                    }
                }
                else -> {
                    // fall back to current basic store list implementations,
                    val stores by viewModel.stores.collectAsState()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier.background(DarkBlue).fillMaxWidth()
                            ) {
                                HomeHeader(
                                    userName = userName,
                                    cartItemCount = cartItemCount,
                                    onCartClick = { navController.navigate(Screen.Cart.route) }
                                )
                                DeliveryInfoSection(
                                    deliveryAddress = deliveryAddress,
                                    deliveryTime = "1 Hour"
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        item { Text(
                            text = "Nearby Stores",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        }

                        items(stores) { store ->
                            StoreCard(
                                store = store,
                                onClick = {navController.navigate(Screen.ProductList.createRoute(store.id))
                                }
                            )

                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreCard(
store : Store,
onClick: () -> Unit

) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = store.image ?: "",
                contentDescription = store.name,
                modifier = Modifier.height(120.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = store.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = store.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📍 ${store.address}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "⭐️ 4.5",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun BannerSection(banners: List<Banner>){
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(banners){banner ->
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = rememberAsyncImagePainter(banner.image),
                        contentDescription = banner.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Overlay with orange/beige tint for first banner
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (banners.indexOf(banner) == 0) {
                                    OrangeAccent.copy(alpha = 0.7f)
                                } else {
                                    Beige.copy(alpha = 0.5f)
                                }
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Text(
                                text = banner.title,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = banner.description,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySection(categories: List<Category>, navController: NavController){
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Shop by Category",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories){ category ->
                Card(
                    modifier = Modifier
                        .width(100.dp)
                        .clickable{
                            navController.navigate(
                                Screen.ProductList.createRoute(category.id)
                            )
                        },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category.name.first().toString().uppercase(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF757575)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = category.name,
                            fontSize = 12.sp,
                            color = Color(0xFF212121),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "View All",
                            fontSize = 10.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun StoreSection(stores: List<Store>, navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Nearby Stores",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        stores.forEach { store ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clickable {
                        navController.navigate(Screen.ProductList.createRoute(store.id))
                    },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    // Store Image Placeholder
                    Box(
                        modifier = Modifier
                            .size(60.dp).clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (store.image?.isNotEmpty() == true){
                            AsyncImage(
                                model = store.image,
                                contentDescription = store.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = store.name.first().toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = store.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = store.description,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            maxLines = 2
                        )
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⭐ 4.5",
                                fontSize = 12.sp,
                                color = Color(0xFFFF9800)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "•",
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Free delivery",
                                fontSize = 12.sp,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }
        }
    }
}