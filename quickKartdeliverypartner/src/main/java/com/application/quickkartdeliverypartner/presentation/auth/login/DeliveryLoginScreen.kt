package com.application.quickkartdeliverypartner.presentation.auth.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.application.quickkartdeliverypartner.ui.navigation.DeliveryScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryLoginScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel = remember { DeliveryLoginViewModel(context) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                navController.navigate(DeliveryScreen.Assignments.route) {
                    popUpTo(DeliveryScreen.Login.route) { inclusive = true }
                }
            }
            is LoginState.Error -> {
                // Error is already shown in the UI
            }
            is LoginState.Idle, is LoginState.Loading -> {
                // No action needed
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Delivery Partner") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Delivery Partner Login",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login(username, password) },
                modifier = Modifier.fillMaxWidth(),
                enabled = username.isNotEmpty() && password.isNotEmpty()
            ) {
                Text("Login as Delivery Partner")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate(DeliveryScreen.Register.route) }) {
                Text("Don't have an account? Register")
            }

            if (loginState is LoginState.Loading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            (loginState as? LoginState.Error)?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}