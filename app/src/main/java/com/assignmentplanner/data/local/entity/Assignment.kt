package com.assignmentplanner.data.local.entity

import java.time.LocalDate

enum class Priority { LOW, MEDIUM, HIGH }

// TODO: Person 5 - add Room @Entity annotations, foreign key to Course, and a TypeConverter for LocalDate
data class Assignment(
    val id: Long = 0,
    val courseId: Long,
    val title: String,
    val description: String = "",
    val dueDate: LocalDate,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false
)
