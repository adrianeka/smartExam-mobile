package com.p79smartexam.smartexam.navigation

sealed class Screen( val route: String) {
    object TestData : Screen("test_data")
    object TestQuiz : Screen("test_quiz")
}
