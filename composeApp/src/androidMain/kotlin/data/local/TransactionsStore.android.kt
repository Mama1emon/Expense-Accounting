package data.local

actual fun getTransactionsStore() = createDataStore(::createTransactionsStore)