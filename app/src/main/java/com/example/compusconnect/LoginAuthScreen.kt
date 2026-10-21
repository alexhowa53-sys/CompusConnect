package com.example.compusconnect

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compusconnect.ui.theme.CompusConnectTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAuthScreen(
    onLoginSuccess: (UserSession) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var authStep by remember { mutableStateOf<AuthStep>(AuthStep.FORM) }
    var isLoginTab by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // OTP verification fields
    var sentOtp by remember { mutableStateOf("4852") }
    var enteredOtp by remember { mutableStateOf("") }

    val repository = remember { CampusRepository() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = CampusCream,
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (authStep == AuthStep.OTP) {
                            authStep = AuthStep.FORM
                        } else {
                            onBackClick()
                        }
                    }) {
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
        when (authStep) {
            AuthStep.FORM -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Title & Subtitle matching Dribbble template
                    Text(
                        text = if (isLoginTab) "Welcome back" else "Sign up Account",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = CampusNavy,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isLoginTab)
                            "Access your tickets, wishlist, and exclusive offers by logging in."
                        else
                            "Join now for a faster, smarter campus experience.",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        color = CampusNavy.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

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

                    // Name Field (Only in Sign up)
                    if (!isLoginTab) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it; errorMessage = null },
                            label = { Text("Name") },
                            placeholder = { Text("Enter your name", color = CampusNavy.copy(alpha = 0.6f)) },
                            textStyle = LocalTextStyle.current.copy(color = CampusInk, fontWeight = FontWeight.Medium),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CampusNavy,
                                unfocusedBorderColor = CampusLightBeige,
                                focusedTextColor = CampusInk,
                                unfocusedTextColor = CampusInk,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        label = { Text("Email") },
                        placeholder = { Text("Enter your email", color = CampusNavy.copy(alpha = 0.6f)) },
                        textStyle = LocalTextStyle.current.copy(color = CampusInk, fontWeight = FontWeight.Medium),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CampusNavy,
                            unfocusedBorderColor = CampusLightBeige,
                            focusedTextColor = CampusInk,
                            unfocusedTextColor = CampusInk,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        label = { Text("Password") },
                        placeholder = { Text("Enter your password", color = CampusNavy.copy(alpha = 0.6f)) },
                        textStyle = LocalTextStyle.current.copy(color = CampusInk, fontWeight = FontWeight.Medium),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = icon, contentDescription = "Toggle password", tint = CampusNavy)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CampusNavy,
                            unfocusedBorderColor = CampusLightBeige,
                            focusedTextColor = CampusInk,
                            unfocusedTextColor = CampusInk,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Remember me & Forgot Password
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

                        if (isLoginTab) {
                            Text(
                                text = "Forgot password?",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.clickable { }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Primary Dark Button ("Sign in" / "Sign up")
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please enter email and password"
                                return@Button
                            }
                            isLoading = true
                            errorMessage = null

                            coroutineScope.launch {
                                if (isLoginTab) {
                                    val result = repository.signIn(email, password)
                                    isLoading = false
                                    result.fold(
                                        onSuccess = { session -> onLoginSuccess(session) },
                                        onFailure = { error ->
                                            val msg = error.message ?: ""
                                            errorMessage = when {
                                                msg.contains("no user record", true) || msg.contains("user-not-found", true) ->
                                                    "No account found with this email. Please sign up first."
                                                msg.contains("password", true) || msg.contains("wrong-password", true) || msg.contains("invalid-credential", true) ->
                                                    "Incorrect password. Please try again."
                                                msg.contains("network", true) || msg.contains("internet", true) ->
                                                    "Network error. Please check your internet connection."
                                                else -> "Account not found or invalid password. Please sign up first if you don't have an account."
                                            }
                                        }
                                    )
                                } else {
                                    if (name.isBlank()) {
                                        isLoading = false
                                        errorMessage = "Please enter your name"
                                        return@launch
                                    }
                                    // Generate a random 4-digit OTP and send to email
                                    sentOtp = (1000..9999).random().toString()
                                    isLoading = false
                                    authStep = AuthStep.OTP
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CampusInk),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = if (isLoginTab) "Sign in" else "Sign up",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // OR Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = CampusLightBeige)
                        Text(
                            text = "  OR  ",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            color = CampusGrey
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = CampusLightBeige)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Social Buttons (Google / Apple with working simulation)
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                val googleSession = UserSession(
                                    isLoggedIn = true,
                                    name = "Google Student",
                                    email = "student.google@university.edu",
                                    role = UserRole.STUDENT
                                )
                                onLoginSuccess(googleSession)
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            GoogleIcon()
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with Google",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = CampusInk
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                val appleSession = UserSession(
                                    isLoggedIn = true,
                                    name = "Apple Student",
                                    email = "student.apple@university.edu",
                                    role = UserRole.STUDENT
                                )
                                onLoginSuccess(appleSession)
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AppleIcon()
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with Apple",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = CampusInk
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Bottom Switch Footer
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isLoginTab) "Don't have an account? " else "Already have an account? ",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 13.sp,
                            color = CampusGrey
                        )
                        Text(
                            text = if (isLoginTab) "Sign up" else "Sign in",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CampusNavy,
                            modifier = Modifier.clickable {
                                isLoginTab = !isLoginTab
                                errorMessage = null
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            AuthStep.OTP -> {
                // OTP Verification Screen (matching the 3rd frame in screenshot)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Enter OTP Code",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = CampusNavy,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "We've sent a 4-digit verification code to $email.\n(Test OTP Code: $sentOtp)",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        color = CampusNavy.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(36.dp))

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

                    // OTP Digit Input Field
                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = { if (it.length <= 4) enteredOtp = it; errorMessage = null },
                        label = { Text("Verification Code") },
                        placeholder = { Text("Enter 4-digit code", color = CampusNavy.copy(alpha = 0.6f)) },
                        textStyle = LocalTextStyle.current.copy(color = CampusInk, fontWeight = FontWeight.Medium),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CampusNavy,
                            unfocusedBorderColor = CampusLightBeige,
                            focusedTextColor = CampusInk,
                            unfocusedTextColor = CampusInk,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.width(220.dp)
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Button(
                        onClick = {
                            if (enteredOtp.trim() != sentOtp) {
                                errorMessage = "Invalid verification code. Please check $sentOtp"
                                return@Button
                            }
                            isLoading = true
                            errorMessage = null
                            coroutineScope.launch {
                                val result = repository.signUp(email, password, name)
                                isLoading = false
                                result.fold(
                                    onSuccess = { session -> onLoginSuccess(session) },
                                    onFailure = { error ->
                                        val msg = error.message ?: ""
                                        errorMessage = when {
                                            msg.contains("email-already-in-use", true) ->
                                                "This email is already registered. Please log in instead."
                                            msg.contains("weak-password", true) ->
                                                "Password should be at least 6 characters."
                                            else -> "Sign up failed: ${error.localizedMessage ?: "Unknown error"}"
                                        }
                                        authStep = AuthStep.FORM
                                    }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CampusInk),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = "Continue",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Didn't get OTP? ",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 13.sp,
                            color = CampusGrey
                        )
                        Text(
                            text = "Resend OTP",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CampusNavy,
                            modifier = Modifier.clickable {
                                sentOtp = (1000..9999).random().toString()
                                errorMessage = "New OTP sent to $email!"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleIcon() {
    Box(
        modifier = Modifier.size(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(20.dp)) {
            val size = size
            drawArc(color = Color(0xFFEA4335), startAngle = -90f, sweepAngle = 90f, useCenter = true, size = size)
            drawArc(color = Color(0xFFFBBC05), startAngle = 180f, sweepAngle = 90f, useCenter = true, size = size)
            drawArc(color = Color(0xFF34A853), startAngle = 90f, sweepAngle = 90f, useCenter = true, size = size)
            drawArc(color = Color(0xFF4285F4), startAngle = 0f, sweepAngle = 90f, useCenter = true, size = size)
            drawCircle(color = Color.White, radius = size.minDimension * 0.28f, center = center)
            drawRect(
                color = Color(0xFF4285F4),
                topLeft = Offset(center.x, center.y - size.minDimension * 0.14f),
                size = Size(size.width * 0.45f, size.height * 0.28f)
            )
        }
    }
}

@Composable
fun AppleIcon() {
    Box(
        modifier = Modifier.size(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(20.dp)) {
            val path = Path().apply {
                moveTo(size.width * 0.5f, size.height * 0.15f)
                cubicTo(size.width * 0.42f, size.height * 0.15f, size.width * 0.35f, size.height * 0.22f, size.width * 0.35f, size.height * 0.3f)
                cubicTo(size.width * 0.35f, size.height * 0.32f, size.width * 0.36f, size.height * 0.35f, size.width * 0.38f, size.height * 0.38f)
                cubicTo(size.width * 0.32f, size.height * 0.38f, size.width * 0.25f, size.height * 0.42f, size.width * 0.22f, size.height * 0.49f)
                cubicTo(size.width * 0.18f, size.height * 0.57f, size.width * 0.22f, size.height * 0.72f, size.width * 0.30f, size.height * 0.84f)
                cubicTo(size.width * 0.34f, size.height * 0.90f, size.width * 0.38f, size.height * 0.95f, size.width * 0.44f, size.height * 0.95f)
                cubicTo(size.width * 0.49f, size.height * 0.95f, size.width * 0.51f, size.height * 0.91f, size.width * 0.57f, size.height * 0.91f)
                cubicTo(size.width * 0.63f, size.height * 0.91f, size.width * 0.65f, size.height * 0.95f, size.width * 0.70f, size.height * 0.95f)
                cubicTo(size.width * 0.76f, size.height * 0.95f, size.width * 0.80f, size.height * 0.90f, size.width * 0.84f, size.height * 0.84f)
                cubicTo(size.width * 0.92f, size.height * 0.72f, size.width * 0.96f, size.height * 0.57f, size.width * 0.92f, size.height * 0.49f)
                cubicTo(size.width * 0.89f, size.height * 0.42f, size.width * 0.82f, size.height * 0.38f, size.width * 0.76f, size.height * 0.38f)
                cubicTo(size.width * 0.71f, size.height * 0.38f, size.width * 0.66f, size.height * 0.41f, size.width * 0.63f, size.height * 0.41f)
                cubicTo(size.width * 0.60f, size.height * 0.35f, size.width * 0.57f, size.height * 0.15f, size.width * 0.5f, size.height * 0.15f)
                close()
                moveTo(size.width * 0.59f, size.height * 0.08f)
                cubicTo(size.width * 0.62f, size.height * 0.04f, size.width * 0.61f, size.height * 0.01f, size.width * 0.60f, size.height * 0.0f)
                cubicTo(size.width * 0.55f, size.height * 0.0f, size.width * 0.51f, size.height * 0.04f, size.width * 0.49f, size.height * 0.08f)
                cubicTo(size.width * 0.47f, size.height * 0.12f, size.width * 0.49f, size.height * 0.16f, size.width * 0.50f, size.height * 0.17f)
                cubicTo(size.width * 0.53f, size.height * 0.18f, size.width * 0.57f, size.height * 0.13f, size.width * 0.59f, size.height * 0.08f)
                close()
            }
            drawPath(path = path, color = Color.Black)
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
