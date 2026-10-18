
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compusconnect.ui.theme.CompusConnectTheme
import kotlinx.coroutines.launch
import org.json.JSONObject

// ==========================================
// 1. DESIGN SYSTEM COLORS
// ==========================================

val CampusNavy = Color(0xFF132A4C)
val CampusAmber = Color(0xFFF2A93B)
val CampusSage = Color(0xFFCFE0D6)
val CampusSageText = Color(0xFF2F5A44)
val CampusCream = Color(0xFFFAF7F0)
val CampusInk = Color(0xFF1C1C1C)
val CampusGrey = Color(0xFF767268)
val CampusLightBeige = Color(0xFFEFECE6)
val CampusSubtitleBlue = Color(0xFFA3B5CD)

// ==========================================
// 2. EVENT CATEGORY AND MODEL
// ==========================================

enum class EventCategory(val color: Color, val label: String) {
    MUSIC(Color(0xFFF2A93B), "MUSIC"),
    SPORTS(Color(0xFF132A4C), "SPORTS"),
    CAREER(Color(0xFF2F5A44), "CAREER"),
    COMMUNITY(Color(0xFF2F5A44), "COMMUNITY")
}

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
    val isPurchased: Boolean = false
)

// ==========================================
// 3. CONVERT MYSQL JSON INTO EVENT OBJECTS
// ==========================================

fun parseDatabaseEvents(response: JSONObject): List<Event> {
    if (!response.optBoolean("success")) {
        throw IllegalStateException(
            response.optString(
                "message",
                "Could not load events."
            )
        )
    }

    val eventArray = response.optJSONArray("events")
        ?: throw IllegalStateException("The events list is missing.")

    return List(eventArray.length()) { index ->
        val item = eventArray.getJSONObject(index)

        val categoryText = item.optString(
            "category",
            "COMMUNITY"
        ).uppercase()

        val category = when {
            categoryText.contains("MUSIC") ->
                EventCategory.MUSIC

            categoryText.contains("SPORT") ->
                EventCategory.SPORTS

            categoryText.contains("CAREER") ||
                    categoryText.contains("TECH") ->
                EventCategory.CAREER

            else -> EventCategory.COMMUNITY
        }

        val paidText = item.optString("is_paid", "0")
            .lowercase()

        val isPaid = paidText in listOf("1", "true", "yes")

        val rawPrice = item.optString("price", "")
            .takeUnless { it == "null" }
            .orEmpty()

        val price = when {
            rawPrice.isBlank() ||
                    rawPrice == "0" ||
                    rawPrice == "0.00" -> ""

            rawPrice.startsWith("K", ignoreCase = true) ->
                rawPrice

            else -> "K$rawPrice"
        }

        Event(
            id = item.optString("id"),
            title = item.optString("title"),
            day = item.optString("day"),
            month = item.optString("month").uppercase(),
            time = item.optString("time"),
            location = item.optString("location"),
            description = item.optString("description"),
            category = category,
            isPaid = isPaid,
            price = price
        )
    }
}

// ==========================================
// 4. SAMPLE EVENTS FOR PREVIEW
// ==========================================

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
        isPurchased = true
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
        isSaved = true
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
        isPurchased = true
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
        price = ""
    )
)

// ==========================================
// 5. MAIN HOME SCREEN
// ==========================================

@Composable
fun HomeScreen(
    currentScreen: Int = 0,
    onNavigate: (Int) -> Unit = {},
    events: List<Event> = sampleEvents,
    onEventClick: (Event) -> Unit = {},
    onToggleSave: (Event) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    // MySQL events and loading state
    var databaseEvents by remember {
        mutableStateOf<List<Event>?>(null)
    }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }
    var retryKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(retryKey) {
        isLoading = true
        errorMessage = null

        try {
            val response = ApiClient.getEvents()
            databaseEvents = parseDatabaseEvents(response)
        } catch (e: Exception) {
            errorMessage = e.message ?: "Could not load events."
        } finally {
            isLoading = false
        }
    }

    // Use MySQL events when available.
    // Keep the passed-in events as a fallback if loading fails.
    val displayedEvents = databaseEvents ?: events

    val filters = listOf(
        "All", "This week", "Music", "Sports", "Career", "Free"
    )

    val filteredEvents = remember(
        displayedEvents,
        selectedFilter,
        searchQuery
    ) {
        val query = searchQuery.trim().lowercase()

        displayedEvents.filter { event ->
            val matchesCategory = when (selectedFilter) {
                "Free" -> !event.isPaid
                "Music" -> event.category == EventCategory.MUSIC
                "Sports" -> event.category == EventCategory.SPORTS
                "Career" ->
                    event.category == EventCategory.CAREER ||
                            event.category == EventCategory.COMMUNITY

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
        ) {
            CampusHeader(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Text(
                text = "Upcoming events",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = CampusNavy,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    horizontal = 20.dp,
                    vertical = 4.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isLoading && databaseEvents == null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = CampusAmber
                            )
                        }
                    }
                } else if (errorMessage != null &&
                    databaseEvents == null
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = errorMessage
                                    ?: "Could not load events.",
                                color = CampusGrey
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Button(
                                onClick = { retryKey++ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CampusNavy
                                )
                            ) {
                                Text("Try again")
                            }
                        }
                    }
                } else if (filteredEvents.isEmpty()) {
                    item {
                        Text(
                            text = "No events found.",
                            color = CampusGrey,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                } else {
                    items(
                        items = filteredEvents,
                        key = { event -> event.id }
                    ) { event ->
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

// ==========================================
// 6. HEADER AND SEARCH BAR
// ==========================================

@Composable
fun CampusHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = 28.dp,
                    bottomEnd = 28.dp
                )
            )
            .background(CampusNavy)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "CampusConnect",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = CampusCream
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "What's happening on campus",
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                color = CampusSubtitleBlue
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Icon(
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
                        androidx.compose.material3.Icon(
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
// 7. FILTER CHIP
// ==========================================

@Composable
fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor =
        if (isSelected) CampusNavy else CampusLightBeige
    val textColor =
        if (isSelected) CampusCream else CampusGrey

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontFamily = FontFamily.SansSerif,
                fontWeight = if (isSelected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                },
                fontSize = 13.sp,
                color = textColor
            )
        }
    }
}

// ==========================================
// 8. EVENT CARD
// ==========================================

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit = {},
    onToggleSave: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                        color = if (
                            event.category == EventCategory.MUSIC
                        ) CampusNavy else CampusAmber
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

                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier.size(32.dp)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = if (event.isSaved) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = "Save event",
                            tint = if (event.isSaved) {
                                Color(0xFF6650a4)
                            } else {
                                CampusGrey
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (event.isPaid) CampusAmber else CampusSage
                        )
                        .padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        )
                ) {
                    Text(
                        text = if (event.isPaid) {
                            "Paid · ${event.price}"
                        } else {
                            "Free"
                        },
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = if (event.isPaid) {
                            CampusInk
                        } else {
                            CampusSageText
                        }
                    )
                }
            }
        }
    }
}

// ==========================================
// 9. PREVIEW
// ==========================================

@Preview(showBackground = true, widthDp = 392, heightDp = 800)
@Composable
fun HomeScreenPreview() {
    CompusConnectTheme {
        HomeScreen()
    }
}
