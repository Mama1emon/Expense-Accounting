package domain.selectedmonth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import data.local.PreferencesKeys

/**
 * @author Andrew Khokhlov on 19/07/2024
 */
class ChangeSelectedMonthUseCase(
    private var preferencesStore: DataStore<Preferences>,
) {

    suspend operator fun invoke(selectedMonth: SelectedMonth) {
        preferencesStore.edit {
            it[PreferencesKeys.SELECTED_MONTH] = selectedMonth.number
        }
    }
}