package com.application.quickkartcustomer.presentation.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.application.quickkartcustomer.domain.model.Category
import com.application.quickkartcustomer.domain.model.Product
import com.application.quickkartcustomer.ui.components.HomeHeader
import com.application.quickkartcustomer.ui.components.QuickKartBottomNavigation
import com.application.quickkartcustomer.ui.components.CartIconWithBadge
import com.application.quickkartcustomer.ui.navigation.Screen
import com.application.quickkartcustomer.ui.navigation.getBottomNavRoute
import com.application.quickkartcustomer.ui.theme.DarkBlue
import com.application.quickkartcustomer.ui.theme.TextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    navController: NavController,
    viewModel: CategoriesViewModel = hiltViewModel(),
    cartViewModel: com.application.quickkartcustomer.presentation.cart.CartViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val categoryProducts by viewModel.categoryProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val cart by cartViewModel.cart.collectAsState()
    val cartItemCount = cart.totalItems

    // Search state
    var searchQuery by remember { mutableStateOf("") }

    // Filter categories based on search query
    val filteredCategories = remember(categories, searchQuery) {
        if (searchQuery.isBlank()) {
            categories
        } else {
            categories.filter { category ->
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
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                        Screen.Categories.route -> {
                            // Already on categories, do nothing
                        }
                        Screen.Profile.route -> {
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                        "more" -> {
                            navController.navigate(Screen.OrderTracking.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading && categories.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null && categories.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = error ?: "Something went wrong",
                            color = Color.Red,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadCategories() }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        // Header section (Categories specific)
                        item {
                            Column(modifier = Modifier.background(DarkBlue)) {
                                CategoryHeader(
                                    title = "Shop by Category",
                                    cartItemCount = cartItemCount,
                                    onCartClick = { navController.navigate(Screen.Cart.route) }
                                )
                                CategorySearchBar(
                                    query = searchQuery,
                                    onQueryChange = { searchQuery = it },
                                    placeholder = "Search categories..."
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        // Categories with products
                        items(filteredCategories) { category ->
                            CategorySection(
                                category = category,
                                products = categoryProducts[category.id] ?: emptyList(),
                                onProductClick = { product ->
                                    // TODO: Navigate to product detail
                                },
                                onCategoryClick = { categoryId ->
                                    // TODO: Navigate to category detail or expand
                                },
                                onAddToCart = { product ->
                                    // TODO: Add to cart
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySection(
    category: Category,
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onCategoryClick: (Int) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Category Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCategoryClick(category.id) }
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Image
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                if (category.image?.isNotEmpty() == true) {
                    AsyncImage(
                        model = category.image,
                        contentDescription = category.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = category.name.firstOrNull()?.toString() ?: "?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = "${products.size} products",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
        }

        // Products in this category
        if (products.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                items(products) { product ->
                    CategoryProductCard(
                        product = product,
                        onClick = { onProductClick(product) },
                        onAddToCart = { onAddToCart(product) }
                    )
                }
            }
        } else {
            // No products message
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No products available",
                    color = TextGray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CategoryProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                if (product.image.isNotEmpty()) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Product Name
            Text(
                text = product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121),
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            // Product Price
            Text(
                text = "₹${String.format("%.2f", product.price)}",
                fontSize = 12.sp,
                color = DarkBlue,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            // Add to Cart Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkBlue)
                    .clickable(onClick = onAddToCart),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Simple category header without "Hey" prefix
@Composable
fun CategoryHeader(
    title: String,
    cartItemCount: Int,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        CartIconWithBadge(
            itemCount = cartItemCount,
            onClick = onCartClick
        )
    }
}

// Functional search bar for categories
@Composable
fun CategorySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
    ) {
        androidx.compose.foundation.text.BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear search",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        )
    }
}