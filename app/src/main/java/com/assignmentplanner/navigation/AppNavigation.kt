package com.assignmentplanner.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.assignmentplanner.presentation.assignment_details.AssignmentDetailsScreen
import com.assignmentplanner.presentation.assignment_form.AddEditAssignmentScreen
import com.assignmentplanner.presentation.courses.CoursesScreen
import com.assignmentplanner.presentation.home.HomeScreen

object Routes {
    const val HOME = "home"
    const val ADD = "add"
    const val EDIT = "edit/{assignmentId}"
    const val DETAILS = "details/{assignmentId}"
    const val COURSES = "courses"
    const val ARG_ASSIGNMENT_ID = "assignmentId"

    fun edit(assignmentId: Long) = "edit/$assignmentId"
    fun details(assignmentId: Long) = "details/$assignmentId"
}

// TODO: Person 6 - handle missing/invalid assignment IDs safely and test back behavior across all screens
@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToAdd = { navController.navigate(Routes.ADD) },
                onNavigateToCourse = { navController.navigate(Routes.COURSES) },
                onNavigateToDetails = { id -> navController.navigate(Routes.details(id)) }
            )
        }

        composable(Routes.ADD) {
            AddEditAssignmentScreen(
                assignmentId = null,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCourses = { navController.navigate(Routes.COURSES) }
            )
        }

        composable(
            route = Routes.EDIT,
            arguments = listOf(navArgument(Routes.ARG_ASSIGNMENT_ID) { type = NavType.LongType })
        ) { entry ->
            AddEditAssignmentScreen(
                assignmentId = entry.arguments?.getLong(Routes.ARG_ASSIGNMENT_ID),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCourses = { navController.navigate(Routes.COURSES) }
            )
        }

        composable(
            route = Routes.DETAILS,
            arguments = listOf(navArgument(Routes.ARG_ASSIGNMENT_ID) { type = NavType.LongType })
        ) { entry ->
            val assignmentId = entry.arguments?.getLong(Routes.ARG_ASSIGNMENT_ID) ?: return@composable
            AssignmentDetailsScreen(
                assignmentId = assignmentId,
                onNavigateBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(Routes.edit(id)) },
                onDeleted = { navController.popBackStack(Routes.HOME, inclusive = false) }
            )
        }

        composable(Routes.COURSES) {
            CoursesScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
