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
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compusconnect.ui.theme.CompusConnectTheme

// Category date-block colors are shared from HomeScreen.kt

data class SearchEvent(
    val id: String,
    val title: String,
    val day: String,
    val month: String,
    val time: String,
    val location: String,
    val category: EventCategory,
    val isPaid: Boolean,
    val price: String
)

val searchSampleEvents = listOf(
    SearchEvent("1", "Homecoming Concert Night", "14", "OCT", "7:00 PM", "Great Hall Lawn", EventCategory.MUSIC, true, "$5.00"),
    SearchEvent("2", "Tech Careers Fair", "16", "OCT", "10:00 AM", "Student Center Hall B", EventCategory.CAREER, false, ""),
    SearchEvent("3", "Inter-Hall Football Finals", "18", "OCT", "4:30 PM", "Main Sports Complex", EventCategory.SPORTS, true, "$2.00"),
    SearchEvent("4", "Poetry & Spoken Word Night", "21", "OCT", "6:00 PM", "Library Courtyard", EventCategory.COMMUNITY, false, ""),
    SearchEvent("5", "Jazz in the Quad", "23", "OCT", "5:00 PM", "South Quad", EventCategory.MUSIC, false, "")
)

@Composable
fun SearchScreen(
    currentScreen: Int = 1,
    onNavigate: (Int) -> Unit = {},
    onEventClick: (SearchEvent) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("This week") }
    val filters = listOf("This week", "Free", "Category", "Time")

    // Filter events based on selected filter
    val filteredEvents = remember(selectedFilter) {
        when (selectedFilter) {
            "Free" -> searchSampleEvents.filter { !it.isPaid }
            "Category" -> searchSampleEvents.filter { it.category == EventCategory.MUSIC }
            "Time" -> searchSampleEvents.sortedBy { it.day }
            else -> searchSampleEvents
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

                // Search Bar below headline
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
                    Text(
                        text = "Search by event or organization",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 15.sp,
                        color = CampusGrey.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )
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
                    SearchEventCard(event = event, onClick = { onEventClick(event) })
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
fun SearchEventCard(event: SearchEvent, onClick: () -> Unit = {}) {
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
