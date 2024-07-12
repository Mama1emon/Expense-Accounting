import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.MainViewModel
import presentation.state.MainScreenState
import kotlin.random.Random

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    val state: MainScreenState = koinViewModel<MainViewModel>().uiState.collectAsState().value

    MaterialTheme {
        var isDialogExpanded by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Expense Accounting") },
                    actions = {
                        IconButton(onClick = { isDialogExpanded = true }) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                        }
                    },
                )
            }
        ) {
            LazyColumn {
                items(
                    items = state.transactions,
                    key = { it.id }
                ) {
                    Row(
                        modifier = Modifier.height(48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = it.name)

                        Text(text = it.amount.toString())
                    }
                }
            }
        }

        if (isDialogExpanded) {
            Dialog(
                onDismissRequest = { isDialogExpanded = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    usePlatformDefaultWidth = false,
                )
            ) {
                var value by remember { mutableStateOf("") }

                Column {
                    TextField(
                        value = value,
                        onValueChange = { value = it },
                        modifier = Modifier.height(48.dp),
                    )

                    Button(
                        onClick = {
                            isDialogExpanded = true
                            state.onAddTransactionClick(
                                value,
                                state.availableCategories.first(),
                                Random.nextDouble(),
                            )
                        },
                    ) {
                        Text(text = "Add")
                    }
                }
            }
        }
    }
}