package com.p79smartexam.smartexam.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.p79smartexam.smartexam.ui.screen.TestDataScreen
import com.p79smartexam.smartexam.ui.screen.TestQuizScreen

@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.TestQuiz.route
    ) {
        composable(route = Screen.TestData.route) {
            TestDataScreen(navController = navController)
        }
        composable(route = Screen.TestQuiz.route) {
            TestQuizScreen(navController = navController)
        }
    }
}
