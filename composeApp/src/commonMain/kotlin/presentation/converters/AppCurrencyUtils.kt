package presentation.converters

import domain.appcurrency.AppCurrency

object AppCurrencyUtils {

    fun valueOf(value: String): AppCurrency {
        return when (value) {
            "Dollar" -> AppCurrency.Dollar
            "Euro" -> AppCurrency.Euro
            "Indonesian Rupiah" -> AppCurrency.IndonesianRupiah
            "Ruble" -> AppCurrency.Ruble
            else -> error("Unknown AppCurrency: $value")
        }
    }
}

val AppCurrency.name: String
    get() = when (this) {
        AppCurrency.Dollar -> "Dollar"
        AppCurrency.Euro -> "Euro"
        AppCurrency.IndonesianRupiah -> "Indonesian Rupiah"
        AppCurrency.Ruble -> "Ruble"
    }