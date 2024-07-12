import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import domain.ExpenseCategory
import domain.Transaction
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.MainViewModel
import presentation.state.MainScreenState
import presentation.ui.TransactionDetailsDialog
import presentation.ui.TransactionItem

@OptIn(
    KoinExperimentalAPI::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
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
            var isFilterExpanded by remember { mutableStateOf(false) }
            var isSelected by remember { mutableStateOf(false) }
            var filterCategory: ExpenseCategory? by remember { mutableStateOf(null) }

            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stickyHeader {
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) {
                                isSelected = false
                                state.onFilterByCategoryClick(null)
                            } else {
                                isFilterExpanded = true
                            }
                        },
                        label = {
                            AnimatedContent(isSelected) { animatedSelected ->
                                Text(
                                    text = if (animatedSelected) {
                                        filterCategory?.let { it::class.simpleName }.orEmpty()
                                    } else {
                                        "Category"
                                    }
                                )
                            }
                        },
                        modifier = Modifier.animateContentSize(),
                        trailingIcon = {
                            AnimatedContent(isSelected) {
                                Icon(
                                    imageVector = if (it) {
                                        Icons.Outlined.Close
                                    } else {
                                        Icons.Outlined.ArrowDropDown
                                    },
                                    contentDescription = null
                                )
                            }
                        },
                    )

                    DropdownMenu(
                        expanded = isFilterExpanded,
                        onDismissRequest = {
                            isFilterExpanded = false
                            isSelected = false
                        },
                        properties = PopupProperties(
                            focusable = false,
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            clippingEnabled = true
                        ),
                        content = {
                            state.availableCategories.forEach {
                                key(it) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(text = it::class.simpleName.orEmpty())
                                        },
                                        onClick = {
                                            filterCategory = it
                                            state.onFilterByCategoryClick(it)
                                            isSelected = true
                                            isFilterExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    )
                }

                items(
                    items = state.transactions,
                    key = Transaction::id,
                    itemContent = {
                        TransactionItem(value = it, modifier = Modifier.animateItemPlacement())
                    }
                )
            }

            if (isDialogExpanded) {
                TransactionDetailsDialog(
                    availableCategories = state.availableCategories,
                    onAddClick = state.onAddTransactionClick,
                    onDismissRequest = { isDialogExpanded = false }
                )
            }
        }
    }
}