import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
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
import presentation.ui.TopBarWithChips
import presentation.ui.TransactionDetailsBottomSheet
import presentation.ui.TransactionItem

@OptIn(KoinExperimentalAPI::class, ExperimentalFoundationApi::class)
@Composable
@Preview
fun App() {
    val state: MainScreenState = koinViewModel<MainViewModel>().uiState.collectAsState().value

    MaterialTheme {
        var isTxDetailsExpanded by remember { mutableStateOf(false) }
        var isSummaryExpanded by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            var bottomOffset by remember { mutableStateOf(0) }
            val density = LocalDensity.current

            LazyColumn(
                contentPadding = PaddingValues(
                    bottom = with(density) { bottomOffset.toDp() },
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stickyHeader {
                    TopBarWithChips(
                        state = state.topBarState,
                        appCurrency = state.appCurrency
                    )
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
                    .padding(all = 16.dp)
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
                state = state.transactionDetailsState,
                currency = state.appCurrency,
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