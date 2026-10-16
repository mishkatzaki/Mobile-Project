package com.assignmentplanner.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

interface PlannerRepository {
    fun getAssignments(): Flow<List<Assignment>>
}

object InMemoryPlannerRepository : PlannerRepository {

    private val assignments = MutableStateFlow(
        listOf(
            Assignment(1, "Math Homework", "Calculus 101", LocalDate.now().minusDays(1), Priority.HIGH, Status.PENDING),
            Assignment(2, "History Essay", "World History", LocalDate.now(), Priority.MEDIUM, Status.PENDING),
            Assignment(3, "Physics Lab Report", "Physics 201", LocalDate.now().plusDays(2), Priority.LOW, Status.PENDING),
            Assignment(4, "Programming Project", "CS 101", LocalDate.now().minusDays(5), Priority.HIGH, Status.COMPLETED),
            Assignment(5, "Literature Review", "English Lit", LocalDate.now().plusDays(5), Priority.MEDIUM, Status.PENDING),
            Assignment(6, "Chemistry Pre-lab", "Chemistry 101", LocalDate.now(), Priority.HIGH, Status.PENDING)
        )
    )

    override fun getAssignments(): Flow<List<Assignment>> = assignments.asStateFlow()
}
