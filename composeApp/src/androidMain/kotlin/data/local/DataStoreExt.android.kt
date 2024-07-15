package data.local

import androidx.datastore.core.DataStore
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import utils.AndroidPlatformContextProvider

internal fun <T> createDataStore(
    create: (FileSystem, (String) -> Path) -> DataStore<T>,
): DataStore<T> {
    return create(FileSystem.SYSTEM, ::producePath)
}

private fun producePath(fileName: String): Path {
    return AndroidPlatformContextProvider.context.filesDir.resolve(fileName).absolutePath.toPath()
}