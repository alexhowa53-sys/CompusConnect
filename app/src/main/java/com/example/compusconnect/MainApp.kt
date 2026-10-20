package com.example.compusconnect

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object MainStudent : Screen()
    object EventDetails : Screen()
    object TicketConfirmation : Screen()
    object AdminDashboard : Screen()
    object CreateEvent : Screen()
}

@Composable
fun MainApp() {
    var currentRoute by remember { mutableStateOf<Screen>(Screen.Splash) }
    var studentTab by remember { mutableStateOf(0) } // 0: Home, 1: Search, 2: Tickets, 3: Profile
    var userSession by remember { mutableStateOf(UserSession()) }
    val repository = remember { CampusRepository() }

    // Hoisted shared events state across Home, Search, Details, and Tickets
    var events by remember {
        mutableStateOf(
            listOf(
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
                    isSaved = true,
                    isPurchased = false
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
                    price = "",
                    isSaved = false,
                    isPurchased = false
                )
            )
        )
    }

    // Fetch events from Firebase Firestore on startup
    LaunchedEffect(Unit) {
        val remoteEvents = repository.fetchEvents()
        if (remoteEvents.isNotEmpty()) {
            events = remoteEvents
        }
    }

    var selectedEventId by remember { mutableStateOf("1") }
    val selectedEvent = events.find { it.id == selectedEventId } ?: events.first()

    val onToggleSave: (Event) -> Unit = { eventToToggle ->
        events = events.map { ev ->
            if (ev.id == eventToToggle.id) ev.copy(isSaved = !ev.isSaved) else ev
        }
    }

    val onBuyTicket: (Event) -> Unit = { eventToBuy ->
        events = events.map { ev ->
            if (ev.id == eventToBuy.id) ev.copy(isPurchased = true, isSaved = true) else ev
        }
        selectedEventId = eventToBuy.id
        currentRoute = Screen.TicketConfirmation
    }

    Crossfade(targetState = currentRoute, label = "ScreenTransition") { route ->
        when (route) {
            is Screen.Splash -> {
                SplashScreen(
                    onSplashFinished = {
                        currentRoute = Screen.MainStudent
                    }
                )
            }
            is Screen.Login -> {
                LoginAuthScreen(
                    onLoginSuccess = { session ->
                        userSession = session
                        if (session.role == UserRole.ADMIN) {
                            currentRoute = Screen.AdminDashboard
                        } else {
                            currentRoute = Screen.MainStudent
                            studentTab = 0
                        }
                    },
                    onBackClick = { currentRoute = Screen.MainStudent }
                )
            }
            is Screen.MainStudent -> {
                Crossfade(targetState = studentTab, label = "TabTransition") { tab ->
                    when (tab) {
                        0 -> HomeScreen(
                            currentScreen = studentTab,
                            onNavigate = { studentTab = it },
                            events = events,
                            onEventClick = { event ->
                                selectedEventId = event.id
                                currentRoute = Screen.EventDetails
                            },
                            onToggleSave = onToggleSave
                        )
                        1 -> SearchScreen(
                            currentScreen = studentTab,
                            onNavigate = { studentTab = it },
                            events = events,
                            onEventClick = { event ->
                                selectedEventId = event.id
                                currentRoute = Screen.EventDetails
                            },
                            onToggleSave = onToggleSave
                        )
                        2 -> TicketsScreen(
                            currentScreen = studentTab,
                            onNavigate = { studentTab = it },
                            events = events,
                            onEventClick = { event ->
                                selectedEventId = event.id
                                currentRoute = Screen.EventDetails
                            },
                            onToggleSave = onToggleSave
                        )
                        3 -> ProfileScreen(
                            currentScreen = studentTab,
                            userSession = userSession,
                            onNavigate = { studentTab = it },
                            onLoginClick = { currentRoute = Screen.Login },
                            onSignUpClick = { currentRoute = Screen.Login },
                            onSwitchToAdminView = {
                                currentRoute = Screen.AdminDashboard
                            },
                            onLogout = {
                                userSession = UserSession()
                                currentRoute = Screen.MainStudent
                                studentTab = 0
                            }
                        )
                    }
                }
            }
            is Screen.EventDetails -> {
                EventDetailsScreen(
                    event = selectedEvent,
                    onBackClick = { currentRoute = Screen.MainStudent },
                    onToggleSave = { onToggleSave(selectedEvent) },
                    onBuyTicketClick = {
                        onBuyTicket(selectedEvent)
                    }
                )
            }
            is Screen.TicketConfirmation -> {
                TicketConfirmationScreen(
                    event = selectedEvent,
                    onViewMyTicketsClick = {
                        currentRoute = Screen.MainStudent
                        studentTab = 2 // Tickets tab
                    },
                    onBackToHomeClick = {
                        currentRoute = Screen.MainStudent
                        studentTab = 0 // Home tab
                    }
                )
            }
            is Screen.AdminDashboard -> {
                AdminDashboardScreen(
                    events = events,
                    onCreateEventClick = {
                        currentRoute = Screen.CreateEvent
                    },
                    onSwitchToStudentView = {
                        currentRoute = Screen.MainStudent
                        studentTab = 0
                    },
                    onDeleteEvent = { eventToDelete ->
                        events = events.filter { it.id != eventToDelete.id }
                        CoroutineScope(Dispatchers.IO).launch {
                            repository.deleteEvent(eventToDelete.id)
                        }
                    }
                )
            }
            is Screen.CreateEvent -> {
                CreateEventScreen(
                    onBackClick = { currentRoute = Screen.AdminDashboard },
                    onPublishEvent = { newEvent ->
                        events = listOf(newEvent) + events
                        currentRoute = Screen.AdminDashboard
                        CoroutineScope(Dispatchers.IO).launch {
                            repository.addEvent(newEvent)
                        }
                    }
                )
            }
        }
    }
}
