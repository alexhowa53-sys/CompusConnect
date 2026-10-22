package com.example.compusconnect

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    userSession: UserSession,
    onBackClick: () -> Unit = {},
    onEventClick: (String) -> Unit = {}
) {
    val repository = remember { CampusRepository() }
    var notifications by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userSession.uid) {
        if (userSession.isLoggedIn && userSession.uid.isNotBlank()) {
            repository.markNotificationsAsSeen(userSession.uid)
            notifications = repository.fetchNotifications(userSession.uid)
        }
        isLoading = false
    }

    Scaffold(
        containerColor = CampusCream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = CampusNavy
                    )
                },
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
                .background(CampusCream)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CampusNavy)
                }
            } else if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "You're all caught up",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 15.sp,
                        color = CampusGrey
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications) { notif ->
                        NotificationCard(
                            notification = notif,
                            onClick = {
                                if (notif.eventId != null) {
                                    onEventClick(notif.eventId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem, onClick: () -> Unit) {
    val timeAgo = formatTimeAgo(notification.createdAt)
    val icon = when (notification.type) {
        "ticket_confirmed" -> Icons.Default.ConfirmationNumber
        "event_updated" -> Icons.Default.Update
        "event_cancelled" -> Icons.Default.Cancel
        else -> Icons.Default.Event
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = CampusNavy.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = CampusNavy,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CampusInk
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CampusAmber)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.body,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 13.sp,
                    color = CampusGrey
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = timeAgo,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    color = CampusGrey.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun NotificationBell(
    unreadCount: Int,
    isLoggedIn: Boolean,
    onLoginPrompt: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.15f))
            .clickable {
                if (isLoggedIn) onClick() else onLoginPrompt()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        if (isLoggedIn && unreadCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-2).dp, y = 2.dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD32F2F))
                    .border(BorderStroke(1.dp, CampusNavy), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

fun formatTimeAgo(timeMillis: Long): String {
    val diff = System.currentTimeMillis() - timeMillis
    val hours = diff / (1000 * 60 * 60)
    val days = hours / 24
    return when {
        days > 0 -> "$days d ago"
        hours > 0 -> "$hours h ago"
        else -> "Just now"
    }
}
