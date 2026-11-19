
package com.assignmentplanner.presentation.assignment_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignmentplanner.data.local.entity.Assignment
import com.assignmentplanner.data.local.entity.Course
import com.assignmentplanner.data.local.entity.Priority
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.assignmentplanner.PlannerApplication
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
    val dueDateText: String = "",
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
    private val repository: PlannerRepository
) : ViewModel() {

    private fun parseDate(text: String): LocalDate? {
        return try {
            LocalDate.parse(text.trim())
        } catch (e: Exception) {
            null
        }
    }

    private val _uiState = MutableStateFlow(AddEditAssignmentUiState())
    val uiState: StateFlow<AddEditAssignmentUiState> = _uiState.asStateFlow()


    private var isInitialized = false

    fun loadAssignment(assignmentId: Long?) {
        // Returning from Courses can re-enter composition with the same draft.
        if (isInitialized) return
        isInitialized = true
        _uiState.value = _uiState.value.copy(assignmentId = assignmentId)

        viewModelScope.launch {
            repository.getCourses().collect { courses ->
                _uiState.value = _uiState.value.copy(courses = courses)
            }
        }

        if (assignmentId != null) {
            viewModelScope.launch {
                val assignment = repository.getAssignment(assignmentId).first()

                if (assignment != null) {
                    _uiState.value = _uiState.value.copy(
                        assignmentId = assignment.id,
                        title = assignment.title,
                        courseId = assignment.courseId,
                        dueDate = assignment.dueDate,
                        dueDateText = assignment.dueDate.toString(),
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


    fun onDueDateTextChange(text: String) {
        _uiState.value = _uiState.value.copy(
            dueDateText = text,
            dueDate = parseDate(text),
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

        val parsedDate = parseDate(state.dueDateText)

        val dueDateError = if (state.dueDateText.isBlank()) {
            "Due date is required"
        } else if (parsedDate == null) {
            "Use the format YYYY-MM-DD, with - between year, month and day (e.g. 2026-12-31)"
        } else if (parsedDate.isBefore(LocalDate.now()) && !state.isEditing) {
            "Due date cannot be in the past"
        } else {
            null
        }
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
                dueDate = parsedDate!!,
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
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = this[
                    ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
                ] as PlannerApplication

                AddEditAssignmentViewModel(
                    repository = application.repository
                )
            }
        }
    }
}
