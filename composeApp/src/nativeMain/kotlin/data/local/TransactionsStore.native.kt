package data.local

import androidx.datastore.core.DataStore
import com.mama1emon.exac.Transactions
import kotlinx.cinterop.ExperimentalForeignApi
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual fun getTransactionsStore(): DataStore<Transactions> {
    @OptIn(ExperimentalForeignApi::class)
    val producePath = {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )

        requireNotNull(documentDirectory).path + "/$DATA_STORE_FILE_NAME"
    }

    return createDataStore(fileSystem = FileSystem.SYSTEM, producePath = { producePath().toPath() })
}