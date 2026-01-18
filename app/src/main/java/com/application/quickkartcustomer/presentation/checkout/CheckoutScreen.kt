
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.AlertDialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.quickkartcustomer.domain.model.Address
import com.application.quickkartcustomer.domain.model.Cart
import com.application.quickkartcustomer.domain.model.Order
import com.application.quickkartcustomer.presentation.checkout.CheckoutViewModel
import com.application.quickkartcustomer.ui.components.QuickKartButton
import com.application.quickkartcustomer.ui.components.QuickKartTextField
import com.application.quickkartcustomer.ui.navigation.Screen
import com.application.quickkartcustomer.ui.theme.DarkBlue
import com.application.quickkartcustomer.ui.theme.LightGray
import com.application.quickkartcustomer.ui.theme.TextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val cart by viewModel.cart.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val orderPlaced by viewModel.orderPlaced.collectAsState()
    val error by viewModel.error.collectAsState()

    var showAddAddressDialog by remember { mutableStateOf(false) }
    var orderNotes by remember { mutableStateOf("") }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var placedOrder by remember { mutableStateOf<Order?>(null) }

    // Handle order placement success
    LaunchedEffect(orderPlaced) {
        orderPlaced?.let { order ->
            placedOrder = order
            showSuccessDialog = true
            }
        }
    if (showSuccessDialog && placedOrder != null) {
        OrderSuccessDialog(
            orderNumber = placedOrder!!.orderNumber,
            onDismiss = {
                showSuccessDialog = false
                navController.navigate(Screen.OrderTracking.route) {
                    popUpTo(Screen.Cart.route) {inclusive = true}
                }
                placedOrder = null
            }
        )
    }


    Scaffold(
        bottomBar = {
            if (!cart.isEmpty){
                CheckoutBottomBar(
                    cart = cart,
                    onPlaceOrderClick = {viewModel.placeOrder(orderNotes.ifBlank { null })},
                    isLoading = isLoading,
                    enabled = selectedAddress != null
                )
            }
        },
    ) { paddingValues ->
        if (showAddAddressDialog) {
            AddAddressDialog(
                onDismiss = { showAddAddressDialog = false },
                onAddressAdded = { street, city, state, zipCode ->
                    viewModel.addNewAddress(street, city, state, zipCode)
                    showAddAddressDialog = false
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            item {
                CheckoutHeader(onBackClick = {navController.navigateUp()})
            }
            // Cart Summary
            item {
                CartSummarySection(cart = cart)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Delivery Address
            item {
                DeliveryAddressSection(
                    addresses = addresses ?: emptyList(),
                    selectedAddress = selectedAddress,
                    onAddressSelected = { viewModel.selectAddress(it) },
                    onAddAddressClick = { showAddAddressDialog = true }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                PaymentMethodSection()
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                OrderNotesSection(
                    notes = orderNotes,
                    onNotesChange = { orderNotes = it }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            if (error != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = error ?: "",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun CheckoutHeader(onBackClick: () -> Unit){
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(LightGray).clickable(onClick = onBackClick),
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
            text = "Checkout",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121),
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
fun CartSummarySection(cart: Cart) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Order Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            cart.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.productName, fontSize = 14.sp, color = Color(0xFF212121))
                        Text(
                            text = "${item.quantity} × ₹${item.price}",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                    Text(
                        text = "₹${item.totalPrice}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total (${cart.totalItems} items)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = "₹${cart.totalPrice}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
        }
}

@Composable
fun DeliveryAddressSection(
    addresses: List<Address>,
    selectedAddress: Address?,
    onAddressSelected: (Address) -> Unit,
    onAddAddressClick: () -> Unit
) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Delivery Address",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                TextButton(onClick = onAddAddressClick) {
                    Text("Add New", color = DarkBlue)
                }
            }

            if (addresses.isEmpty()) {
                Text(
                    text = "No addresses found. Please add a delivery address.",
                    color = TextGray,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                addresses.forEach { address ->
                    AddressItem(
                        address = address,
                        isSelected = address == selectedAddress,
                        onSelected = { onAddressSelected(address) }
                    )
                }
            }
        }
}

@Composable
fun AddressItem(
    address: Address,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp).background(
                if (isSelected) LightGray.copy(alpha = 0.3f) else Color.White,
                RoundedCornerShape(8.dp)
            ).clickable{onSelected()}
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelected
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = address.fullAddress,
                    fontSize = 14.sp,
                    color = Color(0xFF212121)
                )
                if (address.isDefault) {
                    Text(
                        text = "Default Address",
                        fontSize = 12.sp,
                        color = TextGray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
}

@Composable
fun PaymentMethodSection() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Payment Method",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = true,
                    onClick = { /* COD is the only option for now */ },
                    enabled = false
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Cash on Delivery", fontSize = 16.sp, color =  Color(0xFF212121))
                    Text(
                        text = "Pay when you receive your order",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }
        }
}

@Composable
fun OrderNotesSection(notes: String, onNotesChange: (String) -> Unit) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Order Notes (Optional)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                placeholder = { Text("Any special instructions for delivery...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
        }

}

@Composable
fun CheckoutBottomBar(cart: Cart, onPlaceOrderClick: () -> Unit, isLoading: Boolean, enabled: Boolean = true) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                    text = " ₹${cart.totalPrice}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPlaceOrderClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkBlue
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = enabled && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = if (enabled) "Place Order" else "Select Address First",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun AddAddressDialog(
    onDismiss: () -> Unit,
    onAddressAdded: (String, String, String, String) -> Unit
) {
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Add New Address",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                QuickKartTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = "Street Address"
                )

                Spacer(modifier = Modifier.height(8.dp))

                QuickKartTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = "City"
                )

                Spacer(modifier = Modifier.height(8.dp))

                QuickKartTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = "State"
                )

                Spacer(modifier = Modifier.height(8.dp))

                QuickKartTextField(
                    value = zipCode,
                    onValueChange = { zipCode = it },
                    label = "ZIP Code",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = TextGray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    QuickKartButton(
                        text = "Add Address",
                        onClick = {
                            if (street.isNotBlank() && city.isNotBlank() &&
                                state.isNotBlank() && zipCode.isNotBlank()) {
                                onAddressAdded(street, city, state, zipCode)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = street.isNotBlank() && city.isNotBlank() &&
                                state.isNotBlank() && zipCode.isNotBlank()
                    )
                }
            }
        }
    }
}

@Composable
fun OrderSuccessDialog(
    orderNumber: String,
    onDismiss: () -> Unit
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Order Placed Successfully",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )
        },
        text = {
            Column {
                Text(
                    text = "Your Order has been placed successfully",
                    fontSize = 16.sp,
                    color = Color(0xFF212121),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Order Number: $orderNumber",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
        },
        confirmButton = {
            QuickKartButton(
                text = "Track Order",
                onClick = onDismiss
            )
        },
         containerColor = Color.White

    )
}