package data.local

import androidx.datastore.core.DataStore
import com.mama1emon.exac.Transactions
import okio.FileSystem
import okio.Path.Companion.toPath
import utils.AndroidPlatformContextProvider

actual fun getTransactionsStore(): DataStore<Transactions> {
    val context = requireNotNull(AndroidPlatformContextProvider.context)
    val producePath = { context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath.toPath() }

    return createDataStore(fileSystem = FileSystem.SYSTEM, producePath = producePath)
}