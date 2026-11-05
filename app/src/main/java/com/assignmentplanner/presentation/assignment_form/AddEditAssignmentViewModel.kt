
package com.assignmentplanner.presentation.assignment_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignmentplanner.data.local.entity.Assignment
import com.assignmentplanner.data.local.entity.Course
import com.assignmentplanner.data.local.entity.Priority
import com.assignmentplanner.data.repository.InMemoryPlannerRepository
import com.assignmentplanner.data.repository.PlannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
    val isEditing: Boolean
        get() = assignmentId != null
}

class AddEditAssignmentViewModel(
    private val repository: PlannerRepository = InMemoryPlannerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditAssignmentUiState())
    val uiState: StateFlow<AddEditAssignmentUiState> = _uiState.asStateFlow()


    fun loadAssignment(assignmentId: Long?) {
        viewModelScope.launch {
            val courses = repository.getCourses().first()

            _uiState.value = AddEditAssignmentUiState(
                assignmentId = assignmentId,
                courses = courses
            )

            if (assignmentId != null) {
                val assignment = repository.getAssignment(assignmentId).first()

                if (assignment != null) {
                    _uiState.value = _uiState.value.copy(
                        assignmentId = assignment.id,
                        title = assignment.title,
                        courseId = assignment.courseId,
                        dueDate = assignment.dueDate,
                        description = assignment.description,
                        priority = assignment.priority,
                        isCompleted = assignment.isCompleted
                    )
                }
            }
        }
    }


    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            titleError = null
        )
    }


    fun onCourseSelected(courseId: Long) {
        _uiState.value = _uiState.value.copy(
            courseId = courseId,
            courseError = null
        )
    }


    fun onDueDateChange(date: LocalDate) {
        _uiState.value = _uiState.value.copy(
            dueDate = date,
            dueDateError = null
        )
    }


    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(
            description = description
        )
    }


    fun onPriorityChange(priority: Priority) {
        _uiState.value = _uiState.value.copy(
            priority = priority
        )
    }


    fun onCompletedChange(isCompleted: Boolean) {
        _uiState.value = _uiState.value.copy(
            isCompleted = isCompleted
        )
    }


    fun save(onSaved: () -> Unit) {
        val state = _uiState.value

        val titleError =
            if (state.title.isBlank()) "Title is required" else null

        val courseError =
            if (
                state.courseId == null ||
                state.courses.none { it.id == state.courseId }
            ) {
                "Please select a course"
            } else {
                null
            }

        val dueDateError =
            if (state.dueDate == null) "Due date is required" else null

        _uiState.value = state.copy(
            titleError = titleError,
            courseError = courseError,
            dueDateError = dueDateError
        )


        if (
            titleError != null ||
            courseError != null ||
            dueDateError != null
        ) {
            return
        }

        viewModelScope.launch {
            val assignment = Assignment(
                id = state.assignmentId ?: 0L,
                courseId = state.courseId!!,
                title = state.title.trim(),
                description = state.description.trim(),
                dueDate = state.dueDate!!,
                priority = state.priority,
                isCompleted = state.isCompleted
            )

            if (state.isEditing) {
                repository.updateAssignment(assignment)
            } else {
                repository.insertAssignment(assignment)
            }

            onSaved()
        }
    }
}
