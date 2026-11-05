
package com.assignmentplanner.presentation.assignment_details

import android.content.Intent
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.assignmentplanner.data.local.entity.Priority
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDetailsScreen(
    assignmentId: Long,
    onNavigateBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
    viewModel: AssignmentDetailsViewModel = viewModel()
) {
    LaunchedEffect(assignmentId) {
        viewModel.loadAssignment(assignmentId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val assignment = uiState.assignment
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assignment Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(assignmentId) }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete"
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                uiState.isLoading -> {
                    Text("Loading assignment...")
                }

                assignment == null -> {
                    Text("Assignment not found.")
                    OutlinedButton(onClick = onNavigateBack) {
                        Text("Go Back")
                    }
                }

                else -> {
                    Text(
                        text = assignment.title,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DetailRow("Course", uiState.courseName)

                            DetailRow(
                                "Description",
                                assignment.description.ifBlank {
                                    "No description provided"
                                }
                            )

                            DetailRow(
                                "Due Date",
                                assignment.dueDate.format(
                                    DateTimeFormatter.ofPattern("dd MMM yyyy")
                                )
                            )

                            DetailRow(
                                "Priority",
                                when (assignment.priority) {
                                    Priority.HIGH -> "High"
                                    Priority.MEDIUM -> "Medium"
                                    Priority.LOW -> "Low"
                                }
                            )

                            DetailRow(
                                "Status",
                                if (assignment.isCompleted) {
                                    "Completed"
                                } else {
                                    "Pending"
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { viewModel.toggleCompleted() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (assignment.isCompleted) {
                                "Mark as Pending"
                            } else {
                                "Mark as Completed"
                            }
                        )
                    }

                    OutlinedButton(
                        onClick = { onEdit(assignmentId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null
                        )
                        Text("  Edit Assignment")
                    }

                    OutlinedButton(
                        onClick = {
                            val shareText = viewModel.buildShareText()
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }

                            context.startActivity(
                                Intent.createChooser(
                                    sendIntent,
                                    "Share Assignment"
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null
                        )
                        Text("  Share Assignment")
                    }

                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null
                        )
                        Text("  Delete Assignment")
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Assignment?") },
            text = {
                Text(
                    "Are you sure you want to delete this assignment? " +
                            "This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteAssignment(onDeleted)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
