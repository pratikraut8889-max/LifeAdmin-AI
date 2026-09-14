package com.example.data.model

enum class ItemType(val label: String) {
    TASK("Task"),
    BILL("Bill Payment"),
    APPOINTMENT("Appointment"),
    RENEWAL("Renewal"),
    PURCHASE_ADMIN("Purchase & Return"),
    TRAVEL("Travel & Trip"),
    DOCUMENT_ACTION("Document Action"),
    APPLICATION("Application"),
    WAITING_FOR("Waiting For"),
    REMINDER("Reminder"),
    FOLLOW_UP("Follow-up")
}

enum class ItemCategory(val label: String, val iconName: String) {
    PERSONAL("Personal Admin", "person"),
    BILLS("Bills & Dues", "receipt"),
    HEALTH("Health & Medical", "local_hospital"),
    TRAVEL("Travel & Bookings", "flight"),
    SHOPPING("Purchases & Orders", "shopping_cart"),
    WORK("Work & Career", "work"),
    SCHOOL("Education & Exams", "school"),
    VEHICLE("Vehicle & Transport", "directions_car"),
    GOVERNMENT("Government & Legal", "gavel"),
    HOME("Home & Property", "home")
}

enum class ItemPriority(val label: String) {
    HIGH("High Priority"),
    MEDIUM("Medium"),
    LOW("Low")
}

enum class DeadlineGroup(val label: String) {
    OVERDUE("Overdue"),
    TODAY("Today"),
    TOMORROW("Tomorrow"),
    THIS_WEEK("This Week"),
    UPCOMING("Upcoming")
}

enum class RenewalStatus(val label: String) {
    RENEWING_SOON("Renewing Soon"),
    UPCOMING("Upcoming"),
    EXPIRED("Expired"),
    COMPLETED("Completed")
}

enum class DocumentTypeCategory(val label: String) {
    INSURANCE("Insurance Policy"),
    WARRANTY("Warranty & Guarantee"),
    RECEIPT("Receipt & Invoice"),
    CONTRACT("Contract & Agreement"),
    TRAVEL_DOC("Travel & Tickets"),
    IDENTIFICATION("ID & Passport"),
    BILL_DOC("Bill Statement"),
    APPOINTMENT_DOC("Appointment Slip"),
    CERTIFICATE("Certificate & Records"),
    OTHER("Other Document")
}

enum class UserAuthMode(val label: String) {
    GUEST("Guest Mode"),
    EMAIL("Email Account"),
    GOOGLE("Google Account"),
    APPLE("Apple ID")
}
