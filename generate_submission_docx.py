import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH

doc = docx.Document()

# Page margins
for section in doc.sections:
    section.top_margin = Inches(1)
    section.bottom_margin = Inches(1)
    section.left_margin = Inches(1)
    section.right_margin = Inches(1)

# Title
p_title = doc.add_paragraph()
r_title = p_title.add_run("TEST CASE DESIGN SUBMISSION")
r_title.font.name = "Calibri"
r_title.font.size = Pt(22)
r_title.font.bold = True
r_title.font.color.rgb = RGBColor(19, 42, 76)
p_title.alignment = WD_ALIGN_PARAGRAPH.CENTER

p_sub = doc.add_paragraph()
r_sub = p_sub.add_run("Mobile Applications Development Project\n4th Year – Test Design Submission")
r_sub.font.name = "Calibri"
r_sub.font.size = Pt(13)
r_sub.font.italic = True
r_sub.font.color.rgb = RGBColor(118, 114, 104)
p_sub.alignment = WD_ALIGN_PARAGRAPH.CENTER

doc.add_paragraph()

# Project Information Section
p_sec1 = doc.add_paragraph()
r_sec1 = p_sec1.add_run("1. Project Information")
r_sec1.font.name = "Calibri"
r_sec1.font.size = Pt(16)
r_sec1.font.bold = True
r_sec1.font.color.rgb = RGBColor(19, 42, 76)

proj_info = [
    ("Student Name", "Alex Howa"),
    ("Student ID", "BED/COM/10/23"),
    ("Application Name", "CampusConnect"),
    ("Target Platform", "Android 14"),
    ("Date", "15/10/25"),
    ("Template Version", "v1.0")
]

table_info = doc.add_table(rows=len(proj_info) + 1, cols=2)
table_info.style = 'Table Grid'
hdr_cells = table_info.rows[0].cells
hdr_cells[0].text = "Field"
hdr_cells[1].text = "Details"
for cell in hdr_cells:
    for paragraph in cell.paragraphs:
        for run in paragraph.runs:
            run.font.bold = True
            run.font.color.rgb = RGBColor(19, 42, 76)

for i, (field, detail) in enumerate(proj_info):
    row_cells = table_info.rows[i + 1].cells
    row_cells[0].text = field
    row_cells[1].text = detail

doc.add_paragraph()

# Test Cases Section
p_sec2 = doc.add_paragraph()
r_sec2 = p_sec2.add_run("2. Test Cases")
r_sec2.font.name = "Calibri"
r_sec2.font.size = Pt(16)
r_sec2.font.bold = True
r_sec2.font.color.rgb = RGBColor(19, 42, 76)

