package domain.rate

import domain.appcurrency.AppCurrency

/**
 * @author Andrew Khokhlov on 15/07/2024
 */
interface RateRepository {

    suspend fun getRate(from: AppCurrency, to: AppCurrency): Float
}