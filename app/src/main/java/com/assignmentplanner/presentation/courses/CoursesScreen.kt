package com.assignmentplanner.presentation.courses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    onNavigateBack: () -> Unit,
    viewModel: CoursesViewModel =
        viewModel(factory = CoursesViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showCourseDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var courseName by rememberSaveable {
        mutableStateOf("")
    }

    var editingCourseId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    var deletingCourseId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    val isBusy = uiState.isSaving || uiState.isDeleting

    val canModifyCourses =
        !isBusy &&
                !uiState.isLoading &&
                uiState.loadError == null

    val courseToEdit = uiState.courses.find {
        it.id == editingCourseId
    }

    val courseToDelete = uiState.courses.find {
        it.id == deletingCourseId
    }

    val editingCourseMissing =
        editingCourseId != null && courseToEdit == null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Courses")
                },

                navigationIcon = {
                    IconButton(
                        enabled = !isBusy,
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            if (!uiState.isLoading && uiState.loadError == null) {
                FloatingActionButton(
                    onClick = {
                        if (canModifyCourses) {
                            editingCourseId = null
                            courseName = ""
                            viewModel.clearNameError()
                            showCourseDialog = true
                        }
                    }
                ) {
                    Text(
                        text = "Add Course",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),

            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                uiState.loadError != null -> {
                    Text(
                        text = uiState.loadError
                            ?: "Could not load courses.",

                        modifier = Modifier.padding(24.dp),

                        color = MaterialTheme.colorScheme.error
                    )
                }

                uiState.courses.isEmpty() -> {
                    Text(
                        text = "No courses yet. Add your first course.",
                        modifier = Modifier.padding(24.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),

                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 96.dp
                        ),

                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.courses,
                            key = { course -> course.id }
                        ) { course ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),

                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {
                                Text(
                                    text = course.name,
                                    modifier = Modifier.weight(1f),

                                    style =
                                        MaterialTheme.typography.bodyLarge
                                )

                                TextButton(
                                    enabled = canModifyCourses,

                                    onClick = {
                                        editingCourseId = course.id
                                        courseName = course.name

                                        viewModel.clearNameError()
                                        showCourseDialog = true
                                    }
                                ) {
                                    Text("Edit")
                                }

                                TextButton(
                                    enabled = canModifyCourses,

                                    onClick = {
                                        viewModel.clearDeleteError()
                                        deletingCourseId = course.id
                                    }
                                ) {
                                    Text(
                                        text = "Delete",
                                        color =
                                            MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCourseDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isBusy) {
                    showCourseDialog = false
                    editingCourseId = null
                    viewModel.clearNameError()
                }
            },

            title = {
                Text(
                    if (editingCourseId == null) {
                        "Add Course"
                    } else {
                        "Edit Course"
                    }
                )
            },

            text = {
                OutlinedTextField(
                    value = courseName,

                    onValueChange = { newName ->
                        courseName = newName
                        viewModel.clearNameError()
                    },

                    label = {
                        Text("Course name")
                    },

                    singleLine = true,
                    enabled = !isBusy,

                    isError =
                        uiState.nameError != null ||
                                editingCourseMissing,

                    supportingText = {
                        val message = if (editingCourseMissing) {
                            "This course is no longer available."
                        } else {
                            uiState.nameError
                        }

                        if (message != null) {
                            Text(message)
                        }
                    },

                    modifier = Modifier.fillMaxWidth()
                )
            },

            confirmButton = {
                TextButton(
                    enabled =
                        canModifyCourses && !editingCourseMissing,

                    onClick = {
                        val onSaved: () -> Unit = {
                            showCourseDialog = false
                            courseName = ""
                            editingCourseId = null
                        }

                        if (editingCourseId == null) {
                            viewModel.addCourse(
                                name = courseName,
                                onSaved = onSaved
                            )
                        } else {
                            val selectedCourse = courseToEdit

                            if (selectedCourse != null) {
                                viewModel.renameCourse(
                                    course = selectedCourse,
                                    newName = courseName,
                                    onSaved = onSaved
                                )
                            }
                        }
                    }
                ) {
                    Text(
                        if (uiState.isSaving) {
                            "Saving..."
                        } else {
                            "Save"
                        }
                    )
                }
            },

            dismissButton = {
                TextButton(
                    enabled = !isBusy,

                    onClick = {
                        showCourseDialog = false
                        editingCourseId = null
                        viewModel.clearNameError()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (courseToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isBusy) {
                    deletingCourseId = null
                }
            },

            title = {
                Text("Delete course?")
            },

            text = {
                Text(
                    "Are you sure you want to delete " +
                            "\"${courseToDelete.name}\"?"
                )
            },

            confirmButton = {
                TextButton(
                    enabled = canModifyCourses,

                    onClick = {
                        viewModel.deleteCourse(
                            course = courseToDelete,

                            onFinished = {
                                deletingCourseId = null
                            }
                        )
                    }
                ) {
                    Text(
                        text = if (uiState.isDeleting) {
                            "Deleting..."
                        } else {
                            "Delete"
                        },

                        color = MaterialTheme.colorScheme.error
                    )
                }
            },

            dismissButton = {
                TextButton(
                    enabled = !isBusy,

                    onClick = {
                        deletingCourseId = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    val deleteError = uiState.deleteError

    if (
        deleteError != null &&
        deletingCourseId == null &&
        !uiState.isDeleting
    ) {
        AlertDialog(
            onDismissRequest = {
                viewModel.clearDeleteError()
            },

            title = {
                Text("Could not delete course")
            },

            text = {
                Text(deleteError)
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearDeleteError()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}