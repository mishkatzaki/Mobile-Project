package com.assignmentplanner.data

import java.time.LocalDate

/**
 * Placeholder Data Model for Person 5 (Database & Repository).
 * This data class should be integrated with Room Entity annotations later by Person 5.
 */
enum class Priority {
    LOW, MEDIUM, HIGH
}

enum class Status {
    PENDING, COMPLETED
}

data class Assignment(
    val id: Int,
    val title: String,
    val courseName: String,
    val dueDate: LocalDate,
    val priority: Priority,
    val status: Status
)