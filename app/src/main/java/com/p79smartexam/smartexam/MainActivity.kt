package com.p79smartexam.smartexam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.p79smartexam.smartexam.navigation.SetupNavGraph
import com.p79smartexam.smartexam.ui.theme.SmartExamMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartExamMobileTheme {
                SetupNavGraph()
            }
        }
    }
}
