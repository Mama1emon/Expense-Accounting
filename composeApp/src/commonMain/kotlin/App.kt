import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.MainViewModel
import presentation.state.MainScreenState
import presentation.ui.*

@OptIn(KoinExperimentalAPI::class, ExperimentalFoundationApi::class)
@Composable
@Preview
fun App() {
    val state: MainScreenState = koinViewModel<MainViewModel>().uiState.collectAsState().value

    MaterialTheme {
        var isTxDetailsExpanded by remember { mutableStateOf(false) }
        var txDetails: MainScreenState.TransactionItemState.Transaction?
            by remember { mutableStateOf(null) }

        var isSummaryExpanded by remember { mutableStateOf(false) }

        val snackbarHostState = remember { SnackbarHostState() }

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
                stickyHeader { TopBarWithChips(state = state.topBarState, params = state.params) }

                itemsIndexed(
                    items = state.transactions,
                    key = { _, item ->
                        when (item) {
                            is MainScreenState.TransactionItemState.Title -> item.value
                            is MainScreenState.TransactionItemState.Transaction -> item.id
                        }
                    },
                    contentType = { _, item ->
                        when (item) {
                            is MainScreenState.TransactionItemState.Title -> "Title"
                            is MainScreenState.TransactionItemState.Transaction -> "Transaction"
                        }
                    },
                    itemContent = { index, item ->
                        when (item) {
                            is MainScreenState.TransactionItemState.Title -> {
                                AnimatedContent(targetState = item.value) {
                                    Text(
                                        text = it,
                                        modifier = transactionItemModifier(
                                            index = index,
                                            lastIndex = state.transactions.lastIndex
                                        )
                                            .padding(horizontal = 14.dp)
                                            .padding(top = 14.dp),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }

                            is MainScreenState.TransactionItemState.Transaction -> {
                                TransactionItem(
                                    modifier = transactionItemModifier(
                                        index = index,
                                        lastIndex = state.transactions.lastIndex
                                    ),
                                    state = item,
                                    onClick = {
                                        txDetails = item
                                        isTxDetailsExpanded = true
                                    }
                                )
                            }
                        }
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

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        val coroutineScope = rememberCoroutineScope()
        if (isTxDetailsExpanded) {
            TransactionDetailsBottomSheet(
                state = state.transactionDetailsState,
                params = state.params,
                transaction = txDetails,
                onSnackbarShow = { visuals, onClick ->
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(visuals)

                        onClick(result)
                    }
                },
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
@ReadOnlyComposable
fun LazyItemScope.transactionItemModifier(index: Int, lastIndex: Int): Modifier {
    return Modifier
        .fillMaxWidth()
        .animateItemPlacement()
        .roundedShapeItemDecoration(currentIndex = index, lastIndex = lastIndex)
        .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
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