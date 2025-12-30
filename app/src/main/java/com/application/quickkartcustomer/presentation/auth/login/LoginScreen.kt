package com.application.quickkartcustomer.presentation.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.quickkartcustomer.ui.components.QuickKartButton
import com.application.quickkartcustomer.ui.components.QuickKartTextField
import com.application.quickkartcustomer.ui.navigation.Screen
import com.application.quickkartcustomer.ui.theme.Primary
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                //NAVIGATE TO HOME SCREEN
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) {inclusive = true}
                }
            }
            is LoginState.Error -> {
val errorMessage = (loginState as LoginState.Error).message
                scope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
                //keep error state visible until user tries again
            }
            else -> {
                //handle other states in ui
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "QuickKart",
                style = MaterialTheme.typography.displayLarge,
                color = Primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))

            QuickKartTextField(
                value = username,
                onValueChange = {username = it},
                label = "Username"
            )
            Spacer(modifier = Modifier.height(16.dp))

            QuickKartTextField(
                value = password,
                onValueChange = {password = it},
                label = "Password",
                isPassword = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            QuickKartButton(
                text = "Login",
                onClick = {
                    viewModel.login(username, password)
                },
                isLoading = loginState is LoginState.Loading,
                enabled = username.isNotEmpty() && password.isNotEmpty() && loginState !is LoginState.Loading
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
                navController.navigate(Screen.Register.route)
            }) {
                Text("Dont have an account? Register")
            }
        }
    }
}