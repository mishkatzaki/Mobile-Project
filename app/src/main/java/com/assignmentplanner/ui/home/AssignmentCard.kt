package com.assignmentplanner.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.assignmentplanner.data.Assignment
import com.assignmentplanner.data.Priority
import com.assignmentplanner.data.Status
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AssignmentCard(
    assignment: Assignment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (assignment.status == Status.COMPLETED) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = assignment.title,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (assignment.status == Status.COMPLETED) Color.Gray else MaterialTheme.colorScheme.onSurface
                )

                val priorityColor = when (assignment.priority) {
                    Priority.HIGH -> Color.Red
                    Priority.MEDIUM -> Color(0xFFFFA500)
                    Priority.LOW -> Color(0xFF4CAF50)
                }
                Text(
                    text = assignment.priority.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = priorityColor,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = assignment.courseName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                val formattedDate = assignment.dueDate.format(dateFormatter)
                
                val deadlineText = getDeadlineText(assignment)
                val deadlineColor = getDeadlineColor(assignment)
                
                Column {
                    Text(
                        text = "Due: $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (deadlineText.isNotEmpty()) {
                        Text(
                            text = deadlineText,
                            style = MaterialTheme.typography.labelMedium,
                            color = deadlineColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    text = if (assignment.status == Status.COMPLETED) "Completed" else "Pending",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (assignment.status == Status.COMPLETED) Color.Gray else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun getDeadlineText(assignment: Assignment): String {
    if (assignment.status == Status.COMPLETED) return ""
    
    val today = LocalDate.now()
    return when {
        assignment.dueDate.isBefore(today) -> "Overdue"
        assignment.dueDate.isEqual(today) -> "Due Today"
        else -> "Upcoming"
    }
}

private fun getDeadlineColor(assignment: Assignment): Color {
    if (assignment.status == Status.COMPLETED) return Color.Gray
    
    val today = LocalDate.now()
    return when {
        assignment.dueDate.isBefore(today) -> Color.Red
        assignment.dueDate.isEqual(today) -> Color(0xFFFFA500)
        else -> Color.Gray
    }
}