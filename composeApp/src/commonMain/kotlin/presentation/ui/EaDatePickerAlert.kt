package presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import network.chaintech.kmp_date_time_picker.ui.datepicker.SnappedDate
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelTextPicker
import network.chaintech.kmp_date_time_picker.utils.MAX
import network.chaintech.kmp_date_time_picker.utils.MIN
import network.chaintech.kmp_date_time_picker.utils.SelectorProperties
import network.chaintech.kmp_date_time_picker.utils.WheelPickerDefaults
import network.chaintech.kmp_date_time_picker.utils.Year
import network.chaintech.kmp_date_time_picker.utils.calculateDayOfMonths
import network.chaintech.kmp_date_time_picker.utils.now
import network.chaintech.kmp_date_time_picker.utils.shortMonths
import network.chaintech.kmp_date_time_picker.utils.withDayOfMonth
import network.chaintech.kmp_date_time_picker.utils.withMonth
import network.chaintech.kmp_date_time_picker.utils.withYear
import utils.getScreenWidth

@Composable
fun EaDatePickerAlert(
    onDismissRequest: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
) {
    val now = LocalDate.now()
    var selectedDate: LocalDate by remember { mutableStateOf(value = now) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedDate)
                    onDismissRequest()
                }
            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
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

@Composable
private fun WheelDatePicker(
    modifier: Modifier = Modifier,
    startDate: LocalDate = LocalDate.now(),
    minDate: LocalDate = LocalDate.MIN(),
    maxDate: LocalDate = LocalDate.MAX(),
    yearsRange: IntRange? = IntRange(1922, 2122),
    height: Dp = 128.dp,
    rowCount: Int = 3,
    showShortMonths: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    textColor: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onSnappedDate: (snappedDate: SnappedDate) -> Int? = { _ -> null }
) {
    var snappedDate by remember { mutableStateOf(startDate) }

    var dayOfMonths = calculateDayOfMonths(snappedDate.monthNumber, snappedDate.year).filter {
        it.value >= minDate.dayOfMonth && it.value <= maxDate.dayOfMonth
    }

    val months = (minDate.monthNumber..maxDate.monthNumber).map {
        network.chaintech.kmp_date_time_picker.utils.Month(
            text = if (showShortMonths) {
                shortMonths(it)
            } else Month(it).name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            value = it,
            index = it - 1
        )
    }

    val years = yearsRange?.map {
        Year(
            text = it.toString(),
            value = it,
            index = yearsRange.indexOf(it)
        )
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (selectorProperties.enabled().value) {
            HorizontalDivider(
                modifier = Modifier.padding(bottom = (height / rowCount)),
                thickness = (0.5).dp,
                color = selectorProperties.borderColor().value
            )
            HorizontalDivider(
                modifier = Modifier.padding(top = (height / rowCount)),
                thickness = (0.5).dp,
                color = selectorProperties.borderColor().value
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            WheelTextPicker(
                modifier = Modifier.width(120.dp),
                startIndex = months.find { it.value == startDate.monthNumber }?.index ?: 0,
                height = height,
                texts = months.map { it.text },
                rowCount = rowCount,
                style = textStyle,
                color = textColor,
                contentAlignment = Alignment.CenterStart,
            ) { snappedIndex ->

                val newMonth = months.find { it.index == snappedIndex }?.value

                newMonth?.let {
                    val newDate = snappedDate.withMonth(newMonth)

                    if (newDate.compareTo(minDate) >= 0 && newDate.compareTo(maxDate) <= 0) {
                        snappedDate = newDate
                    }

                    dayOfMonths =
                        calculateDayOfMonths(snappedDate.monthNumber, snappedDate.year)

                    val newIndex = months.find { it.value == snappedDate.monthNumber }?.index

                    newIndex?.let {
                        onSnappedDate(
                            SnappedDate.Month(
                                localDate = snappedDate,
                                index = newIndex
                            )
                        )?.let { return@WheelTextPicker it }
                    }
                }

                return@WheelTextPicker months.find { it.value == snappedDate.monthNumber }?.index
            }

            WheelTextPicker(
                modifier = Modifier.width(30.dp),
                startIndex = dayOfMonths.find { it.value == startDate.dayOfMonth }?.index ?: 0,
                height = height,
                texts = dayOfMonths.map { it.text },
                rowCount = rowCount,
                style = textStyle,
                color = textColor,
            ) { snappedIndex ->

                val newDayOfMonth = dayOfMonths.find { it.index == snappedIndex }?.value

                newDayOfMonth?.let {
                    val newDate = snappedDate.withDayOfMonth(newDayOfMonth)

                    if (newDate.compareTo(minDate) >= 0 && newDate.compareTo(maxDate) <= 0) {
                        snappedDate = newDate
                    }

                    val newIndex =
                        dayOfMonths.find { it.value == snappedDate.dayOfMonth }?.index

                    newIndex?.let {
                        onSnappedDate(
                            SnappedDate.DayOfMonth(
                                localDate = snappedDate,
                                index = newIndex
                            )
                        )?.let { return@WheelTextPicker it }
                    }
                }

                return@WheelTextPicker dayOfMonths.find { it.value == snappedDate.dayOfMonth }?.index
            }

            years?.let { years ->
                WheelTextPicker(
                    modifier = Modifier.width(60.dp),
                    startIndex = years.find { it.value == startDate.year }?.index ?: 0,
                    height = height,
                    texts = years.map { it.text },
                    rowCount = rowCount,
                    style = textStyle,
                    color = textColor,
                    contentAlignment = Alignment.CenterEnd,
                ) { snappedIndex ->

                    val newYear = years.find { it.index == snappedIndex }?.value

                    newYear?.let {

                        val newDate = snappedDate.withYear(newYear)

                        if (newDate.compareTo(minDate) >= 0 && newDate.compareTo(maxDate) <= 0) {
                            snappedDate = newDate
                        }

                        dayOfMonths =
                            calculateDayOfMonths(snappedDate.monthNumber, snappedDate.year)

                        val newIndex = years.find { it.value == snappedDate.year }?.index

                        newIndex?.let {
                            onSnappedDate(
                                SnappedDate.Year(
                                    localDate = snappedDate,
                                    index = newIndex
                                )
                            )?.let { return@WheelTextPicker it }

                        }
                    }

                    return@WheelTextPicker years.find { it.value == snappedDate.year }?.index
                }
            }
        }
    }
}