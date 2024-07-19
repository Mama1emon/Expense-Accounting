package presentation.formatters

import domain.appcurrency.AppCurrency
import presentation.converters.symbol

/**
 * @author Andrew Khokhlov on 16/07/2024
 */
object AmountFormatter {

    fun formatAmount(value: Double, currency: AppCurrency): String {
        val stringValue = value.toString()

        val indexOfDot = stringValue.indexOfFirst { it == '.' }

        val amount = if (indexOfDot == -1) {
            stringValue
        } else {
            val lastIndex = if (indexOfDot + 3 > stringValue.length) {
                stringValue.length
            } else {
                indexOfDot + 3
            }

            stringValue.substring(0, lastIndex)
        }

        return "$amount ${currency.symbol}"
    }
}