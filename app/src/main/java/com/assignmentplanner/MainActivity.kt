package com.assignmentplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.assignmentplanner.ui.home.HomeScreen
import com.assignmentplanner.ui.theme.AssignmentPlannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AssignmentPlannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Navigation Placeholder for Person 6
                    // For now, directly showing HomeScreen as part of Person 1's testing
                    HomeScreen(
                        onNavigateToAdd = { /* Navigate to Add Assignment (Person 2) */ },
                        onNavigateToCourse = { /* Navigate to Courses (Person 4) */ },
                        onNavigateToDetails = { _ -> /* Navigate to Details (Person 3) */ }
                    )
                }
            }
        }
    }
}