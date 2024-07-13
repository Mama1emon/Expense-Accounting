import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import presentation.ui.SummaryBottomSheet
import presentation.ui.TransactionDetailsBottomSheet
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
        var isTxDetailsExpanded by remember { mutableStateOf(false) }
        var isSummaryExpanded by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            var bottomOffset by remember { mutableStateOf(0) }
            val density = LocalDensity.current

            LazyColumn(
                contentPadding = PaddingValues(
                    bottom = with(density) {
                        val navigationBarPadding = WindowInsets.navigationBars
                            .asPaddingValues(density).calculateBottomPadding()

                        bottomOffset.toDp() + navigationBarPadding
                    },
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .fillMaxWidth()
                            .padding(
                                top = WindowInsets.statusBars.asPaddingValues()
                                    .calculateTopPadding().plus(24.dp),
                            )
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Expense Accounting",
                            style = MaterialTheme.typography.titleLarge
                        )

                        TransactionFilters(
                            categories = state.availableCategories,
                            onCategoryFilterClick = state.onFilterByCategoryClick,
                        )
                    }
                }

                items(
                    items = state.transactions,
                    key = Transaction::id,
                    itemContent = {
                        TransactionItem(
                            value = it,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateItemPlacement()
                        )
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
                    onClick = { isSummaryExpanded = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Summary")
                }

                FloatingActionButton(onClick = { isTxDetailsExpanded = true }) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                }
            }
        }



        if (isTxDetailsExpanded) {
            TransactionDetailsBottomSheet(
                availableCategories = state.availableCategories,
                onAddClick = state.onAddTransactionClick,
                onDismissRequest = { isTxDetailsExpanded = false }
            )
        }

        if (isSummaryExpanded) {
            SummaryBottomSheet(
                summaryState = state.summaryState,
                onDismissRequest = { isSummaryExpanded = false }
            )
        }
    }
}