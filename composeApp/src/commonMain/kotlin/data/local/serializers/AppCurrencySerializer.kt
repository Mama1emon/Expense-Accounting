package data.local.serializers

import androidx.datastore.core.okio.OkioSerializer
import example.AppCurrency
import example.CurrencyType
import okio.BufferedSink
import okio.BufferedSource
import okio.IOException

object AppCurrencySerializer : OkioSerializer<AppCurrency> {

    override val defaultValue: AppCurrency
        get() = AppCurrency(CurrencyType.CURRENCY_TYPE_UNSPECIFIED)

    override suspend fun readFrom(source: BufferedSource): AppCurrency {
        try {
            return AppCurrency.ADAPTER.decode(source)
        } catch (exception: IOException) {
            throw Exception(exception.message ?: "Serialization Exception")
        }
    }

    override suspend fun writeTo(t: AppCurrency, sink: BufferedSink) {
        sink.write(t.encode())
    }
}