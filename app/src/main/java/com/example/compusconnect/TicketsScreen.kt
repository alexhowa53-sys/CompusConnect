package com.example.compusconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
fun TicketsScreen(
    currentScreen: Int = 2,
    onNavigate: (Int) -> Unit = {},
    events: List<Event> = sampleEvents,
    onEventClick: (Event) -> Unit = {},
    onToggleSave: (Event) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Purchased, 1: Saved
    val tabs = listOf("Purchased", "Saved")

    val ticketsToShow = remember(selectedTab, events) {
        if (selectedTab == 0) events.filter { it.isPurchased }
        else events.filter { it.isSaved }
    }

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
                .background(CampusCream)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "YOUR CAMPUS PLANS",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = CampusGrey,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "My Tickets",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = CampusNavy
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Segmented Toggle (Purchased / Saved)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(CampusLightBeige)
                        .padding(4.dp)
                ) {
                    tabs.forEachIndexed { index, tabTitle ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isSelected) CampusNavy else Color.Transparent)
                                .clickable { selectedTab = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tabTitle,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = if (isSelected) CampusCream else CampusGrey
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Coming Up Section / List
            Text(
                text = if (selectedTab == 0) "Coming up" else "Saved events",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = CampusNavy,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (ticketsToShow.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedTab == 0) "No purchased tickets yet." else "No saved events yet.",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        color = CampusGrey
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(ticketsToShow) { event ->
                        UserTicketCard(
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
fun UserTicketCard(
    event: Event,
    onClick: () -> Unit = {},
    onToggleSave: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Date Block
            Box(
                modifier = Modifier
                    .width(88.dp)
                    .fillMaxHeight()
                    .background(event.category.color),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = event.day,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = CampusCream
                    )
                    Text(
                        text = event.month,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (event.category == EventCategory.MUSIC) CampusNavy else CampusAmber
                    )
                }

                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .align(Alignment.CenterEnd)
                        .offset(x = 8.dp)
                )
            }

            // Right Details
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
                        color = event.category.color,
                        letterSpacing = 1.sp
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Status Badge if purchased
                        if (event.isPurchased) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CampusAmber)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Ready",
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = CampusInk
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        IconButton(
                            onClick = onToggleSave,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (event.isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Save",
                                tint = if (event.isSaved) Color(0xFF6650a4) else CampusGrey,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = event.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = CampusInk
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${event.time} • ${event.location}",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 13.sp,
                    color = CampusGrey
                )
            }
        }
    }
}

// 4-item bottom navigation bar for Home (0), Search (1), Tickets (2), Profile (3)
@Composable
fun CampusBottomBar4(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem("Home", Icons.Default.Home, selectedIndex == 0) { onItemSelected(0) }
            BottomNavItem("Search", Icons.Default.Search, selectedIndex == 1) { onItemSelected(1) }
            BottomNavItem("Tickets", Icons.Default.ConfirmationNumber, selectedIndex == 2) { onItemSelected(2) }
            BottomNavItem("Profile", Icons.Default.Person, selectedIndex == 3) { onItemSelected(3) }
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tintColor = if (isSelected) CampusNavy else CampusGrey

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tintColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontFamily = FontFamily.SansSerif,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 11.sp,
            color = tintColor
        )
    }
}

@Preview(showBackground = true, widthDp = 392, heightDp = 800)
@Composable
fun TicketsScreenPreview() {
    CompusConnectTheme {
        TicketsScreen()
    }
}
