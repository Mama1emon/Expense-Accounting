package data.local

actual fun getAppCurrencyStore() = createDataStore(::createAppCurrencyStore)