package data

import androidx.datastore.core.DataStore
import data.converters.CurrencyTypeConverter
import domain.appcurrency.AppCurrency
import domain.appcurrency.AppCurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.mama1emon.exac.AppCurrency as ProtoAppCurrency

class AppCurrencyRepositoryImpl(
    private val appCurrencyStore: DataStore<ProtoAppCurrency>,
) : AppCurrencyRepository {

    override fun getAppCurrency(): Flow<AppCurrency?> {
        return appCurrencyStore.data.map { CurrencyTypeConverter.convert(it.type) }
    }

    override suspend fun saveAppCurrency(appCurrency: AppCurrency) {
        appCurrencyStore.updateData {
            CurrencyTypeConverter.convert(appCurrency = appCurrency).let(::ProtoAppCurrency)
        }
    }
}