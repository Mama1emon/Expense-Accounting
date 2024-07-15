package data.converters

import com.mama1emon.exac.CurrencyType
import domain.appcurrency.AppCurrency

/**
 * @author Andrew Khokhlov on 15/07/2024
 */
object CurrencyTypeConverter {

    fun convert(currencyType: CurrencyType): AppCurrency? {
        return when (currencyType) {
            CurrencyType.DOLLAR -> AppCurrency.Dollar
            CurrencyType.EURO -> AppCurrency.Euro
            CurrencyType.RUBLE -> AppCurrency.Ruble
            CurrencyType.INDONESIAN_RUPIAH -> AppCurrency.IndonesianRupiah
            CurrencyType.CURRENCY_TYPE_UNSPECIFIED -> null
        }
    }

    fun convert(appCurrency: AppCurrency): CurrencyType {
        return when (appCurrency) {
            AppCurrency.Dollar -> CurrencyType.DOLLAR
            AppCurrency.Euro -> CurrencyType.EURO
            AppCurrency.Ruble -> CurrencyType.RUBLE
            AppCurrency.IndonesianRupiah -> CurrencyType.INDONESIAN_RUPIAH
        }
    }
}