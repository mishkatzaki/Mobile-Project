package com.assignmentplanner.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToCourse: () -> Unit,
    onNavigateToDetails: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val statusFilter = uiState.statusFilter
    val courseFilter = uiState.courseFilter
    val sortOption = uiState.sortOption
    val assignments = uiState.assignments

    var showCourseMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assignment Planner") },
                actions = {
                    IconButton(onClick = onNavigateToCourse) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "Courses")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Assignment")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filters and Sort Section
            Column(modifier = Modifier.padding(16.dp)) {
                // Status Filter Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = statusFilter == StatusFilter.ALL,
                        onClick = { viewModel.setStatusFilter(StatusFilter.ALL) },
                        label = { Text("All") }
                    )
                    FilterChip(
                        selected = statusFilter == StatusFilter.PENDING,
                        onClick = { viewModel.setStatusFilter(StatusFilter.PENDING) },
                        label = { Text("Pending") }
                    )
                    FilterChip(
                        selected = statusFilter == StatusFilter.COMPLETED,
                        onClick = { viewModel.setStatusFilter(StatusFilter.COMPLETED) },
                        label = { Text("Completed") }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                // Course and Sort Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Course Filter Dropdown
                    Box {
                        TextButton(onClick = { showCourseMenu = true }) {
                            Text(courseFilter ?: "All Courses")
                        }
                        DropdownMenu(
                            expanded = showCourseMenu,
                            onDismissRequest = { showCourseMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Courses") },
                                onClick = { 
                                    viewModel.setCourseFilter(null)
                                    showCourseMenu = false
                                }
                            )
                            uiState.availableCourses.forEach { course ->
                                DropdownMenuItem(
                                    text = { Text(course) },
                                    onClick = {
                                        viewModel.setCourseFilter(course)
                                        showCourseMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Sort Dropdown
                    Box {
                        TextButton(onClick = { showSortMenu = true }) {
                            Text("Sort: ${sortOption.label}")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        viewModel.setSortOption(option)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Assignments List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (assignments.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (uiState.hasAnyAssignments) {
                            "No assignments match these filters."
                        } else {
                            "No assignments yet.\nTap + to add a new one."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    if (uiState.hasAnyAssignments && uiState.isFiltered) {
                        TextButton(onClick = viewModel::clearFilters) {
                            Text("Clear filters")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(assignments, key = { it.id }) { assignment ->
                        AssignmentCard(
                            assignment = assignment,
                            onClick = { onNavigateToDetails(assignment.id) }
                        )
                    }
                }
            }
        }
    }
}