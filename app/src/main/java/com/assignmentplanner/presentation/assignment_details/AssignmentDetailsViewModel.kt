
package com.assignmentplanner.presentation.assignment_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignmentplanner.data.local.entity.Assignment
import com.assignmentplanner.data.repository.InMemoryPlannerRepository
import com.assignmentplanner.data.repository.PlannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class AssignmentDetailsUiState(
    val assignment: Assignment? = null,
    val courseName: String = "",
    val isLoading: Boolean = true
)

class AssignmentDetailsViewModel(
    private val repository: PlannerRepository = InMemoryPlannerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssignmentDetailsUiState())
    val uiState: StateFlow<AssignmentDetailsUiState> =
        _uiState.asStateFlow()

    fun loadAssignment(assignmentId: Long) {
        viewModelScope.launch {
            combine(
                repository.getAssignment(assignmentId),
                repository.getCourses()
            ) { assignment, courses ->
                AssignmentDetailsUiState(
                    assignment = assignment,
                    courseName = courses.find {
                        it.id == assignment?.courseId
                    }?.name ?: "Unknown course",
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleCompleted() {
        viewModelScope.launch {
            val assignment = _uiState.value.assignment ?: return@launch

            repository.updateAssignment(
                assignment.copy(isCompleted = !assignment.isCompleted)
            )
        }
    }

    fun deleteAssignment(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val assignment = _uiState.value.assignment
                ?: return@launch

            repository.deleteAssignment(assignment)
            onDeleted()
        }
    }

    fun buildShareText(): String {
        val state = _uiState.value
        val assignment = state.assignment ?: return ""

        return buildString {
            appendLine("Assignment: ${assignment.title}")
            appendLine("Course: ${state.courseName}")
            if (assignment.description.isNotBlank()) {
                appendLine("Description: ${assignment.description}")
            }
            appendLine("Due Date: ${assignment.dueDate}")
            appendLine("Priority: ${assignment.priority}")
            append("Status: ${
                if (assignment.isCompleted) "Completed" else "Pending"
            }")
        }
    }
}
