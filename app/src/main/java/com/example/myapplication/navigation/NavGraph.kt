package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.screen.*
import com.example.myapplication.viewmodel.CategoryViewModel
import com.example.myapplication.viewmodel.TaskViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    taskViewModel: TaskViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel()
) {
    NavHost(navController = navController, startDestination = Screen.Today.route) {
        composable(Screen.Today.route) {
            TodayScreen(
                taskViewModel = taskViewModel,
                categoryViewModel = categoryViewModel,
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskEdit.createRoute(taskId))
                },
                onAddTask = {
                    navController.navigate(Screen.TaskEdit.createRoute())
                }
            )
        }
        composable(Screen.Todo.route) {
            TodoScreen(
                taskViewModel = taskViewModel,
                categoryViewModel = categoryViewModel,
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskEdit.createRoute(taskId))
                },
                onAddTask = {
                    navController.navigate(Screen.TaskEdit.createRoute())
                }
            )
        }
        composable(Screen.Calendar.route) {
            CalendarScreen(
                taskViewModel = taskViewModel,
                categoryViewModel = categoryViewModel,
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskEdit.createRoute(taskId))
                },
                onAddTask = {
                    navController.navigate(Screen.TaskEdit.createRoute())
                }
            )
        }
        composable(Screen.Stats.route) {
            StatsScreen(
                taskViewModel = taskViewModel,
                categoryViewModel = categoryViewModel
            )
        }
        composable(Screen.More.route) {
            MoreScreen()
        }
        composable(
            route = Screen.TaskEdit.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            TaskEditScreen(
                taskId = taskId,
                taskViewModel = taskViewModel,
                categoryViewModel = categoryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
