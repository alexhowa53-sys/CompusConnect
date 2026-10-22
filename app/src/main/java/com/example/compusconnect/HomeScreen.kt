package com.example.compusconnect

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.compusconnect.ui.theme.CompusConnectTheme

val Navy = Color(0xFF132A4C)
val Amber = Color(0xFFF2A93B)
val Sage = Color(0xFFCFE0D6)
val SageText = Color(0xFF2F5A44)
val Cream = Color(0xFFFAF7F0)
val Ink = Color(0xFF1C1C1C)
val Grey = Color(0xFF767268)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentScreen: Int = 0,
    onNavigate: (Int) -> Unit = {},
    events: List<Event> = sampleEvents,
    onEventClick: (Event) -> Unit = {},
    onToggleSave: (Event) -> Unit = {},
    unreadNotificationCount: Int = 0,
    isLoggedIn: Boolean = false,
    onNotificationClick: () -> Unit = {},
    onLoginPrompt: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "This week", "Music", "Sports", "Career", "Free")

    val filteredEvents = remember(searchQuery, selectedCategoryFilter, events) {
        events.filter { event ->
            val matchesSearch = event.title.contains(searchQuery, ignoreCase = true) ||
                    event.location.contains(searchQuery, ignoreCase = true) ||
                    event.description.contains(searchQuery, ignoreCase = true)

            val matchesCategory = when (selectedCategoryFilter) {
                "All" -> true
                "This week" -> true
                "Music" -> event.category == EventCategory.MUSIC
                "Sports" -> event.category == EventCategory.SPORTS
                "Career" -> event.category == EventCategory.CAREER || event.category == EventCategory.COMMUNITY
                "Free" -> !event.isPaid
                else -> true
            }
            matchesSearch && matchesCategory
        }
    }

    Scaffold(
        containerColor = Cream,
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
                .background(Cream)
        ) {
            // ---- Curved Navy Header matching Admin Dashboard design ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(Navy)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CampusLogo(size = 36.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "CampusConnect",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = Color.White
                            )
                        }

                        NotificationBell(
                            unreadCount = unreadNotificationCount,
                            isLoggedIn = isLoggedIn,
                            onLoginPrompt = onLoginPrompt,
                            onClick = onNotificationClick
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search for eventname,location...", color = Color(0xFFB9C6DB), fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Color(0xFFD7DEE9)) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White.copy(alpha = 0.3f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ---- Category chips ----
            LazyRow(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isActive = category == selectedCategoryFilter
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { selectedCategoryFilter = category },
                        color = if (isActive) Navy else Color.White,
                        shape = RoundedCornerShape(20.dp),
                        border = if (!isActive) BorderStroke(1.dp, Color(0xFFDCD5C9)) else null
                    ) {
                        Text(
                            text = category,
                            color = if (isActive) Color.White else Navy,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // ---- Section Heading ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Navy
                )
                Text(
                    text = "${filteredEvents.size} events",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 13.sp,
                    color = Grey
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ---- Event list ----
            if (filteredEvents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No events found",
                        color = Grey,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(filteredEvents) { event ->
                        EventCard(
                            event = event,
                            onClick = { onEventClick(event) },
                            onToggleSave = { onToggleSave(event) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit = {}, onToggleSave: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date block (left side, like a ticket stub)
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(event.category.color),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = event.month.uppercase(),
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = event.day,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 34.sp,
                        color = Color.White
                    )
                }
            }

            // Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.category.label,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Grey,
                        letterSpacing = 1.2.sp
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val badgeBg = if (event.isPaid) Amber else Sage
                        val badgeText = if (event.isPaid) event.price else "Free"
                        val badgeTextColor = if (event.isPaid) Color(0xFF4A2E05) else SageText

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = badgeBg
                        ) {
                            Text(
                                text = badgeText,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = badgeTextColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = onToggleSave,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (event.isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Save event",
                                tint = if (event.isSaved) Navy else Grey,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = event.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Ink,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${event.time} · ${event.location}",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.5.sp,
                    color = Grey
                )
            }
        }
    }
}

@Composable
fun EventPosterVisual(
    posterUrl: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(modifier = modifier) {
        if (posterUrl.startsWith("http") || posterUrl.startsWith("content://")) {
            AsyncImage(
                model = posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Navy),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CAMPUS EVENT",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 20.sp
                )
            }
        }
        content()
    }
}

@Preview(showBackground = true, widthDp = 392, heightDp = 800)
@Composable
fun HomeScreenPreview() {
    CompusConnectTheme {
        HomeScreen()
    }
}
