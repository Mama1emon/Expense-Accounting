package presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import presentation.state.MainScreenState.Filter

@Composable
fun TransactionFilters(
    categories: ImmutableSet<String>,
    currencies: ImmutableSet<String>,
    onFilterClick: (Filter, String?) -> Unit
) {
    var filter: Filter? by remember { mutableStateOf(value = null) }

    var category: String? by remember { mutableStateOf(value = null) }
    var date: String? by remember { mutableStateOf(value = null) }
    var currency: String? by remember { mutableStateOf(value = null) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = category != null,
            label = category ?: "Category",
            onClick = {
                if (category != null) {
                    category = null
                    onFilterClick(Filter.Category, null)
                } else {
                    filter = Filter.Category
                }
            },
            modifier = Modifier.weight(1f)
        )

        FilterChip(
            selected = currency != null,
            label = currency ?: "Currency",
            onClick = {
                if (currency != null) {
                    currency = null
                    onFilterClick(Filter.Currency, null)
                } else {
                    filter = Filter.Currency
                }
            },
            modifier = Modifier.weight(1f)
        )

        FilterChip(
            selected = date != null,
            label = date ?: "Date",
            onClick = {
                TODO()
//                if (date != null) {
//                    date = null
//                    onFilterClick(Filter.Date, null)
//                } else {
//                    filter = Filter.Date
//                }
            },
            modifier = Modifier.weight(1f)
        )
    }

    SelectMenu(
        isExpanded = filter == Filter.Category || filter == Filter.Currency,
        items = when (filter) {
            Filter.Category -> categories
            Filter.Currency -> currencies
            else -> persistentSetOf()
        },
        onSelect = {
            when (filter) {
                Filter.Category -> category = it
                Filter.Currency -> currency = it
                else -> Unit
            }

            onFilterClick(filter!!, it)
            filter = null
        },
        onDismissRequest = { filter = null }
    )
}

@Composable
fun FilterChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            AnimatedContent(label) {
                Text(text = it)
            }
        },
        modifier = modifier.animateContentSize(),
        trailingIcon = {
            AnimatedContent(selected) {
                Icon(
                    imageVector = if (it) Icons.Outlined.Close else Icons.Outlined.ArrowDropDown,
                    contentDescription = null
                )
            }
        },
    )
}