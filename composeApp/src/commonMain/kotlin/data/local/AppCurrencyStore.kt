package data.local

import androidx.datastore.core.DataStore
import data.local.serializers.AppCurrencySerializer
import data.local.utils.createDataStore
import example.AppCurrency
import okio.FileSystem
import okio.Path

expect fun getAppCurrencyStore(): DataStore<AppCurrency>

fun createAppCurrencyStore(
    fileSystem: FileSystem,
    producePath: (String) -> Path,
): DataStore<AppCurrency> {
    return createDataStore(
        fileSystem = fileSystem,
        producePath = { producePath("app_currency.preferences_pb") },
        serializer = AppCurrencySerializer,
    )
}