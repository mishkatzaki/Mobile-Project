package com.assignmentplanner.presentation.assignment_form

import androidx.lifecycle.ViewModel
import com.assignmentplanner.data.local.entity.Course
import com.assignmentplanner.data.local.entity.Priority
import com.assignmentplanner.data.repository.InMemoryPlannerRepository
import com.assignmentplanner.data.repository.PlannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

data class AddEditAssignmentUiState(
    val assignmentId: Long? = null,
    val title: String = "",
    val courseId: Long? = null,
    val dueDate: LocalDate? = null,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val courses: List<Course> = emptyList(),
    val titleError: String? = null,
    val courseError: String? = null,
    val dueDateError: String? = null
) {
    val isEditing: Boolean get() = assignmentId != null
}

class AddEditAssignmentViewModel(
    private val repository: PlannerRepository = InMemoryPlannerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditAssignmentUiState())
    val uiState: StateFlow<AddEditAssignmentUiState> = _uiState.asStateFlow()

    fun loadAssignment(assignmentId: Long?) {
        // TODO: Person 2 - load courses from the repository; if assignmentId is not null, populate the form with that assignment
    }

    fun onTitleChange(title: String) {
        // TODO: Person 2 - update title and clear titleError
    }

    fun onCourseSelected(courseId: Long) {
        // TODO: Person 2 - update courseId and clear courseError
    }

    fun onDueDateChange(date: LocalDate) {
        // TODO: Person 2 - update dueDate and clear dueDateError
    }

    fun onDescriptionChange(description: String) {
        // TODO: Person 2 - update description
    }

    fun onPriorityChange(priority: Priority) {
        // TODO: Person 2 - update priority
    }

    fun save(onSaved: () -> Unit) {
        // TODO: Person 2 - validate title, course and due date; insert or update through the repository; then call onSaved()
    }
}
