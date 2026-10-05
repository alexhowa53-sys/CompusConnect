package com.example.compusconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compusconnect.ui.theme.CompusConnectTheme

@Composable
fun ProfileScreen(
    currentScreen: Int = 3,
    onNavigate: (Int) -> Unit = {},
    onSwitchToAdminView: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Scaffold(
        containerColor = CampusCream,
        bottomBar = {
            CampusBottomBar4(
                selectedIndex = currentScreen,
                onItemSelected = onNavigate
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile Avatar & Name
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(CampusNavy),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AL",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = CampusCream
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Alex Lee",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = CampusNavy
            )

            Text(
                text = "alexhowa53@gmail.com• Computer Science '26",
                fontFamily = FontFamily.SansSerif,
                fontSize = 13.sp,
                color = CampusGrey
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Switch to Admin Dashboard View Button (as requested)
            Button(
                onClick = onSwitchToAdminView,
                colors = ButtonDefaults.buttonColors(containerColor = CampusAmber),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = CampusInk)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Switch to Admin Dashboard View",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = CampusInk
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Settings & Options Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileOptionRow(icon = Icons.Default.Notifications, title = "Notification Preferences")
                    ProfileOptionRow(icon = Icons.Default.Favorite, title = "Saved Clubs & Organizations")
                    ProfileOptionRow(icon = Icons.Default.Payment, title = "Payment Methods & Passes")
                    ProfileOptionRow(icon = Icons.Default.Help, title = "Campus Support & Help")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            OutlinedButton(
                onClick = onLogout,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
            ) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = Color(0xFFD32F2F))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log out",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFFD32F2F)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileOptionRow(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CampusLightBeige),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CampusNavy,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = CampusInk,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = CampusGrey,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 392, heightDp = 800)
@Composable
fun ProfileScreenPreview() {
    CompusConnectTheme {
        ProfileScreen()
    }
}
