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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.MainViewModel
import presentation.state.MainScreenState
import presentation.state.MainScreenState.TransactionState
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
        var txDetails: TransactionState? by remember { mutableStateOf(null) }

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
            ) {
                stickyHeader {
                    TopBarWithChips(
                        state = state.topBarState,
                        appCurrency = state.appCurrency
                    )
                }

                itemsIndexed(
                    items = state.transactions,
                    key = { _, item -> item.id },
                    itemContent = { index, item ->
                        TransactionItem(
                            modifier = Modifier
                                .animateItemPlacement()
                                .roundedShapeItemDecoration(
                                    currentIndex = index,
                                    lastIndex = state.transactions.lastIndex,
                                ),
                            state = item,
                            onClick = {
                                txDetails = item
                                isTxDetailsExpanded = true
                            }
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
                transaction = txDetails,
                onDismissRequest = {
                    txDetails = null
                    isTxDetailsExpanded = false
                }
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

fun Modifier.roundedShapeItemDecoration(
    currentIndex: Int,
    lastIndex: Int,
): Modifier = composed {
    val modifier = this.padding(horizontal = 14.dp)
    val isSingleItem = currentIndex == 0 && lastIndex == 0

    when {
        isSingleItem -> {
            modifier
                .clip(shape = RoundedCornerShape(16.dp))
        }

        currentIndex == 0 -> {
            modifier
                .clip(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                    ),
                )
        }

        currentIndex == lastIndex -> {
            modifier
                .clip(
                    shape = RoundedCornerShape(
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp,
                    ),
                )
        }

        else -> modifier
    }
}