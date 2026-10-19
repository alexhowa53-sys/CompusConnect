package com.example.compusconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compusconnect.ui.theme.CompusConnectTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAuthScreen(
    onLoginSuccess: (UserSession) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var isLoginTab by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = CampusCream,
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CampusNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CampusCream)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Branding Top Header
            Text(
                text = "CampusConnect",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = CampusNavy
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Your campus, all connected...",
                fontFamily = FontFamily.SansSerif,
                fontSize = 15.sp,
                color = CampusGrey
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Log in / Sign up tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(23.dp))
                    .background(CampusLightBeige)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(19.dp))
                        .background(if (isLoginTab) CampusNavy else Color.Transparent)
                        .clickable { isLoginTab = true; errorMessage = null },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Log in",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = if (isLoginTab) CampusCream else CampusGrey
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(19.dp))
                        .background(if (!isLoginTab) CampusNavy else Color.Transparent)
                        .clickable { isLoginTab = false; errorMessage = null },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign up",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = if (!isLoginTab) CampusCream else CampusGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) {
                Surface(
                    color = Color(0xFFF8D7DA),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = Color(0xFF721C24))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFF721C24),
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 13.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Email input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text("Campus Email") },
                placeholder = { Text("e.g. alex@university.edu or admin@compusconnect.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CampusNavy) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CampusNavy,
                    unfocusedBorderColor = CampusLightBeige,
                    focusedLabelColor = CampusNavy
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = { Text("Password") },
                placeholder = { Text("••••••••") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CampusNavy) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CampusNavy,
                    unfocusedBorderColor = CampusLightBeige,
                    focusedLabelColor = CampusNavy
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Remember me
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = CampusNavy)
                    )
                    Text(
                        text = "Remember me",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        color = CampusInk
                    )
                }

                Text(
                    text = "Forgot password?",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = CampusNavy,
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button (Log in / Create Account)
            Button(
                onClick = {
                    if (isLoginTab) {
                        val session = attemptLogin(email, password)
                        if (session == null) {
                            errorMessage = "Invalid email or password (Test admin: admin@compusconnect.com / admin123)"
                        } else {
                            onLoginSuccess(session)
                        }
                    } else {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Please enter email and password"
                        } else {
                            // New sign-ups default to Student role; backend handles admin accounts securely
                            val session = UserSession(
                                isLoggedIn = true,
                                name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                                email = email.trim(),
                                role = UserRole.STUDENT
                            )
                            onLoginSuccess(session)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CampusAmber),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (isLoginTab) "Log in to CampusConnect" else "Create Account",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = CampusInk
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 392, heightDp = 800)
@Composable
fun LoginAuthScreenPreview() {
    CompusConnectTheme {
        LoginAuthScreen()
    }
}
