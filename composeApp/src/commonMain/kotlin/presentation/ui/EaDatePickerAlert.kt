package presentation.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.ui.datepicker.DefaultWheelDatePicker
import network.chaintech.kmp_date_time_picker.utils.MAX
import network.chaintech.kmp_date_time_picker.utils.MIN
import network.chaintech.kmp_date_time_picker.utils.now
import utils.getScreenWidth

@Composable
fun EaDatePickerAlert(
    minDate: LocalDate = LocalDate.MIN(),
    maxDate: LocalDate = LocalDate.MAX(),
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
            DefaultWheelDatePicker(
                minDate = minDate,
                maxDate = maxDate,
                yearsRange = IntRange(start = minDate.year, endInclusive = maxDate.year),
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
