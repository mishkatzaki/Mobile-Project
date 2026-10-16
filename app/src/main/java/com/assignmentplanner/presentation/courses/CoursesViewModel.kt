package com.assignmentplanner.presentation.courses

import androidx.lifecycle.ViewModel
import com.assignmentplanner.data.local.entity.Course
import com.assignmentplanner.data.repository.InMemoryPlannerRepository
import com.assignmentplanner.data.repository.PlannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CoursesUiState(
    val courses: List<Course> = emptyList(),
    val nameError: String? = null,
    val deleteError: String? = null
)

class CoursesViewModel(
    private val repository: PlannerRepository = InMemoryPlannerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoursesUiState())
    val uiState: StateFlow<CoursesUiState> = _uiState.asStateFlow()

    fun addCourse(name: String) {
        // TODO: Person 4 - reject blank and duplicate names, then insert through the repository
    }

    fun renameCourse(course: Course, newName: String) {
        // TODO: Person 4 - reject blank and duplicate names, then update through the repository
    }

    fun deleteCourse(course: Course) {
        // TODO: Person 4 - block deletion when courseHasAssignments() is true and set deleteError, otherwise delete
    }
}
