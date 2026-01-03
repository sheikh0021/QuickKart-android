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
import com.application.quickkartcustomer.ui.navigation.Screen
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context)}
    val homeData by viewModel.homeData.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        if (!preferencesManager.isLoggedIn()){
            navController.navigate(Screen.Login.route){
                popUpTo(Screen.Home.route) {inclusive = true}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QuickKart", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {navController.navigate(Screen.Profile.route)}) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = {navController.navigate(Screen.Cart.route)}) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        }
    ) {paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)){
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
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        //banner
                        if (homeData!!.banners.isNotEmpty()) {
                            item {
                                BannerSection(banners = homeData!!.banners)
                            }
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
                            Text(
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
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(banners){banner ->
            Card(
                modifier = Modifier.width(300.dp).height(150.dp).clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(banner.image),
                    contentDescription = banner.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)).padding(16.dp),
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

@Composable
fun CategorySection(categories: List<Category>, navController: NavController){
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Shop by Category",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(categories){ category ->
                Card(
                    modifier = Modifier.width(100.dp).clickable{
                        navController.navigate(
                            Screen.ProductList.createRoute(category.id)
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                            Box(
                                modifier = Modifier.size(50.dp).background(Color.Gray, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = category.name.first().toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = category.name,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Text(
                            text = "View All",
                            fontSize = 10.sp,
                            color = Color.Gray
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
                            .size(60.dp)
                            .background(Color.LightGray, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = store.name.first().toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
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

                    Column(horizontalAlignment = Alignment.End) {
                        if (store.isActive) {
                            Text(
                                text = "OPEN",
                                color = Color(0xFF4CAF50),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "CLOSED",
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}