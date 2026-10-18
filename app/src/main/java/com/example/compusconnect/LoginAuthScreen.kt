
package com.example.compusconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun LoginAuthScreen(
    onLoginSuccess: (isAdmin: Boolean) -> Unit = {}
) {
    var isLoginTab by remember { mutableStateOf(true) }
    var isAdminSelected by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = CampusCream
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

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
                        .background(
                            if (isLoginTab) CampusNavy
                            else Color.Transparent
                        )
                        .clickable {
                            isLoginTab = true
                            errorMessage = ""
                        },
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
                        .background(
                            if (!isLoginTab) CampusNavy
                            else Color.Transparent
                        )
                        .clickable {
                            isLoginTab = false
                            errorMessage = ""
                        },
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

            // Account type selector
            if (!isLoginTab) {
                Text(
                    text = "Account Type",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CampusNavy,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { isAdminSelected = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (!isAdminSelected)
                                CampusNavy else Color.White,
                            contentColor = if (!isAdminSelected)
                                CampusCream else CampusNavy
                        )
                    ) {
                        Text(
                            text = "Student",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = { isAdminSelected = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isAdminSelected)
                                CampusNavy else Color.White,
                            contentColor = if (isAdminSelected)
                                CampusCream else CampusNavy
                        )
                    ) {
                        Text(
                            text = "Administrator",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Full Name input - registration only
            if (!isLoginTab) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        errorMessage = ""
                    },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CampusNavy,
                        unfocusedBorderColor = CampusLightBeige,
                        focusedLabelColor = CampusNavy
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Email input
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                label = { Text("Campus Email") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = CampusNavy
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true,
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
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = CampusNavy
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        enabled = !isLoading,
                        colors = CheckboxDefaults.colors(
                            checkedColor = CampusNavy
                        )
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
                    modifier = Modifier.clickable {
                        errorMessage =
                            "Password recovery is not connected yet."
                    }
                )
            }

            // Error or success message
            if (errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = errorMessage,
                    color = if (
                        errorMessage.startsWith("Account created successfully")
                    ) {
                        Color(0xFF2E7D32)
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Log in / Create Account button
            Button(
                onClick = {
                    errorMessage = ""

                    if (!isLoginTab) {
                        when {
                            fullName.isBlank() -> {
                                errorMessage =
                                    "Please enter your full name."
                            }

                            email.isBlank() -> {
                                errorMessage = "Please enter your email."
                            }

                            !android.util.Patterns.EMAIL_ADDRESS
                                .matcher(email.trim())
                                .matches() -> {
                                errorMessage =
                                    "Please enter a valid email address."
                            }

                            password.length < 8 -> {
                                errorMessage =
                                    "Password must contain at least 8 characters."
                            }

                            else -> {
                                scope.launch {
                                    isLoading = true

                                    try {
                                        val response = ApiClient.register(
                                            name = fullName.trim(),
                                            email = email.trim(),
                                            password = password
                                        )

                                        if (response.optBoolean("success")) {
                                            isLoginTab = true
                                            password = ""
                                            fullName = ""
                                            errorMessage =
                                                "Account created successfully! Please log in."
                                        } else {
                                            errorMessage = response.optString(
                                                "message",
                                                "Registration failed. Please try again."
                                            )
                                        }
                                    } catch (e: Exception) {
                                        errorMessage =
                                            "Could not connect to the server. Check that XAMPP is running and try again."
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    } else if (
                        email.isBlank() || password.isBlank()
                    ) {
                        errorMessage =
                            "Please enter your email and password."
                    } else {
                        scope.launch {
                            isLoading = true

                            try {
                                val response = ApiClient.login(
                                    email = email.trim(),
                                    password = password
                                )

                                if (response.optBoolean("success")) {
                                    val user: JSONObject? =
                                        response.optJSONObject("user")

                                    val role = (
                                            user?.optString("role")
                                                ?: response.optString("role")
                                            ).lowercase()

                                    if (role == "admin") {
                                        onLoginSuccess(true)
                                    } else if (role == "student") {
                                        onLoginSuccess(false)
                                    } else {
                                        errorMessage =
                                            "Login worked, but the account role is missing or invalid."
                                    }
                                } else {
                                    errorMessage = response.optString(
                                        "message",
                                        "Login failed. Please check your details."
                                    )
                                }
                            } catch (e: Exception) {
                                errorMessage =
                                    "Could not connect to the server. Check that XAMPP is running and try again."
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CampusAmber
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = CampusInk,
                        strokeWidth = 2.dp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        if (isLoginTab) "Logging in..."
                        else "Creating account..."
                    )
                } else {
                    Text(
                        text = if (isLoginTab)
                            "Log in to CampusConnect"
                        else
                            "Create Account",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CampusInk
                    )
                }
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
