package com.adriano.gfucoroutines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.adriano.gfucoroutines.mvi.ExerciseIntent
import com.adriano.gfucoroutines.mvi.ExerciseState
import com.adriano.gfucoroutines.mvi.ExerciseViewModel
import com.adriano.gfucoroutines.mvi.solution.SolutionViewModel

class MainActivity : ComponentActivity() {
    
    // NOTE: Swap this with `SolutionViewModel` to see the working solutions!
    private val viewModel: ExerciseViewModel by viewModels()
    // private val viewModel: SolutionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExerciseScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun ExerciseScreen(viewModel: ExerciseViewModel) {
// @Composable
// fun ExerciseScreen(viewModel: SolutionViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Coroutines MVI Workshop",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Status Display Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (val currentState = state) {
                    is ExerciseState.Idle -> Text("Ready to start exercises.")
                    is ExerciseState.Loading -> CircularProgressIndicator()
                    is ExerciseState.Success -> Text(
                        text = currentState.message,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                    is ExerciseState.Error -> Text(
                        text = "Error: \${currentState.message}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // Exercise Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Modul 2: Grundlagen", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
            Button(
                onClick = { viewModel.processIntent(ExerciseIntent.LoadUserDataIntent) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("1. async/await")
            }
            Button(
                onClick = { viewModel.processIntent(ExerciseIntent.CancelOngoingWorkIntent) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("2. Job Cancellation")
            }
            Button(
                onClick = { viewModel.processIntent(ExerciseIntent.ImplicitWaitIntent) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("3. Implicit Wait (coroutineScope)")
            }
            Button(
                onClick = { viewModel.processIntent(ExerciseIntent.ExplicitWaitIntent) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("4. Explicit Wait (job.join)")
            }
            Button(
                onClick = { viewModel.processIntent(ExerciseIntent.RefactorCallbackIntent) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("5. Refactor Callback (suspendCancellable...)")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Modul 3: Context & Dispatchers", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
            
            Button(
                onClick = { viewModel.processIntent(ExerciseIntent.LoadDatabaseAndNetworkIntent) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("6. withContext (IO)")
            }
            Button(
                onClick = { viewModel.processIntent(ExerciseIntent.CustomScopeCancellationIntent) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("7. Custom Scope Cancellation")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Modul 4: Fehlerbehandlung & State", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))

            Button(
                onClick = { viewModel.processIntent(ExerciseIntent.IncrementCounterIntent) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("8. Shared Mutable State (Mutex)")
            }
            Button(
                onClick = { viewModel.processIntent(ExerciseIntent.LoadRiskyDataIntent) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("9. Local Exception Handling (supervisorScope)")
            }
            Button(
                onClick = { viewModel.processIntent(ExerciseIntent.GlobalExceptionHandlingIntent) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("10. Global Exception Handling (CEH)")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Modul 5: Asynchronous Flow", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))

            var searchQuery by remember { mutableStateOf("") }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.processIntent(ExerciseIntent.SearchQueryChangedIntent(it))
                },
                label = { Text("11. Search (flatMapLatest)") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )

            Button(
                onClick = { viewModel.processIntent(ExerciseIntent.FlowProcessingPipelineIntent) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("12. Flow Processing (flatMapMerge)")
            }
        }
    }
}