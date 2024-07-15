package data

import domain.appcurrency.AppCurrency
import domain.rate.RateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class MockRateRepository : RateRepository {

    override suspend fun getRate(from: AppCurrency, to: AppCurrency): Float {
        return withContext(Dispatchers.IO) {
            when (from) {
                AppCurrency.Dollar -> dollarRates[to]!!
                AppCurrency.Euro -> euroRates[to]!!
                AppCurrency.Ruble -> rubleRates[to]!!
                AppCurrency.IndonesianRupiah -> indonesianRupiahRates[to]!!
            }
        }
    }

    private companion object {

        val dollarRates: Map<AppCurrency, Float> = mapOf(
            AppCurrency.Euro to 0.92f,
            AppCurrency.Ruble to 88.35f,
            AppCurrency.IndonesianRupiah to 16_178f,
        )

        val euroRates: Map<AppCurrency, Float> = mapOf(
            AppCurrency.Dollar to 1.09f,
            AppCurrency.Ruble to 96.39f,
            AppCurrency.IndonesianRupiah to 17_651f,
        )

        val rubleRates: Map<AppCurrency, Float> = mapOf(
            AppCurrency.Dollar to 0.011f,
            AppCurrency.Euro to 0.010f,
            AppCurrency.IndonesianRupiah to 0.95f,
        )

        val indonesianRupiahRates: Map<AppCurrency, Float> = mapOf(
            AppCurrency.Dollar to 0.012f,
            AppCurrency.Euro to 0.011f,
            AppCurrency.Ruble to 1.06f,
        )
    }
}