package data.local

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.mama1emon.exac.Transactions
import data.local.serializers.TransactionsSerializer
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import okio.FileSystem
import okio.Path

internal const val DATA_STORE_FILE_NAME = "transactions.preferences_pb"

private lateinit var transactionsStore: DataStore<Transactions>

private val lock = SynchronizedObject()

expect fun getTransactionsStore(): DataStore<Transactions>

fun createDataStore(fileSystem: FileSystem, producePath: () -> Path): DataStore<Transactions> {
    return synchronized(lock) {
        if (::transactionsStore.isInitialized) {
            transactionsStore
        } else {
            DataStoreFactory.create(
                storage = OkioStorage(
                    fileSystem = fileSystem,
                    producePath = producePath,
                    serializer = TransactionsSerializer,
                ),
            )
                .also { transactionsStore = it }
        }
    }
}