package com.application.quickkartcustomer.presentation.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.quickkartcustomer.ui.components.QuickKartButton
import com.application.quickkartcustomer.ui.components.QuickKartTextField
import com.application.quickkartcustomer.ui.navigation.Screen
import com.application.quickkartcustomer.ui.theme.Primary


@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
){
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by  remember { mutableStateOf(false)}

    val registerState by viewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                isLoading = false
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Register.route) {inclusive=true}
                }
            }
            is RegisterState.Error -> {
                isLoading = false
            }
            RegisterState.Loading -> {
                isLoading = true
            }
            else -> {
                isLoading = false
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.displayMedium,
            color = Primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        QuickKartTextField(
            value = username,
            onValueChange = {username = it},
            label = "Username"
        )
        Spacer(modifier = Modifier.height(12.dp))

        QuickKartTextField(
            value = email,
            onValueChange = {email = it},
            label = "Email",
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
        )
        Spacer(modifier =  Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            QuickKartTextField(
                value = firstName,
                onValueChange = {firstName = it},
                label = "First Name",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            QuickKartTextField(
                value = lastName,
                onValueChange = {lastName = it},
                label = "Last Name",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        QuickKartTextField(
            value = phoneNumber,
            onValueChange = {phoneNumber = it},
            label = "Phone Number",
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
        )
        Spacer(modifier = Modifier.height(12.dp))

        QuickKartTextField(
            value = password,
            onValueChange = {password = it},
            label = "Password",
            isPassword = true
        )
        Spacer(modifier = Modifier.height(24.dp))

        QuickKartButton(
            text = "Register",
            onClick = {
                viewModel.register(username, email, password, firstName, lastName, phoneNumber)
            },
            isLoading = isLoading,
            enabled = username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            navController.popBackStack()
        }) {
            Text("Already have an account? Login")
        }
    }
}