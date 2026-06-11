package com.p79smartexam.smartexam.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.p79smartexam.smartexam.model.Soal
import com.p79smartexam.smartexam.ui.viewmodel.TestViewModel
import com.p79smartexam.smartexam.ui.viewmodel.TestViewModelFactory
import com.p79smartexam.smartexam.ui.viewmodel.Filter
import android.content.Intent
import androidx.core.content.ContextCompat
import com.p79smartexam.smartexam.SmartExamApplication
import com.p79smartexam.smartexam.util.SubmitJawabanService
import androidx.compose.ui.res.stringResource
import com.p79smartexam.smartexam.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestDataScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val app = context.applicationContext as SmartExamApplication
    
    val startSubmitService = {
        val intent = Intent(context, SubmitJawabanService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    val viewModel: TestViewModel = viewModel(
        factory = TestViewModelFactory(
            app.container.soalRepository,
            app.container.networkObserver,
            startSubmitService
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val data = uiState.data
    val currentFilter = uiState.currentFilter
    val isLoading = uiState.isLoading
    var selectedSoal by remember { mutableStateOf<Soal?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.title_data_db_lokal))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize()
        ) {
            FilterRow(
                selectedFilter = currentFilter,
                onFilterSelected = { viewModel.setFilter(it) }
            )
            HorizontalDivider()
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (data.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.no_data),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                ScreenContent(
                    data = data,
                    onSoalClick = { selectedSoal = it },
                    modifier = Modifier.weight(1f)
                )
            }
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
    selectedFilter: Filter,
    onFilterSelected: (Filter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Filter.entries.forEach { filter ->
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
        modifier = modifier.fillMaxSize()
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
                text = stringResource(id = R.string.format_id_type, soal.id, soal.tipeSoal),
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
                    text = stringResource(id = R.string.format_jawaban, soal.jawaban),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (soal.isSynced) stringResource(id = R.string.status_synced) else stringResource(id = R.string.status_not_synced),
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
            Text(text = stringResource(id = R.string.preview_data_lengkap))
        },
        text = {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = stringResource(id = R.string.format_id, soal.id), fontWeight = FontWeight.Bold)
                val tipe = if (soal.tipeSoal == 1) stringResource(id = R.string.pilihan_ganda) else stringResource(id = R.string.essay)
                Text(text = stringResource(id = R.string.format_tipe_soal, tipe))
                Text(text = stringResource(id = R.string.label_soal), fontWeight = FontWeight.SemiBold)
                Text(text = soal.soal)
                
                if (soal.tipeSoal == 1 && soal.pilihan.isNotEmpty()) {
                    Text(text = stringResource(id = R.string.label_pilihan_jawaban), fontWeight = FontWeight.SemiBold)
                    soal.pilihan.split(" ~ ").forEach { pilihan ->
                        Text(text = "• $pilihan")
                    }
                }

                Text(text = stringResource(id = R.string.label_jawaban), fontWeight = FontWeight.SemiBold)
                val emptyAns = stringResource(id = R.string.belum_dijawab)
                Text(
                    text = soal.jawaban.ifEmpty { emptyAns },
                    color = if (soal.jawaban.isEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )

                Text(text = stringResource(id = R.string.label_status_sinkronisasi), fontWeight = FontWeight.SemiBold)
                Text(text = if (soal.isSynced) stringResource(id = R.string.sudah_sinkron) else stringResource(id = R.string.belum_sinkron))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.btn_tutup))
            }
        }
    )
}
