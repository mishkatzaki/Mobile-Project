package com.assignmentplanner.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignmentplanner.data.Assignment
import com.assignmentplanner.data.InMemoryPlannerRepository
import com.assignmentplanner.data.PlannerRepository
import com.assignmentplanner.data.Status
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

data class HomeUiState(
    val assignments: List<Assignment> = emptyList(),
    val availableCourses: List<String> = emptyList(),
    val statusFilter: StatusFilter = StatusFilter.ALL,
    val courseFilter: String? = null,
    val sortOption: SortOption = SortOption.DEADLINE,
    val hasAnyAssignments: Boolean = false,
    val isLoading: Boolean = true
) {
    val isFiltered: Boolean get() = statusFilter != StatusFilter.ALL || courseFilter != null
}

class HomeViewModel(
    repository: PlannerRepository = InMemoryPlannerRepository
) : ViewModel() {

    private val statusFilter = MutableStateFlow(StatusFilter.ALL)
    private val courseFilter = MutableStateFlow<String?>(null)
    private val sortOption = MutableStateFlow(SortOption.DEADLINE)

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAssignments(),
        statusFilter,
        courseFilter,
        sortOption
    ) { all, status, course, sort ->
        val courses = all.map { it.courseName }.distinct().sorted()
        val activeCourse = course?.takeIf { it in courses }

        HomeUiState(
            assignments = all
                .filter { it.matches(status) }
                .filter { activeCourse == null || it.courseName == activeCourse }
                .sortedWith(sort.comparator()),
            availableCourses = courses,
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

    fun setCourseFilter(course: String?) {
        courseFilter.value = course
    }

    fun setSortOption(option: SortOption) {
        sortOption.value = option
    }

    fun clearFilters() {
        statusFilter.value = StatusFilter.ALL
        courseFilter.value = null
    }

    private fun Assignment.matches(filter: StatusFilter): Boolean = when (filter) {
        StatusFilter.ALL -> true
        StatusFilter.PENDING -> status == Status.PENDING
        StatusFilter.COMPLETED -> status == Status.COMPLETED
    }

    private fun SortOption.comparator(): Comparator<Assignment> = when (this) {
        SortOption.DEADLINE -> compareBy<Assignment> { it.dueDate }
            .thenByDescending { it.priority.ordinal }
        SortOption.PRIORITY -> compareByDescending<Assignment> { it.priority.ordinal }
            .thenBy { it.dueDate }
    }
}
