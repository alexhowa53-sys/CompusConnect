package com.example.compusconnect

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.compusconnect.ui.theme.CompusConnectTheme

// ==========================================
// 1. DESIGN SYSTEM COLORS & DATA MODEL
// ==========================================

val CampusNavy = Color(0xFF132A4C)      // Primary / Header background / Sports category
val CampusAmber = Color(0xFFF2A93B)     // Accent / Music category / Paid tags
val CampusSage = Color(0xFFCFE0D6)      // Free tag background
val CampusSageText = Color(0xFF2F5A44)  // Free tag text color
val CampusCream = Color(0xFFFAF7F0)     // Screen background
val CampusInk = Color(0xFF1C1C1C)       // Body text color
val CampusGrey = Color(0xFF767268)      // Secondary / metadata text color
val CampusLightBeige = Color(0xFFEFECE6)// Unselected filter chip background
val CampusSubtitleBlue = Color(0xFFA3B5CD)// Header subtitle color

/**
 * Event categories with their specific date-block colors:
 * - Amber for Music events
 * - Navy for Sports events
 * - Dark green for Career/Community events
 */
enum class EventCategory(val color: Color, val label: String) {
    MUSIC(Color(0xFFF2A93B), "MUSIC"),       // Amber
    SPORTS(Color(0xFF132A4C), "SPORTS"),      // Navy
    CAREER(Color(0xFF2F5A44), "CAREER"),      // Dark Green
    COMMUNITY(Color(0xFF2F5A44), "COMMUNITY") // Dark Green
}

/**
 * Data class representing an event on campus.
 */
/**
 * Poster artwork visual configuration option for campus events.
 */
data class PosterOption(
    val id: String,
    val name: String,
    val colors: List<Color>
)

val campusPosterOptions = listOf(
    PosterOption(
        id = "poster_1",
        name = "Neon Festival",
        colors = listOf(Color(0xFFF2A93B), Color(0xFF132A4C), Color(0xFFE63946))
    ),
    PosterOption(
        id = "poster_2",
        name = "Tech Innovation",
        colors = listOf(Color(0xFF132A4C), Color(0xFF2F5A44), Color(0xFF457B9D))
    ),
    PosterOption(
        id = "poster_3",
        name = "Stadium Championship",
        colors = listOf(Color(0xFF1D3557), Color(0xFFE63946), Color(0xFFF1FAEE))
    ),
    PosterOption(
        id = "poster_4",
        name = "Campus Arts & Poetry",
        colors = listOf(Color(0xFF2F5A44), Color(0xFFF4A261), Color(0xFF264653))
    )
)

/**
 * Renders the event's poster/artwork visual with a subtle gradient overlay for readability.
 */
@Composable
fun EventPosterVisual(
    posterUrl: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val isMediaUri = posterUrl.startsWith("http://") || 
                     posterUrl.startsWith("https://") || 
                     posterUrl.startsWith("content://") || 
                     posterUrl.startsWith("file://")

    Box(modifier = modifier) {
        if (isMediaUri) {
            AsyncImage(
                model = posterUrl,
                contentDescription = "Event Poster",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            val poster = campusPosterOptions.find { it.id == posterUrl } ?: campusPosterOptions.first()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = poster.colors
                        )
                    )
            )
        }

        // Dark gradient overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                    )
                )
        )
        content()
    }
}

/**
 * Data class representing an event on campus.
 */
data class Event(
    val id: String,
    val title: String,
    val day: String,
    val month: String,
    val time: String,
    val location: String,
    val description: String,
    val category: EventCategory,
    val isPaid: Boolean,
    val price: String,
    val isSaved: Boolean = false,
    val isPurchased: Boolean = false,
    val posterUrl: String = "poster_1"
)

