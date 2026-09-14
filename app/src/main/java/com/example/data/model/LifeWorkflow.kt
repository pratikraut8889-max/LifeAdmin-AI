package com.example.data.model

import com.example.data.local.ExtractedItemEntity

enum class WorkflowType(
    val title: String,
    val description: String,
    val category: ItemCategory,
    val iconName: String
) {
    PASSPORT_RENEWAL(
        title = "Passport Application & Renewal",
        description = "Form DS-82, photos, appointment booking, and document checklist.",
        category = ItemCategory.GOVERNMENT,
        iconName = "gavel"
    ),
    VEHICLE_REGISTRATION(
        title = "Vehicle Registration & Insurance",
        description = "Emissions check, insurance comparison, DMV payment, and license tags.",
        category = ItemCategory.VEHICLE,
        iconName = "directions_car"
    ),
    TRAVEL_PREPARATION(
        title = "Travel & Trip Preparation",
        description = "Check-in 24h prior, pack essentials, download boarding pass, leave-home alert.",
        category = ItemCategory.TRAVEL,
        iconName = "flight"
    ),
    MOVING_HOUSE(
        title = "Moving House Administration",
        description = "Landlord notice, USPS change of address, utility transfers, and address updates.",
        category = ItemCategory.HOME,
        iconName = "home"
    ),
    RETURN_WARRANTY(
        title = "Product Return & Warranty Claim",
        description = "Verify return window, locate invoice, generate return label, and drop package.",
        category = ItemCategory.SHOPPING,
        iconName = "shopping_cart"
    ),
    DOCTOR_CHECKUP(
        title = "Medical Appointment Preparation",
        description = "Locate lab results, verify insurance card, fasting instructions, and consultation questions.",
        category = ItemCategory.HEALTH,
        iconName = "local_hospital"
    )
}
