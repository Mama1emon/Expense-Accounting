import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.Transaction
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.MainViewModel
import presentation.state.MainScreenState
import presentation.ui.TransactionDetailsDialog
import presentation.ui.TransactionFilters
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
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stickyHeader {
                    TransactionFilters(
                        categories = state.availableCategories,
                        onCategoryFilterClick = state.onFilterByCategoryClick,
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