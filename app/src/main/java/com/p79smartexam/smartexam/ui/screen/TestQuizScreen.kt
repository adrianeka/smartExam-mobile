package com.p79smartexam.smartexam.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.p79smartexam.smartexam.database.SmartExamDb
import com.p79smartexam.smartexam.model.Soal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestQuizScreen(
    navController: NavHostController,
    viewModel: TestViewModel = viewModel(
        factory = TestViewModelFactory(
            SmartExamDb.getInstance(LocalContext.current).dao,
            LocalContext.current
        )
    )
) {
    val data by viewModel.data.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val saveStatus by viewModel.saveStatus.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Test Quiz", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = saveStatus,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Icon(
                        imageVector = if (isOnline) Icons.Filled.CloudDone else Icons.Filled.CloudOff,
                        contentDescription = "Connection Status",
                        tint = if (isOnline) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Connectivity Banner
            AnimatedVisibility(visible = !isOnline) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CloudOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Mode Offline: Progres Disimpan!",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(data, key = { it.id }) { soal ->
                    QuizItem(
                        soal = soal,
                        onAnswerChanged = { newAnswer ->
                            viewModel.updateJawaban(soal, newAnswer)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuizItem(
    soal: Soal,
    onAnswerChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Pertanyaan ${soal.id}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = soal.soal,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (soal.tipeSoal == 1) {
                val options = soal.pilihan.split(" ~ ")
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        RadioButton(
                            selected = (soal.jawaban == option),
                            onClick = { onAnswerChanged(option) }
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )
                    }
                }
            } else {
                OutlinedTextField(
                    value = soal.jawaban,
                    onValueChange = onAnswerChanged,
                    label = { Text("Jawaban Anda") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        }
    }
}
