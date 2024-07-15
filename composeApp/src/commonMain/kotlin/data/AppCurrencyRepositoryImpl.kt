package data

import androidx.datastore.core.DataStore
import data.converters.AppCurrencyConverter
import domain.appcurrency.AppCurrency
import domain.appcurrency.AppCurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import example.AppCurrency as ProtoAppCurrency

class AppCurrencyRepositoryImpl(
    private val appCurrencyStore: DataStore<ProtoAppCurrency>,
) : AppCurrencyRepository {

    override suspend fun getAppCurrency(): Flow<AppCurrency?> {
        return appCurrencyStore.data.map {
            AppCurrencyConverter.convert(protoAppCurrency = it)
        }
    }

    override suspend fun saveAppCurrency(appCurrency: AppCurrency) {
        appCurrencyStore.updateData {
            AppCurrencyConverter.convert(appCurrency = appCurrency)
        }
    }
}