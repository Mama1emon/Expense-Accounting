package domain.selectedmonth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import data.local.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * @author Andrew Khokhlov on 19/07/2024
 */
class GetSelectedMonthUseCase(
    private var preferencesStore: DataStore<Preferences>,
) {

    operator fun invoke(): Flow<Int> {
        return preferencesStore.data.map { it[PreferencesKeys.SELECTED_MONTH] ?: 1 }
    }
}