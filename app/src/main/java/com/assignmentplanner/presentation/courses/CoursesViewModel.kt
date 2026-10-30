package com.assignmentplanner.presentation.courses

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.assignmentplanner.PlannerApplication
import com.assignmentplanner.data.local.entity.Course
import com.assignmentplanner.data.repository.PlannerRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CoursesUiState(
    val courses: List<Course> = emptyList(),
    val nameError: String? = null,
    val deleteError: String? = null,
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false
)

class CoursesViewModel(
    private val repository: PlannerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoursesUiState())

    val uiState: StateFlow<CoursesUiState> =
        _uiState.asStateFlow()

    init {
        observeCourses()
    }

    private fun observeCourses() {
        viewModelScope.launch {
            try {
                repository.getCourses().collect { courses ->
                    _uiState.update {
                        it.copy(
                            courses = courses,
                            isLoading = false,
                            loadError = null
                        )
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loadError = "Could not load courses."
                    )
                }
            }
        }
    }

    fun addCourse(
        name: String,
        onSaved: () -> Unit
    ) {
        saveCourse(
            course = null,
            name = name,
            onSaved = onSaved
        )
    }

    fun renameCourse(
        course: Course,
        newName: String,
        onSaved: () -> Unit
    ) {
        saveCourse(
            course = course,
            name = newName,
            onSaved = onSaved
        )
    }

    private fun saveCourse(
        course: Course?,
        name: String,
        onSaved: () -> Unit
    ) {
        val state = _uiState.value

        if (
            state.isSaving ||
            state.isDeleting ||
            state.isLoading ||
            state.loadError != null
        ) {
            return
        }

        val cleanedName = name.trim()

        if (cleanedName.isBlank()) {
            _uiState.update {
                it.copy(nameError = "Course name is required.")
            }
            return
        }

        val alreadyExists = state.courses.any {
            it.id != course?.id &&
                    it.name.equals(cleanedName, ignoreCase = true)
        }

        if (alreadyExists) {
            _uiState.update {
                it.copy(
                    nameError = "A course with this name already exists."
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                nameError = null,
                isSaving = true
            )
        }

        viewModelScope.launch {
            try {
                if (course == null) {
                    repository.insertCourse(
                        Course(name = cleanedName)
                    )
                } else {
                    repository.updateCourse(
                        course.copy(name = cleanedName)
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: SQLiteConstraintException) {
                _uiState.update {
                    it.copy(
                        nameError =
                            "A course with this name already exists."
                    )
                }
                return@launch
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        nameError =
                            "Could not save the course. Please try again."
                    )
                }
                return@launch
            } finally {
                _uiState.update {
                    it.copy(isSaving = false)
                }
            }

            onSaved()
        }
    }

    fun deleteCourse(
        course: Course,
        onFinished: () -> Unit
    ) {
        val state = _uiState.value

        if (
            state.isDeleting ||
            state.isSaving ||
            state.isLoading ||
            state.loadError != null
        ) {
            return
        }

        _uiState.update {
            it.copy(
                isDeleting = true,
                deleteError = null
            )
        }

        viewModelScope.launch {
            try {
                val hasAssignments =
                    repository.courseHasAssignments(course.id)

                if (hasAssignments) {
                    _uiState.update {
                        it.copy(
                            deleteError =
                                "This course has assignments. " +
                                        "Delete or move them before deleting " +
                                        "the course."
                        )
                    }
                } else {
                    repository.deleteCourse(course)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: SQLiteConstraintException) {
                _uiState.update {
                    it.copy(
                        deleteError =
                            "This course is still used by assignments " +
                                    "and cannot be deleted."
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        deleteError =
                            "Could not delete the course. Please try again."
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(isDeleting = false)
                }
            }

            onFinished()
        }
    }

    fun clearNameError() {
        _uiState.update {
            it.copy(nameError = null)
        }
    }

    fun clearDeleteError() {
        _uiState.update {
            it.copy(deleteError = null)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = this[
                    ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
                ] as PlannerApplication

                CoursesViewModel(
                    repository = application.repository
                )
            }
        }
    }
}