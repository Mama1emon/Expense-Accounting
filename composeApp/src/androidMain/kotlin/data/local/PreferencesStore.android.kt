package data.local

actual fun getPreferenceStore() = createPreferencesStore(path = ::producePath)