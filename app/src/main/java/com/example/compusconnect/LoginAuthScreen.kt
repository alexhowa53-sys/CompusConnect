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

@Composable
fun LoginAuthScreen(
    onLoginSuccess: (isAdmin: Boolean) -> Unit = {}
) {
    var isLoginTab by remember { mutableStateOf(true) }
    var isAdminSelected by remember { mutableStateOf(false) } // false = Student, true = Administrator
    var email by remember { mutableStateOf("alexhowa53@gmail.com") }
    var password by remember { mutableStateOf("••••••••") }
    var rememberMe by remember { mutableStateOf(true) }

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
                        .clickable { isLoginTab = true },
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
                        .clickable { isLoginTab = false },
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

            // Account type selector (Student / Administrator)
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
                        containerColor = if (!isAdminSelected) CampusNavy else Color.White,
                        contentColor = if (!isAdminSelected) CampusCream else CampusNavy
                    )
                ) {
                    Text(text = "Student", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { isAdminSelected = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isAdminSelected) CampusNavy else Color.White,
                        contentColor = if (isAdminSelected) CampusCream else CampusNavy
                    )
                ) {
                    Text(text = "Administrator", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Email input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Campus Email") },
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
                onValueChange = { password = it },
                label = { Text("Password") },
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

            // Login Button
            Button(
                onClick = { onLoginSuccess(isAdminSelected) },
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
