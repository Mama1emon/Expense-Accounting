package presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import network.chaintech.kmp_date_time_picker.utils.now
import presentation.state.MainScreenState.Filter
import utils.getScreenWidth

@Composable
fun TransactionFilters(
    categories: ImmutableSet<String>,
    currencies: ImmutableSet<String>,
    onFilterClick: (Filter, String?) -> Unit
) {
    var filter: Filter? by remember { mutableStateOf(value = null) }

    var category: String? by remember { mutableStateOf(value = null) }
    var date: LocalDate? by remember { mutableStateOf(value = null) }
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
            modifier = Modifier.weight(1f),
            enabled = categories.isNotEmpty()
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
            modifier = Modifier.weight(1f),
            enabled = currencies.isNotEmpty()
        )

        FilterChip(
            selected = date != null,
            label = date?.format(
                LocalDate.Format {
                    monthName(MonthNames.ENGLISH_ABBREVIATED)
                    char(' ')
                    dayOfMonth(padding = Padding.NONE)
                }
            ) ?: "Date",
            onClick = {
                if (date != null) {
                    date = null
                    onFilterClick(Filter.Date, null)
                } else {
                    filter = Filter.Date
                }
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


    if (filter == Filter.Date) {
        val now = LocalDate.now()
        var selectedDate: LocalDate by remember { mutableStateOf(value = now) }

        AlertDialog(
            onDismissRequest = { filter = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        date = selectedDate

                        onFilterClick(
                            filter!!,
                            selectedDate
                                .atTime(0, 0)
                                .toInstant(TimeZone.currentSystemDefault())
                                .toEpochMilliseconds()
                                .toString()
                        )

                        filter = null
                    }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { filter = null }) {
                    Text(text = "Cancel")
                }
            },
            text = {
                WheelDatePicker(
                    minDate = LocalDate(
                        year = now.year,
                        monthNumber = now.monthNumber,
                        dayOfMonth = 1,
                    ),
                    maxDate = now,
                    yearsRange = IntRange(start = now.year, endInclusive = now.year),
                    height = getScreenWidth() / 3,
                    rowCount = 5,
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onSnappedDate = {
                        selectedDate = it.snappedLocalDate

                        null
                    }
                )
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun FilterChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            AnimatedContent(label) {
                Text(text = it)
            }
        },
        enabled = enabled,
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