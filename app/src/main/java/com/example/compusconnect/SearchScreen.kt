package com.example.compusconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compusconnect.ui.theme.CompusConnectTheme

@Composable
fun SearchScreen(
    currentScreen: Int = 1,
    onNavigate: (Int) -> Unit = {},
    events: List<Event> = sampleEvents,
    onEventClick: (Event) -> Unit = {},
    onToggleSave: (Event) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("All", "This week", "Music", "Sports", "Career", "Free")

    // Filter events based on selected filter and real-time search query matching title, location, or description
    val filteredEvents = remember(events, selectedFilter, searchQuery) {
        val query = searchQuery.trim().lowercase()
        events.filter { event ->
            val matchesCategory = when (selectedFilter) {
                "Free" -> !event.isPaid
                "Music" -> event.category == EventCategory.MUSIC
                "Sports" -> event.category == EventCategory.SPORTS
                "Career" -> event.category == EventCategory.CAREER || event.category == EventCategory.COMMUNITY
                "This week" -> true
                else -> true
            }
            val matchesQuery = query.isEmpty() ||
                event.title.lowercase().contains(query) ||
                event.location.lowercase().contains(query) ||
                event.description.lowercase().contains(query)
            matchesCategory && matchesQuery
        }
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
            // Top Section on cream background
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Eyebrow label
                Text(
                    text = "EXPLORE CAMPUS",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = CampusGrey,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Large serif headline
                Text(
                    text = "Find your next thing",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = CampusNavy
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Search Bar below headline
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(2.dp, RoundedCornerShape(26.dp))
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CampusGrey,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search by event, location, or description",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 14.sp,
                                color = CampusGrey.copy(alpha = 0.7f)
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 14.sp,
                                color = CampusInk
                            )
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = CampusGrey,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        IconButton(onClick = { /* Open filters */ }) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter",
                                tint = CampusNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Horizontal scrollable filter chips below search bar
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    SearchFilterChip(
                        text = filter,
                        isSelected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Row showing result count on the left and sort dropdown on the right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredEvents.size} results",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = CampusNavy
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Show sort menu */ }
                ) {
                    Text(
                        text = "Soonest",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        color = CampusGrey
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Sort",
                        tint = CampusGrey,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable list of event cards
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredEvents) { event ->
                    SearchEventCard(
                        event = event,
                        onClick = { onEventClick(event) },
                        onToggleSave = { onToggleSave(event) }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) CampusNavy else Color.White
    val textColor = if (isSelected) CampusCream else CampusNavy
    val borderModifier = if (!isSelected) Modifier.border(1.dp, CampusLightBeige, RoundedCornerShape(20.dp)) else Modifier

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .then(borderModifier)
            .clickable(onClick = onClick),
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontFamily = FontFamily.SansSerif,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 13.sp,
                color = textColor
            )
        }
    }
}

@Composable
fun SearchEventCard(
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
            // Left Side: Colored date block based on category
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

                // Perforation notch
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .align(Alignment.CenterEnd)
                        .offset(x = 8.dp)
                )
            }

            // Right Side: Event Details with Category Label
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Category Label
                        Text(
                            text = event.category.label,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = event.category.color,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Event Title (Bold Serif)
                        Text(
                            text = event.title,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CampusInk
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Time and Location
                        Text(
                            text = "${event.time} • ${event.location}",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 13.sp,
                            color = CampusGrey
                        )
                    }

                    // Bookmark / Heart Toggle Button
                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (event.isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save event",
                            tint = if (event.isSaved) Color(0xFF6650a4) else CampusGrey,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price / Free Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (event.isPaid) CampusAmber else CampusSage)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (event.isPaid) "Paid · ${event.price}" else "Free",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = if (event.isPaid) CampusInk else CampusSageText
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 392, heightDp = 800)
@Composable
fun SearchScreenPreview() {
    CompusConnectTheme {
        SearchScreen()
    }
}
