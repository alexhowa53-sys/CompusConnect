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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compusconnect.ui.theme.CompusConnectTheme

// Who is currently using the app.
// isLoggedIn = false -> Profile screen shows the Login/Sign up prompt
// isLoggedIn = true  -> Profile screen shows the account options + logout
enum class UserRole {
    STUDENT,
    ADMIN
}

data class UserSession(
    val isLoggedIn: Boolean = false,
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.STUDENT
)

@Composable
fun ProfileScreen(
    currentScreen: Int = 3,
    userSession: UserSession = UserSession(),
    onNavigate: (Int) -> Unit = {},
    onLoginClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
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
        if (!userSession.isLoggedIn) {
            LoggedOutProfileContent(
                modifier = Modifier.padding(innerPadding),
                onLoginClick = onLoginClick,
                onSignUpClick = onSignUpClick
            )
        } else {
            LoggedInProfileContent(
                modifier = Modifier.padding(innerPadding),
                userSession = userSession,
                onSwitchToAdminView = onSwitchToAdminView,
                onLogout = onLogout
            )
        }
    }
}

@Composable
private fun LoggedOutProfileContent(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(CampusLightBeige),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = CampusNavy,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "You're not logged in",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = CampusNavy
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Log in to buy tickets, save events, and manage your campus account.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            color = CampusGrey,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onLoginClick,
            colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Log in",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = CampusCream
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onSignUpClick,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CampusNavy)
        ) {
            Text(
                text = "Sign up",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = CampusNavy
            )
        }
    }
}

@Composable
private fun LoggedInProfileContent(
    modifier: Modifier = Modifier,
    userSession: UserSession,
    onSwitchToAdminView: () -> Unit,
    onLogout: () -> Unit
) {
    val initials = userSession.name
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")
        .ifEmpty { "U" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(CampusNavy),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = CampusCream
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userSession.name,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = CampusNavy
        )

        Text(
            text = if (userSession.role == UserRole.ADMIN)
                "${userSession.email} • Administrator"
            else
                "${userSession.email} • Student",
            fontFamily = FontFamily.SansSerif,
            fontSize = 13.sp,
            color = CampusGrey
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Only useful for an admin who is currently browsing the student-facing screens
        if (userSession.role == UserRole.ADMIN) {
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
        }

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
                text = "Log in / Log out",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFFD32F2F)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
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
fun ProfileScreenLoggedOutPreview() {
    CompusConnectTheme {
        ProfileScreen(userSession = UserSession(isLoggedIn = false))
    }
}

@Preview(showBackground = true, widthDp = 392, heightDp = 800)
@Composable
fun ProfileScreenLoggedInPreview() {
    CompusConnectTheme {
        ProfileScreen(
            userSession = UserSession(
                isLoggedIn = true,
                name = "Alex Lee",
                email = "alexhowa53@gmail.com",
                role = UserRole.STUDENT
            )
        )
    }
}
