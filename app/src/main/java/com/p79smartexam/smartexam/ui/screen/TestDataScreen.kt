package com.p79smartexam.smartexam.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.p79smartexam.smartexam.database.SmartExamDb
import com.p79smartexam.smartexam.model.Soal
import com.p79smartexam.smartexam.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestDataScreen(
    navController: NavHostController,
    viewModel: TestViewModel = viewModel(
        factory = TestViewModelFactory(
            SmartExamDb.getInstance(LocalContext.current).dao,
            LocalContext.current
        )
    )
) {
    val data by viewModel.data.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    var selectedSoal by remember { mutableStateOf<Soal?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Test DB Lokal")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.TestQuiz.route) }) {
                Icon(
                    imageVector = Icons.Filled.Quiz,
                    contentDescription = "Quiz",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            FilterRow(
                selectedFilter = currentFilter,
                onFilterSelected = { viewModel.setFilter(it) }
            )
            HorizontalDivider()
            ScreenContent(
                data = data,
                onSoalClick = { selectedSoal = it },
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (selectedSoal != null) {
        SoalDetailDialog(
            soal = selectedSoal!!,
            onDismiss = { selectedSoal = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterRow(
    selectedFilter: TestViewModel.Filter,
    onFilterSelected: (TestViewModel.Filter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TestViewModel.Filter.entries.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.name) }
            )
        }
    }
}

@Composable
fun ScreenContent(
    data: List<Soal>,
    onSoalClick: (Soal) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(data, key = { it.id }) { soal ->
            SoalItem(
                soal = soal,
                onClick = { onSoalClick(soal) }
            )
        }
    }
}

@Composable
fun SoalItem(
    soal: Soal,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "ID: ${soal.id} | Type: ${soal.tipeSoal}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = soal.soal,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            if (soal.jawaban.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Jawaban: ${soal.jawaban}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (soal.isSynced) "Synced" else "Not Synced",
                style = MaterialTheme.typography.labelMedium,
                color = if (soal.isSynced) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun SoalDetailDialog(
    soal: Soal,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Preview Data Lengkap")
        },
        text = {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "ID: ${soal.id}", fontWeight = FontWeight.Bold)
                Text(text = "Tipe Soal: ${if (soal.tipeSoal == 1) "Pilihan Ganda" else "Essay"}")
                Text(text = "Soal:", fontWeight = FontWeight.SemiBold)
                Text(text = soal.soal)
                
                if (soal.tipeSoal == 1 && soal.pilihan.isNotEmpty()) {
                    Text(text = "Pilihan Jawaban:", fontWeight = FontWeight.SemiBold)
                    soal.pilihan.split(" ~ ").forEach { pilihan ->
                        Text(text = "• $pilihan")
                    }
                }

                Text(text = "Jawaban:", fontWeight = FontWeight.SemiBold)
                Text(
                    text = soal.jawaban.ifEmpty { "(Belum Dijawab)" },
                    color = if (soal.jawaban.isEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )

                Text(text = "Status Sinkronisasi:", fontWeight = FontWeight.SemiBold)
                Text(text = if (soal.isSynced) "Sudah Sinkron" else "Belum Sinkron")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}
