package domain.appcurrency

import kotlinx.coroutines.flow.Flow

/**
 * @author Andrew Khokhlov on 15/07/2024
 */
interface AppCurrencyRepository {

    suspend fun getAppCurrency(): Flow<AppCurrency?>

    suspend fun saveAppCurrency(appCurrency: AppCurrency)
}