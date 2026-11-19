package com.assignmentplanner.presentation.assignment_form

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.assignmentplanner.data.local.AppDatabase
import com.assignmentplanner.data.local.entity.Course
import com.assignmentplanner.data.repository.RoomPlannerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddEditAssignmentViewModelTest {
    @Test
    fun returningFromCourses_keepsDraftAndShowsNewCourse() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val repository = RoomPlannerRepository(database.courseDao(), database.assignmentDao())
        val viewModel = AddEditAssignmentViewModel(repository)

        try {
            withContext(Dispatchers.Main) {
                viewModel.loadAssignment(assignmentId = null)
                viewModel.onTitleChange("Test Assignment")
                viewModel.onDescriptionChange("Draft description")
            }

            val courseId = repository.insertCourse(Course(name = "Mobile Computing"))
            withContext(Dispatchers.Main) {
                viewModel.loadAssignment(assignmentId = null)
            }

            val state = withTimeout(5_000) {
                viewModel.uiState.first { it.courses.any { course -> course.id == courseId } }
            }
            assertEquals("Test Assignment", state.title)
            assertEquals("Draft description", state.description)
            assertEquals("Mobile Computing", state.courses.single().name)
        } finally {
            withContext(Dispatchers.Main) {
                viewModel.viewModelScope.cancel()
            }
            database.close()
        }
    }
}
