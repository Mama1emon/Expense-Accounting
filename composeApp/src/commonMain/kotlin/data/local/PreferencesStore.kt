package data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import okio.Path

expect fun getPreferenceStore(): DataStore<Preferences>

fun createPreferencesStore(path: (fileName: String) -> Path): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = null,
        scope = CoroutineScope(context = SupervisorJob() + Dispatchers.IO),
        migrations = emptyList(),
        produceFile = { path("preferences.preferences_pb") }
    )
}

object PreferencesKeys {

    val SELECTED_MONTH by lazy { intPreferencesKey(name = "SELECTED_MONTH_KEY") }
}