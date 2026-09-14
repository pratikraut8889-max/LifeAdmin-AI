package com.example.data.model

enum class ItemType(val label: String) {
    TASK("Task"),
    BILL("Bill Payment"),
    APPOINTMENT("Appointment"),
    REMINDER("Reminder"),
    FOLLOW_UP("Follow-up")
}

enum class ItemCategory(val label: String, val iconName: String) {
    BILLS("Bills & Finance", "receipt"),
    PERSONAL("Personal", "person"),
    WORK("Work", "work"),
    SCHOOL("School & Edu", "school"),
    HEALTH("Health & Medical", "local_hospital"),
    SHOPPING("Shopping", "shopping_cart"),
    TRAVEL("Travel & Booking", "flight")
}

enum class ItemPriority(val label: String) {
    HIGH("High Priority"),
    MEDIUM("Medium"),
    LOW("Low")
}

enum class UserAuthMode(val label: String) {
    GUEST("Guest Mode"),
    EMAIL("Email Account"),
    GOOGLE("Google Account"),
    APPLE("Apple ID")
}
