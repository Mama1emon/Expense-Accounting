package data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

actual fun getPreferenceStore(): DataStore<Preferences> {
    return createPreferencesStore(path = ::producePath)
}