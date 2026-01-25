package com.application.quickkartcustomer.presentation.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.application.quickkartcustomer.domain.model.Store
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.application.quickkartcustomer.core.util.PreferencesManager
import com.application.quickkartcustomer.domain.model.Banner
import com.application.quickkartcustomer.domain.model.Category
import com.application.quickkartcustomer.domain.model.HomeData
import com.application.quickkartcustomer.domain.model.Product
import com.application.quickkartcustomer.presentation.cart.CartViewModel
import com.application.quickkartcustomer.ui.components.EnhancedHomeHeader
import com.application.quickkartcustomer.ui.components.EnhancedSearchBar
import com.application.quickkartcustomer.ui.components.QuickKartBottomNavigation
import com.application.quickkartcustomer.ui.components.ShimmerStoreCard
import com.application.quickkartcustomer.ui.theme.BackgroundGradient
import com.application.quickkartcustomer.ui.theme.DarkBlue
import com.application.quickkartcustomer.ui.theme.OrangeAccent
import com.application.quickkartcustomer.ui.theme.Beige
import com.application.quickkartcustomer.ui.theme.PremiumOrange
import com.application.quickkartcustomer.presentation.home.AnimatedProductCarousel
import com.application.quickkartcustomer.presentation.home.SlidingBannerCards
import com.application.quickkartcustomer.presentation.home.PromoCard
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import com.application.quickkartcustomer.domain.model.Cart
import com.application.quickkartcustomer.ui.navigation.NavigationStateManager
import com.application.quickkartcustomer.ui.navigation.navigateWithAnimation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    navigationStateManager: NavigationStateManager,
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    var showSearchResults by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context)}
    val user = preferencesManager.getUser()
    val homeData by viewModel.homeData.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val cart by cartViewModel.cart.collectAsState()
    val cartItemCount = cart?.totalItems ?: 0

    LaunchedEffect(Unit) {
        if (!preferencesManager.isLoggedIn()){
            navController.navigateWithAnimation(Screen.Login.route, navigationStateManager){
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
            navController.navigateWithAnimation(Screen.Login.route, navigationStateManager) {
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
        modifier = Modifier.background(
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(BackgroundGradient)
        ),
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
                                navController.navigateWithAnimation(Screen.Home.route, navigationStateManager) {
                                    popUpTo(Screen.Home.route) { inclusive = false }
                                }
                            }
                        }
                        Screen.Categories.route -> {
                            navController.navigateWithAnimation(Screen.Categories.route, navigationStateManager) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
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
        },
    ) {paddingValues ->
        Box(modifier = Modifier.fillMaxSize()){
            when {
                isLoading -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item { ShimmerStoreCard() }
                        item { ShimmerStoreCard() }
                        item { ShimmerStoreCard() }
                        item { ShimmerStoreCard() }
                        item { ShimmerStoreCard() }
                    }
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
                        //header with gradient background
                        item {
                            Column(
                                modifier = Modifier.background(Brush.verticalGradient(
                                    colors = listOf(
                                        DarkBlue.copy(alpha = 0.75f),  // Lighter: was 0.95f
                                        DarkBlue.copy(alpha = 0.65f),  // Lighter: was 0.88f
                                        DarkBlue.copy(alpha = 0.55f)   // Lighter: was 0.82f
                                    )
                                ))
                            ) {
                                EnhancedHomeHeader(
                                    userName = userName,
                                    deliveryAddress = deliveryAddress,
                                    deliveryItem = "Within 1 hour",
                                    cartItemCount = cartItemCount,
                                    onCartClick = {navController.navigateWithAnimation(Screen.Cart.route, navigationStateManager)},
                                    onAddressClick = {},
                                    onTimeClick = {}
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                EnhancedSearchBar(
                                    placeholder = "Search products or stores...",
                                    onClick = {
                                        showSearchResults = true
                                    },
                                    searchQuery = searchQuery,
                                    onSearchQueryChange = {query ->
                                        if (query.isNotEmpty()){
                                            viewModel.searchProducts(query)
                                            showSearchResults = true
                                        } else {
                                            viewModel.clearSearch()
                                        }
                                    },
                                    onSearch = {query ->
                                        viewModel.searchProducts(query)
                                        showSearchResults= true
                                    },
                                    isSearching = isSearching
                                )
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                        homeData?.let { data ->
                            val safeBanners = data.banners ?: emptyList()
                            val safeProducts = data.products ?: emptyList()
                            val safeCategories = data.categories ?: emptyList()
                            val safeStores = data.stores ?: emptyList()
                            
                            if (safeBanners.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HeroBannerSection(banners = safeBanners)
                                }
                            }
                            // Add animated product carousel after categories
                            if (safeProducts.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(32.dp))
                                    AnimatedProductCarousel(
                                        products = safeProducts.take(8), // Show first 8 products
                                        title = "Trending Now",
                                        onProductClick = { product ->
                                            navController.navigateWithAnimation(Screen.ProductList.createRoute(product.store), navigationStateManager)
                                        }
                                    )
                                }
                            }

                        // Add sliding promotional banner cards
                        item {
                            Spacer(modifier = Modifier.height(32.dp))

                            // Sample promotional cards - REPLACE with API data later
                            val promoCards = listOf(
                                PromoCard(
                                    id = 1,
                                    title = "Fresh & Fast",
                                    subtitle = "Get groceries delivered in 30 mins",
                                    imageUrl = "https://via.placeholder.com/100x100/4CAF50/FFFFFF?text=Fresh",
                                    backgroundColor = Color(0xFF4CAF50),
                                    actionText = "Order Now",
                                    icon = "🚚"
                                ),
                                PromoCard(
                                    id = 2,
                                    title = "Special Deals",
                                    subtitle = "Up to 50% off on daily essentials",
                                    imageUrl = "https://via.placeholder.com/100x100/FF9800/FFFFFF?text=Deals",
                                    backgroundColor = PremiumOrange,
                                    actionText = "Shop Deals",
                                    icon = "💰"
                                ),
                                PromoCard(
                                    id = 3,
                                    title = "Premium Stores",
                                    subtitle = "Shop from verified premium partners",
                                    imageUrl = "https://via.placeholder.com/100x100/2196F3/FFFFFF?text=Premium",
                                    backgroundColor = Color(0xFF2196F3),
                                    actionText = "Explore",
                                    icon = "⭐"
                                ),
                                PromoCard(
                                    id = 4,
                                    title = "Express Delivery",
                                    subtitle = "Same-day delivery available now",
                                    imageUrl = "https://via.placeholder.com/100x100/FF5722/FFFFFF?text=Express",
                                    backgroundColor = Color(0xFFFF5722),
                                    actionText = "Get Fast",
                                    icon = "⚡"
                                )
                            )

                            SlidingBannerCards(
                                promoCards = promoCards,
                                onCardClick = { card ->
                                    // Handle promo card navigation
                                    when (card.id) {
                                        1 -> navController.navigateWithAnimation(Screen.Categories.route, navigationStateManager)
                                        2 -> navController.navigateWithAnimation(Screen.Categories.route, navigationStateManager)
                                        3 -> navController.navigateWithAnimation(Screen.Home.route, navigationStateManager)
                                        4 -> navController.navigateWithAnimation(Screen.Categories.route, navigationStateManager)
                                    }
                                }
                            )
                            }
                            //categories
                            if (safeCategories.isNotEmpty()){
                                item {
                                    Spacer(modifier = Modifier.height(32.dp))
                                    InteractiveCategorySection(
                                        categories = safeCategories,
                                        navController = navController
                                    )
                                }
                            }
                            //store section
                            if (safeStores.isNotEmpty()){
                                item {
                                    Spacer(modifier = Modifier.height(32.dp))
                                    PremiumStoreSection(
                                        stores = safeStores,
                                        navController = navController
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {
                    // fall back to current basic store list implementations,
                    val stores by viewModel.stores.collectAsState()
                    val safeStores = stores ?: emptyList()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier.background(Brush.verticalGradient(
                                    colors = listOf(
                                        DarkBlue.copy(alpha = 0.75f),  // Lighter gradient
                                        DarkBlue.copy(alpha = 0.65f),
                                        DarkBlue.copy(alpha = 0.55f)
                                    )
                                )).fillMaxWidth()
                            ) {
                                EnhancedHomeHeader(
                                    userName = userName,
                                    deliveryAddress = deliveryAddress,
                                    deliveryItem = "Within 1 hour",
                                    cartItemCount = cartItemCount,
                                    onCartClick = { navController.navigateWithAnimation(Screen.Cart.route, navigationStateManager) },
                                    onAddressClick = {},
                                    onTimeClick = {}
                                )
                                EnhancedSearchBar(
                                    placeholder = "Search products or stores...",
                                    onClick = {
                                        stores.firstOrNull()?.let { store ->
                                            navController.navigateWithAnimation(
                                                Screen.ProductList.createRoute(
                                                    store.id
                                                ), navigationStateManager
                                            )
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                        item { Text(
                            text = "Nearby Stores",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        }

                        items(safeStores) { store ->
                            StoreCard(
                                store = store,
                                onClick = {navController.navigateWithAnimation(Screen.ProductList.createRoute(store.id), navigationStateManager)
                                }
                            )

                        }
                    }
                }
            }
        }
    }
    if (showSearchResults){
        SearchResultsSheet(
            searchQuery = searchQuery,
            onSearchQueryChange = {query ->
                if (query.isNotEmpty()) {
                    viewModel.searchProducts(query)
                } else {
                    viewModel.clearSearch()
                }
            },
            searchResults = searchResults ?: emptyList(),
            isSearching = isSearching,
            homeData = homeData,
            stores = viewModel.stores.collectAsState().value ?: emptyList(),
            onDismiss = {
                showSearchResults = false
                viewModel.clearSearch()
            },
            onStoreClick = { storeId ->
                navController.navigateWithAnimation(Screen.ProductList.createRoute(storeId), navigationStateManager)
                showSearchResults = false
                viewModel.clearSearch()
            },
            onCategoryClick = {categoryId ->
                navController.navigateWithAnimation(Screen.ProductList.createRoute(categoryId), navigationStateManager)
                showSearchResults = false
                viewModel.clearSearch()
            },
            onProductClick = { product ->
                navController.navigateWithAnimation(Screen.ProductList.createRoute(product.store), navigationStateManager)
                showSearchResults = false
                viewModel.clearSearch()
            },
            onAddToCart = {product ->
                cartViewModel.addToCart(product, 1)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsSheet(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchResults: List<Product>,
    isSearching: Boolean,
    homeData: HomeData?,
    stores: List<Store>,
    onDismiss: () -> Unit,
    onStoreClick: (Int) -> Unit,
    onCategoryClick: (Int) -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    modifier : Modifier = Modifier
){
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val queryLower = searchQuery.lowercase().trim()
    val safeViewModelStores = stores ?: emptyList()
    val safeStoresList = (homeData?.stores ?: safeViewModelStores) ?: emptyList()
    val matchingStores = safeStoresList.filter { store ->
        store.name.lowercase().contains(queryLower) ||
                store.description.lowercase().contains(queryLower)
    }
    val safeCategoriesList = homeData?.categories ?: emptyList()
    val matchingCategories = safeCategoriesList.filter { category ->
        category.name.lowercase().contains(queryLower)
    }

    val totalResults = searchResults.size + matchingStores.size + matchingCategories.size

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {Text("Search products, stores, categories ....")},
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()){
                            IconButton(onClick = {onSearchQueryChange("")}) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        } else if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (searchQuery.isNotEmpty()) {
                                // Search is already triggered by onValueChange
                            }
                        }
                    )

                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (searchQuery.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Found $totalResults results",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Text(
                    text = "Start typing to search...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (searchQuery.isNotEmpty()) {
                when {
                    isSearching -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    totalResults == 0 -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No results found",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.height(400.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Products Section
                            if (searchResults.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Products",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                items(searchResults) { product ->
                                    SearchResultItem(
                                        product = product,
                                        onClick = { onProductClick(product) },
                                        onAddToCart = { onAddToCart(product) }
                                    )
                                }
                            }

                            // Stores Section
                            if (matchingStores.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Stores",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                items(matchingStores) { store ->
                                    SearchStoreItem(
                                        store = store,
                                        onClick = { onStoreClick(store.id) }
                                    )
                                }
                            }

                            // Categories Section
                            if (matchingCategories.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Categories",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                items(matchingCategories) { category ->
                                    SearchCategoryItem(
                                        category = category,
                                        onClick = { onCategoryClick(category.id) }
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

@Composable
fun SearchStoreItem(
    store: Store,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = store.image ?: "",
                contentDescription = store.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = store.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun SearchCategoryItem(
    category: Category,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.name.first().toString().uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun SearchResultItem(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product Image
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Product Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = product.description ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${product.price}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }

            // Add to Cart Button
            Button(
                onClick = onAddToCart,
                modifier = Modifier.height(36.dp)
            ) {
                Text("Add", fontSize = 12.sp)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("DEPRECATION")
fun StoreCard(
store : Store,
onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 8f,
        animationSpec = tween(200),
        label = "card_elevation"
    )
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )
    Card(
        modifier = Modifier.fillMaxWidth().graphicsLayer{
            scaleX = scale
            scaleY = scale
        }
            .clickable(
                interactionSource = interactionSource,
                onClick = onClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation =  elevation.dp)
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