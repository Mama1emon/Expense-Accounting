package presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableSet
import presentation.state.MainScreenState

/**
 * @author Andrew Khokhlov on 15/07/2024
 */
@Composable
fun TopBarWithChips(state: MainScreenState.TopBarState, params: MainScreenState.BaseParams) {
    var isMonthsDialogExpanded by remember { mutableStateOf(value = false) }
    var isGroupingDialogExpanded by remember { mutableStateOf(value = false) }
    var isCurrencyDialogExpanded by remember { mutableStateOf(value = false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 12.dp)
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(targetState = params.selectedMonth) {
                ClickableText(
                    text = buildAnnotatedString {
                        append("Expense")

                        if (it.isNotBlank()) {
                            withStyle(
                                style = SpanStyle(color = MaterialTheme.colorScheme.primary),
                            ) {
                                append(" for $it")
                            }
                        }
                    },
                    style = MaterialTheme.typography.titleLarge,
                    onClick = {
                        if (state.availableMonths.isNotEmpty() && it >= 8) {
                            isMonthsDialogExpanded = true
                        }
                    }
                )
            }

            Row {
                IconButton(onClick = { isGroupingDialogExpanded = true }) {
                    Icon(
                        imageVector = Icons.Outlined.GridView,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                IconButton(onClick = { isCurrencyDialogExpanded = true }) {
                    Icon(
                        imageVector = Icons.Rounded.CurrencyExchange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        TransactionFilters(state = state.transactionFiltersState)
    }

    if (isMonthsDialogExpanded) {
        SelectAlert(
            title = "Select month",
            select = params.selectedMonth,
            items = state.availableMonths,
            onSelect = state.onChangeMonthClick,
            onDismissRequest = { isMonthsDialogExpanded = false },
        )
    }

    if (isGroupingDialogExpanded) {
        SelectAlert(
            title = "Group by",
            select = params.groupBy.name,
            items = state.availableGroups.map(MainScreenState.Group::name).toImmutableSet(),
            onSelect = {
                state.onChangeGroupClick(MainScreenState.Group.valueOf(it))
            },
            onDismissRequest = { isGroupingDialogExpanded = false },
        )
    }

    if (isCurrencyDialogExpanded) {
        SelectAlert(
            title = "Select currency",
            select = params.appCurrency,
            items = params.availableAppCurrencies,
            onSelect = state.onChangeAppCurrencyClick,
            onDismissRequest = { isCurrencyDialogExpanded = false },
        )
    }
}

