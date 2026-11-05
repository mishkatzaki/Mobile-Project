package com.assignmentplanner.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignmentplanner.data.local.entity.Assignment
import com.assignmentplanner.data.local.entity.Course
import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.assignmentplanner.PlannerApplication
import com.assignmentplanner.data.repository.PlannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class StatusFilter { ALL, PENDING, COMPLETED }

enum class SortOption(val label: String) {
    DEADLINE("Deadline"),
    PRIORITY("Priority")
}

data class AssignmentItem(
    val assignment: Assignment,
    val courseName: String
)

data class HomeUiState(
    val items: List<AssignmentItem> = emptyList(),
    val availableCourses: List<Course> = emptyList(),
    val statusFilter: StatusFilter = StatusFilter.ALL,
    val courseFilter: Course? = null,
    val sortOption: SortOption = SortOption.DEADLINE,
    val hasAnyAssignments: Boolean = false,
    val isLoading: Boolean = true
) {
    val isFiltered: Boolean get() = statusFilter != StatusFilter.ALL || courseFilter != null
}

class HomeViewModel(
    private val repository: PlannerRepository
) : ViewModel()  {

    private val statusFilter = MutableStateFlow(StatusFilter.ALL)
    private val courseFilterId = MutableStateFlow<Long?>(null)
    private val sortOption = MutableStateFlow(SortOption.DEADLINE)

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAssignments(),
        repository.getCourses(),
        statusFilter,
        courseFilterId,
        sortOption
    ) { all, courses, status, courseId, sort ->
        val courseNames = courses.associate { it.id to it.name }
        val activeCourse = courses.find { it.id == courseId }

        HomeUiState(
            items = all
                .filter { it.matches(status) }
                .filter { activeCourse == null || it.courseId == activeCourse.id }
                .sortedWith(sort.comparator())
                .map { AssignmentItem(it, courseNames[it.courseId].orEmpty()) },
            availableCourses = courses.sortedBy { it.name },
            statusFilter = status,
            courseFilter = activeCourse,
            sortOption = sort,
            hasAnyAssignments = all.isNotEmpty(),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun setStatusFilter(filter: StatusFilter) {
        statusFilter.value = filter
    }

    fun setCourseFilter(courseId: Long?) {
        courseFilterId.value = courseId
    }

    fun setSortOption(option: SortOption) {
        sortOption.value = option
    }

    fun clearFilters() {
        statusFilter.value = StatusFilter.ALL
        courseFilterId.value = null
    }

    private fun Assignment.matches(filter: StatusFilter): Boolean = when (filter) {
        StatusFilter.ALL -> true
        StatusFilter.PENDING -> !isCompleted
        StatusFilter.COMPLETED -> isCompleted
    }

    private fun SortOption.comparator(): Comparator<Assignment> = when (this) {
        SortOption.DEADLINE -> compareBy<Assignment> { it.dueDate }
            .thenByDescending { it.priority.ordinal }
        SortOption.PRIORITY -> compareByDescending<Assignment> { it.priority.ordinal }
            .thenBy { it.dueDate }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = this[
                    ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
                ] as PlannerApplication

                HomeViewModel(repository = application.repository)
            }
        }
    }
}
