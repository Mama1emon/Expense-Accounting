package presentation.formatters

import domain.appcurrency.AppCurrency

/**
 * @author Andrew Khokhlov on 16/07/2024
 */
object AmountFormatter {

    fun formatAmount(value: Double, currency: AppCurrency): String {
        val currencySymbol = when (currency) {
            AppCurrency.Dollar -> "$"
            AppCurrency.Euro -> "€"
            AppCurrency.Ruble -> "₽"
            AppCurrency.IndonesianRupiah -> "Rp"
        }

        return "$value $currencySymbol"
    }
}