test_cases = [
    {
        "id": "AUTH_LOGIN_001",
        "desc": "Verify that a registered student can log in successfully with valid credentials and role verification from Firestore.",
        "type": "Positive",
        "priority": "High",
        "prereq": "App installed; student account registered with role 'student'.",
        "data": "Email: student@university.edu; Password: securePassword123",
        "steps": "1. Launch the application.\n2. Tap Profile tab and select 'Log in'.\n3. Enter registered email and correct password.\n4. Tap the 'Sign in' button.",
        "expected": "User is authenticated via Firebase Auth, role is verified as student, and app navigates to the student Home screen.",
        "post": "A secure user session is created and the student remains logged in."
    },
    {
        "id": "AUTH_LOGIN_002",
        "desc": "Verify that login fails when a registered email is entered with an incorrect password.",
        "type": "Negative",
        "priority": "High",
        "prereq": "App installed; student account registered.",
        "data": "Email: student@university.edu; Password: wrongPassword",
        "steps": "1. Launch the application and open Login screen.\n2. Enter registered email and an incorrect password.\n3. Tap 'Sign in'.",
        "expected": "Error message 'Incorrect password. Please try again.' is displayed; user remains on the Login screen.",
        "post": "No session is created; password field is cleared."
    },
    {
        "id": "AUTH_LOGIN_003",
        "desc": "Verify that login fails when attempting to log in with an unregistered email address.",
        "type": "Negative",
        "priority": "High",
        "prereq": "App installed; email is not registered in Firebase Auth.",
        "data": "Email: unknown@university.edu; Password: securePassword123",
        "steps": "1. Launch the application and open Login screen.\n2. Enter an unregistered email and any password.\n3. Tap 'Sign in'.",
        "expected": "Error message 'No account found with this email. Please sign up first.' is shown; user remains on Login screen.",
        "post": "No session is created."
    },
    {
        "id": "AUTH_SIGNUP_001",
        "desc": "Verify that a new student can successfully sign up and complete OTP email verification.",
        "type": "Positive",
        "priority": "High",
        "prereq": "App installed; user is on Login/Sign up screen.",
        "data": "Name: Alex Howa; Email: alex.howa@university.edu; Password: securePassword123",
        "steps": "1. Open app and switch to the 'Sign up' tab.\n2. Enter Name, Email, and Password.\n3. Tap 'Sign up' and enter the 4-digit OTP verification code.\n4. Tap 'Continue'.",
        "expected": "Account is created in Firebase Auth, Firestore user doc is provisioned with role 'student', and user navigates to Home.",
        "post": "New user session is established and active."
    },
    {
        "id": "AUTH_SIGNUP_002",
        "desc": "Verify that sign up fails when attempting to register with an email address already in use.",
        "type": "Negative",
        "priority": "High",
        "prereq": "App installed; email is already registered in the system.",
        "data": "Name: Alex Howa; Email: existing@university.edu; Password: securePassword123",
        "steps": "1. Switch to 'Sign up' tab.\n2. Enter an already registered email and password.\n3. Complete OTP step and tap 'Continue'.",
        "expected": "Error message 'This email is already registered. Please log in instead.' is displayed.",
        "post": "No new account is created."
    },
    {
        "id": "AUTH_RESET_001",
        "desc": "Verify that requesting a password reset sends a reset email and initiates a 60-second cooldown timer.",
        "type": "Positive",
        "priority": "Medium",
        "prereq": "App installed; user is on Login screen.",
        "data": "Email: student@university.edu",
        "steps": "1. Open Login screen and tap 'Forgot password?'.\n2. Enter account email and tap 'Send Reset Link'.",
        "expected": "Success message 'If an account exists for this email, we have sent a reset link...' appears and button enters 60s cooldown.",
        "post": "Password reset email dispatched via Firebase Auth."
    },
    {
        "id": "EVENT_SEARCH_001",
        "desc": "Verify that typing keywords in the search bar filters upcoming campus events in real-time.",
        "type": "Positive",
        "priority": "High",
        "prereq": "App installed; user is on Home or Search screen with multiple events loaded.",
        "data": "Search Query: 'Tech'",
        "steps": "1. Navigate to Search screen.\n2. Tap the search bar and type 'Tech'.",
        "expected": "Event feed instantly filters to display matching events (e.g. 'Tech Careers Fair').",
        "post": "Only matching events are displayed."
    },
    {
        "id": "EVENT_SEARCH_002",
        "desc": "Verify that searching for a non-existent keyword displays an appropriate empty state.",
        "type": "Negative",
        "priority": "Low",
        "prereq": "App installed; user is on Search screen.",
        "data": "Search Query: 'NonExistentEventXYZ'",
        "steps": "1. Enter a search query that matches no events.",
        "expected": "System displays friendly empty state 'No events match your search'.",
        "post": "Event list remains empty."
    },
    {
        "id": "EVENT_FILTER_001",
        "desc": "Verify that selecting category chips filters the event feed correctly.",
        "type": "Positive",
        "priority": "Medium",
        "prereq": "App installed; user is on Home screen.",
        "data": "Category Chip: 'Music'",
        "steps": "1. Tap on the 'Music' category filter chip.",
        "expected": "Event feed updates to display only events categorized under Music.",
        "post": "Filtered list is rendered."
    },
    {
        "id": "TKT_BUY_001",
        "desc": "Verify that a logged-in student can open the purchase confirmation dialog, select ticket quantity, and successfully buy tickets.",
        "type": "Positive",
        "priority": "High",
        "prereq": "App installed; user is logged in as student; viewing a paid event details page.",
        "data": "Quantity: 2 tickets",
        "steps": "1. Tap 'Buy Ticket' on a paid event.\n2. Select quantity using the stepper (+/-).\n3. Tap 'Confirm purchase'.",
        "expected": "Transaction executes in Firestore, ticket counts and sales update, and app navigates to Ticket Confirmation screen showing user's first name and reference codes.",
        "post": "Tickets are recorded in user's purchased tickets collection."
    },
    {
        "id": "TKT_BUY_002",
        "desc": "Verify that ticket purchases are strictly blocked when attempting to exceed the maximum limit of 3 tickets per event.",
        "type": "Negative",
        "priority": "High",
        "prereq": "App installed; user is logged in; user already holds 3 tickets for the target event.",
        "data": "Target Event: Welcoming Function (already holding 3 tickets)",
        "steps": "1. Open the event details page.\n2. Tap 'Buy Ticket'.",
        "expected": "Button displays 'Limit Reached' and confirmation dialog shows 'You have reached the limit of 3 tickets for this event.'",
        "post": "No additional tickets are purchased."
    },
    {
        "id": "TKT_BUY_003",
        "desc": "Verify that guest (logged-out) users cannot buy tickets and are redirected to login.",
        "type": "Negative",
        "priority": "High",
        "prereq": "App installed; user is logged out.",
        "data": "None",
        "steps": "1. Browse events while logged out.\n2. Open an event details page and tap 'Buy Ticket'.",
        "expected": "System intercepts request and navigates user directly to the Login screen.",
        "post": "User remains on Login screen."
    },
    {
        "id": "ADMIN_CREATE_001",
        "desc": "Verify that an administrator can publish a new campus event with poster artwork and details.",
        "type": "Positive",
        "priority": "High",
        "prereq": "App installed; logged in as admin.",
        "data": "Title: Spring Fest; Location: Great Hall; Price: K5000.00",
        "steps": "1. Open Admin Dashboard and tap 'Create a new event'.\n2. Fill in event title, date, location, pricing, and poster URL.\n3. Tap 'Publish Event'.",
        "expected": "New event document is created in Firestore with ownerUid, and event appears on student feeds instantly.",
        "post": "Event is live in the system."
    },
    {
        "id": "ADMIN_EDIT_001",
        "desc": "Verify that an administrator can edit the details of an event they created.",
        "type": "Positive",
        "priority": "Medium",
        "prereq": "App installed; logged in as admin; at least one event created by admin exists.",
        "data": "Updated Title: Spring Music Fest 2025",
        "steps": "1. On Admin Dashboard, tap Edit icon on an event card.\n2. Modify event title and tap 'Save Changes'.",
        "expected": "Event details are updated in Firestore and reflect instantly on dashboard and student feeds.",
        "post": "Event record is updated."
    },
    {
        "id": "ADMIN_DELETE_001",
        "desc": "Verify that an administrator can delete an event from the dashboard.",
        "type": "Positive",
        "priority": "Medium",
        "prereq": "App installed; logged in as admin.",
        "data": "Target Event: Sample Event",
        "steps": "1. On Admin Dashboard, tap Delete (trash) icon on an event card.",
        "expected": "Event document is deleted from Firestore and removed from admin and student feeds.",
        "post": "Event is removed."
    },
    {
        "id": "NOTIF_BELL_001",
        "desc": "Verify that unread notifications display a live red dot badge with the correct unread count on the header bell icon.",
        "type": "Positive",
        "priority": "Medium",
        "prereq": "App installed; logged in as student; new broadcast notification exists.",
        "data": "1 Unread Broadcast Notification",
        "steps": "1. Observe header bell icon on Home screen.",
        "expected": "Red dot badge appears over the bell icon showing unread count ('1').",
        "post": "Badge is visible."
    }
]

