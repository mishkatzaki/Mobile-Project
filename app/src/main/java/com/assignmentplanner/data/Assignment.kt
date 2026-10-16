package com.assignmentplanner.data

import java.time.LocalDate

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