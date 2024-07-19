package data.local

import androidx.datastore.core.DataStore
import kotlinx.cinterop.ExperimentalForeignApi
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal fun <T> createDataStore(
    create: (FileSystem, (String) -> Path) -> DataStore<T>,
): DataStore<T> {
    return create(FileSystem.SYSTEM, ::producePath)
}

@OptIn(ExperimentalForeignApi::class)
internal fun producePath(fileName: String): Path {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )!!

    return "${documentDirectory.path}/$fileName".toPath()
}