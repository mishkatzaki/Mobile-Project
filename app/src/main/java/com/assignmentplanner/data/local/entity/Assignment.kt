package com.assignmentplanner.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class Priority { LOW, MEDIUM, HIGH }

@Entity(
    tableName = "assignments",
    foreignKeys = [ForeignKey(
        entity = Course::class,
        parentColumns = ["id"],
        childColumns = ["courseId"],
        onDelete = ForeignKey.RESTRICT   // a course with assignments can't be deleted
    )],
    indices = [Index("courseId")]
)
data class Assignment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseId: Long,
    val title: String,
    val description: String = "",
    val dueDate: LocalDate,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false
)