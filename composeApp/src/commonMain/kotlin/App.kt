import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                var bottomOffset by remember { mutableStateOf(0) }
                val density = LocalDensity.current

                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = with(density) { bottomOffset.toDp() },
                    ),
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

                Row(
                    modifier = Modifier
                        .onSizeChanged { bottomOffset = it.height }
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ExtendedFloatingActionButton(
                        onClick = { TODO() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Summary")
                    }

                    FloatingActionButton(onClick = { isDialogExpanded = true }) {
                        Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                    }
                }
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