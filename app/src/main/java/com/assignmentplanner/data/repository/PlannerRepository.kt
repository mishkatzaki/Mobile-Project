package com.assignmentplanner.data.repository

import com.assignmentplanner.data.local.entity.Assignment
import com.assignmentplanner.data.local.entity.Course
import com.assignmentplanner.data.local.entity.Priority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.LocalDate

interface PlannerRepository {
    fun getAssignments(): Flow<List<Assignment>>
    fun getAssignment(id: Long): Flow<Assignment?>
    suspend fun insertAssignment(assignment: Assignment): Long
    suspend fun updateAssignment(assignment: Assignment)
    suspend fun deleteAssignment(assignment: Assignment)

    fun getCourses(): Flow<List<Course>>
    suspend fun insertCourse(course: Course): Long
    suspend fun updateCourse(course: Course)
    suspend fun deleteCourse(course: Course)
    suspend fun courseHasAssignments(courseId: Long): Boolean
}

// TODO: Person 5 - replace with a Room-backed implementation built on CourseDao and AssignmentDao
object InMemoryPlannerRepository : PlannerRepository {

    private val courses = MutableStateFlow(
        listOf(
            Course(1, "Calculus 101"),
            Course(2, "World History"),
            Course(3, "Physics 201"),
            Course(4, "CS 101"),
            Course(5, "English Lit"),
            Course(6, "Chemistry 101")
        )
    )

    private val assignments = MutableStateFlow(
        listOf(
            Assignment(1, 1, "Math Homework", dueDate = LocalDate.now().minusDays(1), priority = Priority.HIGH),
            Assignment(2, 2, "History Essay", dueDate = LocalDate.now(), priority = Priority.MEDIUM),
            Assignment(3, 3, "Physics Lab Report", dueDate = LocalDate.now().plusDays(2), priority = Priority.LOW),
            Assignment(4, 4, "Programming Project", dueDate = LocalDate.now().minusDays(5), priority = Priority.HIGH, isCompleted = true),
            Assignment(5, 5, "Literature Review", dueDate = LocalDate.now().plusDays(5), priority = Priority.MEDIUM),
            Assignment(6, 6, "Chemistry Pre-lab", dueDate = LocalDate.now(), priority = Priority.HIGH)
        )
    )

    override fun getAssignments(): Flow<List<Assignment>> = assignments.asStateFlow()

    override fun getAssignment(id: Long): Flow<Assignment?> =
        assignments.map { list -> list.find { it.id == id } }

    override suspend fun insertAssignment(assignment: Assignment): Long {
        val id = (assignments.value.maxOfOrNull { it.id } ?: 0) + 1
        assignments.update { it + assignment.copy(id = id) }
        return id
    }

    override suspend fun updateAssignment(assignment: Assignment) {
        assignments.update { list -> list.map { if (it.id == assignment.id) assignment else it } }
    }

    override suspend fun deleteAssignment(assignment: Assignment) {
        assignments.update { list -> list.filterNot { it.id == assignment.id } }
    }

    override fun getCourses(): Flow<List<Course>> = courses.asStateFlow()

    override suspend fun insertCourse(course: Course): Long {
        val id = (courses.value.maxOfOrNull { it.id } ?: 0) + 1
        courses.update { it + course.copy(id = id) }
        return id
    }

    override suspend fun updateCourse(course: Course) {
        courses.update { list -> list.map { if (it.id == course.id) course else it } }
    }

    override suspend fun deleteCourse(course: Course) {
        courses.update { list -> list.filterNot { it.id == course.id } }
    }

    override suspend fun courseHasAssignments(courseId: Long): Boolean =
        assignments.value.any { it.courseId == courseId }
}
