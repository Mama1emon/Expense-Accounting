package data.local

import androidx.datastore.core.DataStore
import com.mama1emon.exac.Transactions
import data.local.serializers.TransactionsSerializer
import data.local.utils.createDataStore
import okio.FileSystem
import okio.Path

expect fun getTransactionsStore(): DataStore<Transactions>

fun createTransactionsStore(
    fileSystem: FileSystem,
    producePath: (fileName: String) -> Path,
): DataStore<Transactions> {
    return createDataStore(
        fileSystem = fileSystem,
        producePath = { producePath("transactions.preferences_pb") },
        serializer = TransactionsSerializer,
    )
}