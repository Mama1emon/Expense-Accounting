package presentation.formatters

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

/**
 * @author Andrew Khokhlov on 17/07/2024
 */
object DateFormatter {

    private val format = LocalDateTime.Format {
        monthName(MonthNames.ENGLISH_ABBREVIATED)

        char(' ')

        dayOfMonth(padding = Padding.NONE)
    }

    fun format(timestamp: Long): String {
        val now = Clock.System.now()
        val past = Instant.fromEpochMilliseconds(timestamp)

        val difference = now - past

        return when (difference.inWholeDays) {
            0L -> "Today"
            1L -> "Yesterday"
            else -> format.format(past.toLocalDateTime(TimeZone.currentSystemDefault()))
        }
    }
}