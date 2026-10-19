package com.example.compusconnect

// ---------------------------------------------------------------------
// TEMPORARY login check, used until Firebase Authentication is wired up.
// Replace the body of this function later with a real Firebase sign-in
// call that looks up the user's role from your Firestore "users" collection.
// ---------------------------------------------------------------------

/**
 * Checks the given credentials and returns a UserSession describing who
 * just logged in, or null if the credentials were invalid/empty.
 *
 * Admin check is hardcoded for now — swap the email/password for your
 * own test admin account, and change this to a real backend check later.
 */
fun attemptLogin(email: String, password: String): UserSession? {
    val trimmedEmail = email.trim()

    if (trimmedEmail.isBlank() || password.isBlank()) {
        return null // invalid login — caller should show an error message
    }

    val isAdmin = trimmedEmail.equals("admin@compusconnect.com", ignoreCase = true) &&
            password == "admin123"

    return if (isAdmin) {
        UserSession(
            isLoggedIn = true,
            name = "Campus Admin",
            email = trimmedEmail,
            role = UserRole.ADMIN
        )
    } else {
        // Any other valid-looking email/password combo is treated as a student
        // for now. Later, this should check against real registered accounts.
        UserSession(
            isLoggedIn = true,
            name = trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
            email = trimmedEmail,
            role = UserRole.STUDENT
        )
    }
}
