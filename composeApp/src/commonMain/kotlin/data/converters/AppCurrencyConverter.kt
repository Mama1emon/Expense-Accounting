package data.converters

import domain.appcurrency.AppCurrency
import example.CurrencyType
import example.AppCurrency as ProtoAppCurrency

/**
 * @author Andrew Khokhlov on 15/07/2024
 */
object AppCurrencyConverter {

    fun convert(protoAppCurrency: ProtoAppCurrency): AppCurrency? {
        return when (protoAppCurrency.type) {
            CurrencyType.DOLLAR -> AppCurrency.Dollar
            CurrencyType.EURO -> AppCurrency.Euro
            CurrencyType.RUBLE -> AppCurrency.Ruble
            CurrencyType.INDONESIAN_RUPIAH -> AppCurrency.IndonesianRupiah
            CurrencyType.CURRENCY_TYPE_UNSPECIFIED -> null
        }
    }

    fun convert(appCurrency: AppCurrency): ProtoAppCurrency {
        return when (appCurrency) {
            AppCurrency.Dollar -> ProtoAppCurrency(CurrencyType.DOLLAR)
            AppCurrency.Euro -> ProtoAppCurrency(CurrencyType.EURO)
            AppCurrency.Ruble -> ProtoAppCurrency(CurrencyType.RUBLE)
            AppCurrency.IndonesianRupiah -> ProtoAppCurrency(CurrencyType.INDONESIAN_RUPIAH)
        }
    }
}