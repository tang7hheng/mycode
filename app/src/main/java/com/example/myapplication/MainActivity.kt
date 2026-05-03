package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.component.BottomNavBar
import com.example.myapplication.navigation.NavGraph
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.CategoryViewModel
import com.example.myapplication.viewmodel.TaskViewModel
import com.example.myapplication.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val themeMode by themeViewModel.themeMode.collectAsState()
            MyApplicationTheme(themeMode = themeMode) {
                MainScreen(themeViewModel = themeViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val taskViewModel: TaskViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when (currentRoute) {
        Screen.Today.route -> "今日待办"
        Screen.Todo.route -> "所有任务"
        Screen.Calendar.route -> "日历"
        Screen.Stats.route -> "统计"
        Screen.More.route -> "更多"
        else -> "待办"
    }

    val showFab = currentRoute != Screen.More.route && currentRoute != Screen.Stats.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(title) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Search.route)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "搜索"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.TaskEdit.createRoute())
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "添加任务")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(
                navController = navController,
                taskViewModel = taskViewModel,
                categoryViewModel = categoryViewModel,
                themeViewModel = themeViewModel
            )
        }
    }
}