for tc in test_cases:
    p_id = doc.add_paragraph()
    r_id = p_id.add_run(f"Test Case ID: {tc['id']}")
    r_id.font.name = "Calibri"
    r_id.font.size = Pt(12)
    r_id.font.bold = True
    r_id.font.color.rgb = RGBColor(19, 42, 76)

    table_tc = doc.add_table(rows=9, cols=2)
    table_tc.style = 'Table Grid'

    fields = [
        ("Description", tc["desc"]),
        ("Test Type", tc["type"]),
        ("Priority", tc["priority"]),
        ("Prerequisites", tc["prereq"]),
        ("Test Data", tc["data"]),
        ("Test Steps", tc["steps"]),
        ("Expected Result", tc["expected"]),
        ("Post-conditions", tc["post"]),
        ("Status", "")
    ]

    for row_idx, (f_name, f_val) in enumerate(fields):
        row = table_tc.rows[row_idx]
        row.cells[0].text = f_name
        row.cells[1].text = f_val
        row.cells[0].paragraphs[0].runs[0].font.bold = True
        row.cells[0].paragraphs[0].runs[0].font.color.rgb = RGBColor(19, 42, 76)

    doc.add_paragraph()

doc.save("CampusConnect_Test_Cases_Alex_Howa.docx")
print("Successfully generated CampusConnect_Test_Cases_Alex_Howa.docx")
