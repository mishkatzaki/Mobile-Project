package com.assignmentplanner.presentation.assignment_details

import androidx.lifecycle.ViewModel
import com.assignmentplanner.data.local.entity.Assignment
import com.assignmentplanner.data.repository.InMemoryPlannerRepository
import com.assignmentplanner.data.repository.PlannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AssignmentDetailsUiState(
    val assignment: Assignment? = null,
    val courseName: String = "",
    val isLoading: Boolean = true
)

class AssignmentDetailsViewModel(
    private val repository: PlannerRepository = InMemoryPlannerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssignmentDetailsUiState())
    val uiState: StateFlow<AssignmentDetailsUiState> = _uiState.asStateFlow()

    fun loadAssignment(assignmentId: Long) {
        // TODO: Person 3 - observe the assignment and its course name from the repository
    }

    fun toggleCompleted() {
        // TODO: Person 3 - flip isCompleted and update through the repository
    }

    fun deleteAssignment(onDeleted: () -> Unit) {
        // TODO: Person 3 - delete through the repository, then call onDeleted()
    }

    fun buildShareText(): String {
        // TODO: Person 3 - build the share text (omit description when empty)
        return ""
    }
}
