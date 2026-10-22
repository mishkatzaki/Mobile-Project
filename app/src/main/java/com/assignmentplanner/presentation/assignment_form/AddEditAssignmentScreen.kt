
package com.assignmentplanner.presentation.assignment_form

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.assignmentplanner.data.local.entity.Priority
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAssignmentScreen(
    assignmentId: Long?,
    onNavigateBack: () -> Unit,
    onNavigateToCourses: () -> Unit,
    viewModel: AddEditAssignmentViewModel = viewModel()
) {
    LaunchedEffect(assignmentId) {
        viewModel.loadAssignment(assignmentId)
    }

    val state by viewModel.uiState.collectAsState()

    var courseMenuExpanded by remember { mutableStateOf(false) }
    var dueDateText by remember { mutableStateOf("") }

    LaunchedEffect(state.dueDate) {
        dueDateText = state.dueDate?.toString() ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (assignmentId == null) {
                            "Add Assignment"
                        } else {
                            "Edit Assignment"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {


            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title") },
                isError = state.titleError != null,
                supportingText = {
                    state.titleError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )


            ExposedDropdownMenuBox(
                expanded = courseMenuExpanded,
                onExpandedChange = {
                    if (state.courses.isNotEmpty()) {
                        courseMenuExpanded = !courseMenuExpanded
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedCourse = state.courses.find {
                    it.id == state.courseId
                }

                OutlinedTextField(
                    value = selectedCourse?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Course") },
                    placeholder = { Text("Select a course") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = courseMenuExpanded
                        )
                    },
                    isError = state.courseError != null,
                    supportingText = {
                        state.courseError?.let { Text(it) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = courseMenuExpanded,
                    onDismissRequest = {
                        courseMenuExpanded = false
                    }
                ) {
                    state.courses.forEach { course ->
                        DropdownMenuItem(
                            text = { Text(course.name) },
                            onClick = {
                                viewModel.onCourseSelected(course.id)
                                courseMenuExpanded = false
                            }
                        )
                    }
                }
            }

            if (state.courses.isEmpty()) {
                Text("No courses available. Add a course first.")
                Button(
                    onClick = onNavigateToCourses,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Go to Courses")
                }
            }


            OutlinedTextField(
                value = dueDateText,
                onValueChange = { input ->
                    dueDateText = input
                    val parsedDate = runCatching {
                        LocalDate.parse(input)
                    }.getOrNull()

                    if (parsedDate != null) {
                        viewModel.onDueDateChange(parsedDate)
                    }
                },
                label = { Text("Due Date (YYYY-MM-DD)") },
                placeholder = { Text("2026-12-31") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.dueDateError != null,
                supportingText = {
                    if (state.dueDateError != null) {
                        Text(state.dueDateError!!)
                    } else {
                        Text("Enter the date as year-month-day")
                    }
                }
            )


            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )


            Text("Priority")

            Column {
                Priority.values().forEach { priority ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.priority == priority,
                            onClick = {
                                viewModel.onPriorityChange(priority)
                            }
                        )
                        Text(priority.name)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Save
            Button(
                onClick = {
                    viewModel.save(onSaved = onNavigateBack)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (state.isEditing) {
                        "Save Changes"
                    } else {
                        "Add Assignment"
                    }
                )
            }
        }
    }
}