// Sample events data list with category date-block colors
val sampleEvents = listOf(
    Event(
        id = "1",
        title = "Welcoming Function",
        day = "14",
        month = "OCT",
        time = "7:00 PM",
        location = "Great Hall",
        description = "Join us for the biggest night of the semester with live music and food trucks.",
        category = EventCategory.MUSIC,
        isPaid = true,
        price = "K5000.00",
        isSaved = true,
        isPurchased = true,
        posterUrl = "poster_1"
    ),
    Event(
        id = "2",
        title = "Tech Careers Fair",
        day = "16",
        month = "OCT",
        time = "10:00 AM",
        location = "Computer Lab1",
        description = "Connect with top tech employers and alumni recruiters.",
        category = EventCategory.CAREER,
        isPaid = false,
        price = "",
        isSaved = true,
        isPurchased = false,
        posterUrl = "poster_2"
    ),
    Event(
        id = "3",
        title = "UNIMA Football Finals",
        day = "18",
        month = "OCT",
        time = "4:30 PM",
        location = "Sports Complex",
        description = "Come support your hall in the championship match!",
        category = EventCategory.SPORTS,
        isPaid = true,
        price = "K2000.00",
        isSaved = false,
        isPurchased = true,
        posterUrl = "poster_3"
    ),
    Event(
        id = "4",
        title = "Poetry & Spoken Word Night",
        day = "21",
        month = "OCT",
        time = "6:00 PM",
        location = "Room A",
        description = "An evening of powerful performances and acoustic interludes.",
        category = EventCategory.COMMUNITY,
        isPaid = false,
        price = "",
        isSaved = false,
        isPurchased = false,
        posterUrl = "poster_4"
    )
)

// ==========================================
// 2. MAIN HOME SCREEN COMPOSABLE
// ==========================================

@Composable
fun HomeScreen(
    currentScreen: Int = 0,
    onNavigate: (Int) -> Unit = {},
    events: List<Event> = sampleEvents,
    onEventClick: (Event) -> Unit = {},
    onToggleSave: (Event) -> Unit = {}
) {
    // State for tracking the selected filter chip
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("All", "This week", "Music", "Sports", "Career", "Free")

    // Filter events based on selection and real-time search query matching title, location, or description
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

    // Scaffold provides the structural layout of the screen with a bottom bar
    Scaffold(
        containerColor = CampusCream,
        bottomBar = {
            CampusBottomBar4(
                selectedIndex = currentScreen,
                onItemSelected = onNavigate
            )
        }
    ) { innerPadding ->
        // Main vertical column for the screen contents
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Section with Navy Background and Interactive Search Bar
            CampusHeader(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips Row (Horizontally scrollable)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    FilterChipItem(
                        text = filter,
                        isSelected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section Title: "Upcoming events"
            Text(
                text = "Upcoming events",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = CampusNavy,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Vertically scrollable list of event ticket cards
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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

// ==========================================
// 3. HEADER & INTERACTIVE SEARCH BAR
// ==========================================

@Composable
fun CampusHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    // Header container with Navy background and rounded bottom corners
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(CampusNavy)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // App Name in bold serif
            Text(
                text = "CampusConnect",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = CampusCream
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle
            Text(
                text = "What's happening on campus",
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                color = CampusSubtitleBlue
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Interactive Search Bar TextField
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search icon",
                    tint = Color(0xFFD0D7E2),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search events, clubs, venues...",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 14.sp,
                            color = Color(0xFFD0D7E2).copy(alpha = 0.7f)
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    )
                }
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = Color(0xFFD0D7E2),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. FILTER CHIP COMPOSABLE
// ==========================================

@Composable
fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) CampusNavy else CampusLightBeige
    val textColor = if (isSelected) CampusCream else CampusGrey

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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

// ==========================================
// 5. EVENT CARD (TICKET STUB STYLE)
// ==========================================

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit = {},
    onToggleSave: () -> Unit = {}
) {
    // Ticket stub card with white background, rounded corners, and subtle shadow
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
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
                    // Day Number (Large bold serif, Cream)
                    Text(
                        text = event.day,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = CampusCream
                    )
                    // Month Abbreviation (Small, Amber or contrasting)
                    Text(
                        text = event.month,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (event.category == EventCategory.MUSIC) CampusNavy else CampusAmber
                    )
                }

                // Small circular white notch cut into the right edge of the date block
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .align(Alignment.CenterEnd)
                        .offset(x = 8.dp)
                )
            }

            // Right Side: Event Details
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
                        // Event Title (Bold Serif)
                        Text(
                            text = event.title,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CampusInk
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Time and Location separated by a dot
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

                Spacer(modifier = Modifier.height(10.dp))

                // Price/Free Tag
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

// ==========================================
// 7. PREVIEW
// ==========================================

@Preview(showBackground = true, widthDp = 392, heightDp = 800)
@Composable
fun HomeScreenPreview() {
    CompusConnectTheme {
        HomeScreen()
    }
}
