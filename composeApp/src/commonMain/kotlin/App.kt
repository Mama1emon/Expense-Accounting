import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import domain.ExpenseCategory
import domain.Transaction
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.MainViewModel
import presentation.state.MainScreenState
import presentation.ui.TransactionItem
import kotlin.random.Random

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
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
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { isDialogExpanded = true }) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = state.transactions,
                    key = Transaction::id,
                    itemContent = { TransactionItem(it) }
                )
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
                var name by remember { mutableStateOf("") }
                var amount by remember { mutableStateOf("") }
                var category: ExpenseCategory? by remember { mutableStateOf(null) }
                val focusManager = LocalFocusManager.current

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    TopAppBar(
                        title = { Text(text = "Transaction Details") },
                        navigationIcon = {
                            IconButton(onClick = { isDialogExpanded = false }) {
                                Icon(imageVector = Icons.Outlined.Close, contentDescription = null)
                            }
                        },
                        actions = {
                            TextButton(
                                onClick = {
                                    isDialogExpanded = false

                                    state.onAddTransactionClick(
                                        name,
                                        state.availableCategories.first(),
                                        Random.nextDouble(),
                                    )
                                },
                                enabled = name.isNotEmpty() && amount.isNotEmpty() &&
                                    category != null
                            ) {
                                Text(text = "Add")
                            }
                        }
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        label = { Text(text = "Name") },
                        placeholder = { Text(text = "Restaurant") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next,
                        ),
                        keyboardActions = KeyboardActions {
                            focusManager.moveFocus(focusDirection = FocusDirection.Down)
                        }
                    )

                    var isMenuExpanded by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        label = { Text(text = "Amount") },
                        placeholder = { Text(text = "1") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                        keyboardActions = KeyboardActions {
                            focusManager.moveFocus(focusDirection = FocusDirection.Down)
                            isMenuExpanded = true
                        }
                    )

                    OutlinedTextField(
                        value = category?.let { it::class.simpleName }.orEmpty(),
                        onValueChange = { },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        readOnly = true,
                        label = { Text(text = "Category") },
                        trailingIcon = {
                            IconButton(
                                onClick = { isMenuExpanded = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowDropDown,
                                    contentDescription = null
                                )
                            }
                        },
                        singleLine = true,
                    )

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        properties = PopupProperties(
                            focusable = true,
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            clippingEnabled = false
                        ),
                        content = {
                            state.availableCategories.forEach {
                                key(it) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(text = it::class.simpleName.orEmpty())
                                        },
                                        onClick = {
                                            category = it
                                            isMenuExpanded = false
                                            focusManager.clearFocus()
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}