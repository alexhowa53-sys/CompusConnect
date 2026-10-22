package com.example.compusconnect

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object MainStudent : Screen()
    object EventDetails : Screen()
    object TicketConfirmation : Screen()
    object AdminDashboard : Screen()
    object CreateEvent : Screen()
    object Notifications : Screen()
}

@Composable
fun MainApp() {
    var currentRoute by remember { mutableStateOf<Screen>(Screen.Splash) }
    var studentTab by remember { mutableStateOf(0) } // 0: Home, 1: Search, 2: Tickets, 3: Profile
    var userSession by remember { mutableStateOf(UserSession()) }
    var eventBeingEdited by remember { mutableStateOf<Event?>(null) }
    var lastPurchasedRefs by remember { mutableStateOf(listOf("CC-TKT-8849")) }
    val repository = remember { CampusRepository() }
    val context = LocalContext.current

    // Request notification permission on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

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
                    isSaved = false,
                    isPurchased = false
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
                    isSaved = false,
                    isPurchased = false
                )
            )
        )
    }

    // Unread notification count state
    var unreadNotificationCount by remember { mutableStateOf(0) }

    // Fetch events and user-specific saved/purchased tickets from Firestore on startup or login
    LaunchedEffect(userSession.uid) {
        val rememberMe = CampusPreferences.getRememberMe(context).first()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (userSession.uid.isBlank() && currentUser != null && rememberMe) {
            val role = repository.fetchUserRole(currentUser.uid)
            userSession = UserSession(
                isLoggedIn = true,
                uid = currentUser.uid,
                name = currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "User",
                email = currentUser.email ?: "",
                role = role
            )
            FirebaseMessaging.getInstance().subscribeToTopic("students")
            if (role == UserRole.ADMIN) {
                currentRoute = Screen.AdminDashboard
            }
        } else if (!rememberMe && currentUser != null && userSession.uid.isBlank()) {
            FirebaseAuth.getInstance().signOut()
        }

        val remoteEvents = repository.fetchEvents()
        if (remoteEvents.isNotEmpty()) {
            events = remoteEvents
        }

        if (userSession.isLoggedIn && userSession.uid.isNotBlank()) {
            val savedIds = repository.fetchUserSavedEventIds(userSession.uid)
            val ticketIds = repository.fetchUserTicketEventIds(userSession.uid)
            events = events.map { ev ->
                ev.copy(
                    isSaved = savedIds.contains(ev.id),
                    isPurchased = ticketIds.contains(ev.id)
                )
            }

            // Calculate unread notifications count
            val notifs = repository.fetchNotifications(userSession.uid)
            unreadNotificationCount = notifs.count { !it.isRead }
        }
    }

    var selectedEventId by remember { mutableStateOf("1") }
    val selectedEvent = events.find { it.id == selectedEventId } ?: events.first()

    val onToggleSave: (Event) -> Unit = { eventToToggle ->
        val newIsSaved = !eventToToggle.isSaved
        events = events.map { ev ->
            if (ev.id == eventToToggle.id) ev.copy(isSaved = newIsSaved) else ev
        }
        if (userSession.isLoggedIn && userSession.uid.isNotBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                if (newIsSaved) {
                    repository.saveEvent(userSession.uid, eventToToggle)
                } else {
                    repository.unsaveEvent(userSession.uid, eventToToggle.id)
                }
            }
        }
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
                        FirebaseMessaging.getInstance().subscribeToTopic("students")
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
                            onToggleSave = onToggleSave,
                            unreadNotificationCount = unreadNotificationCount,
                            isLoggedIn = userSession.isLoggedIn,
                            onNotificationClick = {
                                if (userSession.isLoggedIn) {
                                    currentRoute = Screen.Notifications
                                } else {
                                    currentRoute = Screen.Login
                                }
                            },
                            onLoginPrompt = {
                                currentRoute = Screen.Login
                            }
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
                                FirebaseMessaging.getInstance().unsubscribeFromTopic("students")
                                FirebaseAuth.getInstance().signOut()
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
                    userSession = userSession,
                    onBackClick = { currentRoute = Screen.MainStudent },
                    onToggleSave = { onToggleSave(selectedEvent) },
                    onRequireLogin = { currentRoute = Screen.Login },
                    onTicketPurchased = { refs, qty ->
                        lastPurchasedRefs = refs
                        events = events.map { ev ->
                            if (ev.id == selectedEvent.id) ev.copy(isPurchased = true, isSaved = true) else ev
                        }
                        currentRoute = Screen.TicketConfirmation
                        if (userSession.isLoggedIn && userSession.uid.isNotBlank()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                repository.saveEvent(userSession.uid, selectedEvent)
                            }
                        }
                    }
                )
            }
            is Screen.TicketConfirmation -> {
                TicketConfirmationScreen(
                    event = selectedEvent,
                    ticketRefs = lastPurchasedRefs,
                    holderName = if (userSession.name.isNotBlank()) userSession.name else "Campus Student",
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
            is Screen.Notifications -> {
                NotificationsScreen(
                    userSession = userSession,
                    onBackClick = { currentRoute = Screen.MainStudent },
                    onEventClick = { eventId ->
                        selectedEventId = eventId
                        currentRoute = Screen.EventDetails
                    }
                )
            }
            is Screen.AdminDashboard -> {
                AdminDashboardScreen(
                    userSession = userSession,
                    events = events,
                    onCreateEventClick = {
                        eventBeingEdited = null
                        currentRoute = Screen.CreateEvent
                    },
                    onEditEventClick = { eventToEdit ->
                        eventBeingEdited = eventToEdit
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
                    eventToEdit = eventBeingEdited,
                    onBackClick = { currentRoute = Screen.AdminDashboard },
                    onPublishEvent = { newEvent ->
                        CoroutineScope(Dispatchers.IO).launch {
                            var finalPosterUrl = newEvent.posterUrl
                            if (finalPosterUrl.startsWith("content://")) {
                                val uploadResult = repository.uploadPosterImage(Uri.parse(finalPosterUrl))
                                uploadResult.onSuccess { downloadUrl ->
                                    finalPosterUrl = downloadUrl
                                }
                            }
                            val eventToSave = newEvent.copy(posterUrl = finalPosterUrl)
                            events = listOf(eventToSave) + events
                            repository.addEvent(eventToSave, userSession.uid)
                        }
                        currentRoute = Screen.AdminDashboard
                    },
                    onUpdateEvent = { updatedEvent ->
                        CoroutineScope(Dispatchers.IO).launch {
                            var finalPosterUrl = updatedEvent.posterUrl
                            if (finalPosterUrl.startsWith("content://")) {
                                val uploadResult = repository.uploadPosterImage(Uri.parse(finalPosterUrl))
                                uploadResult.onSuccess { downloadUrl ->
                                    finalPosterUrl = downloadUrl
                                }
                            }
                            val eventToUpdate = updatedEvent.copy(posterUrl = finalPosterUrl)
                            events = events.map { if (it.id == eventToUpdate.id) eventToUpdate else it }
                            repository.updateEvent(eventToUpdate)
                        }
                        currentRoute = Screen.AdminDashboard
                    }
                )
            }
        }
    }
}
