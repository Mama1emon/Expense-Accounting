package domain

import domain.appcurrency.AppCurrency
import domain.rate.RateRepository

/**
 * @author Andrew Khokhlov on 16/07/2024
 */
class ConvertCurrencyUseCase(
    private val rateRepository: RateRepository,
) {

    suspend operator fun invoke(amount: Amount, to: AppCurrency): Amount {
        if (amount.currency == to) return amount

        val rate = rateRepository.getRate(from = amount.currency, to = to)

        return Amount(value = amount.value * rate, currency = to)
    }
}