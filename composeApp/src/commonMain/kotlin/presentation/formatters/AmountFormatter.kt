package presentation.formatters

import domain.appcurrency.AppCurrency

/**
 * @author Andrew Khokhlov on 16/07/2024
 */
object AmountFormatter {

    fun formatAmount(value: Double, currency: AppCurrency): String {
        val value = value.toString()

        val currencySymbol = when (currency) {
            AppCurrency.Dollar -> "$"
            AppCurrency.Euro -> "€"
            AppCurrency.Ruble -> "₽"
            AppCurrency.IndonesianRupiah -> "Rp"
        }

        val indexOfDot = value.indexOfFirst { it == '.' }

        val amount = if (indexOfDot == -1) {
            value
        } else {
            val lastIndex = if (indexOfDot + 3 > value.length) {
                value.length
            } else {
                indexOfDot + 3
            }

            value.substring(0, lastIndex)
        }

        return "$amount $currencySymbol"
    }
}