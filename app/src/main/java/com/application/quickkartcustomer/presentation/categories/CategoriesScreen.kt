package com.application.quickkartcustomer.presentation.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.application.quickkartcustomer.domain.model.Category
import com.application.quickkartcustomer.domain.model.Product
import com.application.quickkartcustomer.ui.components.QuickKartBottomNavigation
import com.application.quickkartcustomer.ui.components.CartIconWithBadge
import com.application.quickkartcustomer.ui.navigation.NavigationStateManager
import com.application.quickkartcustomer.ui.navigation.Screen
import com.application.quickkartcustomer.ui.navigation.getBottomNavRoute
import com.application.quickkartcustomer.ui.navigation.navigateWithAnimation
import com.application.quickkartcustomer.ui.theme.DarkBlue
import com.application.quickkartcustomer.ui.theme.LightGray
import com.application.quickkartcustomer.ui.theme.Primary
import com.application.quickkartcustomer.ui.theme.TextGray
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.application.quickkartcustomer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    navController: NavController,
    viewModel: CategoriesViewModel = hiltViewModel(),
    navigationStateManager: NavigationStateManager,
    cartViewModel: com.application.quickkartcustomer.presentation.cart.CartViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val categoryProducts by viewModel.categoryProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val safeCategories = categories ?: emptyList()

    val cart by cartViewModel.cart.collectAsState()
    val cartItemCount = cart.totalItems

    // Search state
    var searchQuery by remember { mutableStateOf("") }

    // Filter categories based on search query
    val filteredCategories = remember(safeCategories, searchQuery) {
        if (searchQuery.isBlank()) {
            safeCategories
        } else {
            safeCategories.filter { category ->
                category.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        bottomBar = {
            val navHostController = navController as androidx.navigation.NavHostController
            val navBackStackEntry by navHostController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val bottomNavRoute = getBottomNavRoute(currentRoute)

            QuickKartBottomNavigation(
                currentRoute = bottomNavRoute,
                onItemClick = { route: String ->
                    when (route) {
                        Screen.Home.route -> {
                            navController.navigateWithAnimation(Screen.Home.route, navigationStateManager) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                        Screen.Categories.route -> {
                            // Already on categories, do nothing
                        }
                        Screen.Profile.route -> {
                            navController.navigateWithAnimation(Screen.Profile.route, navigationStateManager) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                        Screen.OrderTracking.route -> {
                            navController.navigateWithAnimation(Screen.OrderTracking.route, navigationStateManager) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            LightGray,
                            Color.White
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            when {
                isLoading && safeCategories.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Primary
                    )
                }
                error != null && safeCategories.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Oops!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color(0xFF212121),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = error ?: "Something went wrong",
                                    color = Color.Red,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.loadCategories() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                                ) {
                                    Text("Try Again", color = Color.White)
                                }
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        // Header section
                        item {
                            CategoryHeaderCard(
                                title = "Shop by Category",
                                subtitle = "Find your favorite products",
                                cartItemCount = cartItemCount,
                                onCartClick = { navController.navigateWithAnimation(Screen.Cart.route, navigationStateManager) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Search bar
                        item {
                            CategorySearchBar(
                                query = searchQuery,
                                onQueryChange = { searchQuery = it },
                                placeholder = "Search categories..."
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Categories grid
                        if (filteredCategories.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Browse Categories",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color(0xFF212121),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Grid layout for categories
                            item {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.height(400.dp)
                                ) {
                                    items(filteredCategories) { category ->
                                        CategoryGridCard(
                                            category = category,
                                            products = categoryProducts[category.id] ?: emptyList(),
                                            navigationStateManager = navigationStateManager,
                                            navController = navController,
                                            onCategoryClick = { categoryId ->
                                                navController.navigateWithAnimation(
                                                    Screen.ProductListByCategory.createRoute(categoryId),
                                                    navigationStateManager
                                                )
                                            },
                                            onProductClick = { product ->
                                                // TODO: Navigate to product detail
                                            },
                                            onAddToCart = { product ->
                                                // TODO: Add to cart
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.List,
                                            contentDescription = "No categories",
                                            tint = TextGray,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "No categories found",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = TextGray,
                                            textAlign = TextAlign.Center
                                        )
                                        if (searchQuery.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Try adjusting your search",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextGray,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Function to get Lottie animation resource based on category name
@Composable
fun getCategoryLottieResource(categoryName: String): Int {
    return when {
        categoryName.contains("Fruits", ignoreCase = true) && 
        categoryName.contains("Vegetables", ignoreCase = true) -> R.raw.thanks_giving_basket
        categoryName.contains("Fruits", ignoreCase = true) && 
        !categoryName.contains("Vegetables", ignoreCase = true) -> R.raw.thanks_giving_basket
        categoryName.contains("Dairy", ignoreCase = true) && 
        categoryName.contains("Eggs", ignoreCase = true) -> R.raw.cow_drink_milk
        categoryName.contains("Snacks", ignoreCase = true) -> R.raw.chips_salsa
        categoryName.contains("Beverages", ignoreCase = true) -> R.raw.food_toss
        else -> R.raw.thanks_giving_basket // Default fallback
    }
}

@Composable
fun CategoryGridCard(
    category: Category,
    products: List<Product>,
    navigationStateManager: NavigationStateManager,
    navController: NavController,
    onCategoryClick: (Int) -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                onCategoryClick(category.id)
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Category Lottie Animation
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.1f),
                                Primary.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                val lottieResource = getCategoryLottieResource(category.name)
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieResource))
                
                LottieAnimation(
                    composition = composition,
                    iterations = Int.MAX_VALUE,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Name
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF212121),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Product Count
            Text(
                text = "${products.size} products",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                textAlign = TextAlign.Center
            )

            // Sample products preview (show up to 3 products)
            if (products.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    products.take(3).forEach { product ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF5F5F5))
                                .clickable { onProductClick(product) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!product.image.isNullOrEmpty()) {
                                AsyncImage(
                                    model = product.image,
                                    contentDescription = product.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = product.name.firstOrNull()?.toString() ?: "?",
                                    fontSize = 12.sp,
                                    color = TextGray
                                )
                            }
                        }
                    }

                    // Add more indicator if there are more products
                    if (products.size > 3) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${products.size - 3}",
                                fontSize = 12.sp,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}


// Modern category header card
@Composable
fun CategoryHeaderCard(
    title: String,
    subtitle: String,
    cartItemCount: Int,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkBlue
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            CartIconWithBadge(
                itemCount = cartItemCount,
                onClick = onCartClick
            )
        }
    }
}

// Modern search bar for categories
@Composable
fun CategorySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextGray
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextGray
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = TextGray
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
    }
}