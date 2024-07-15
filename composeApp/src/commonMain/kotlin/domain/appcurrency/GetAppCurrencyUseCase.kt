package domain.appcurrency

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * @author Andrew Khokhlov on 15/07/2024
 */
class GetAppCurrencyUseCase(
    private val appCurrencyRepository: AppCurrencyRepository,
) {

    operator fun invoke(): Flow<AppCurrency> {
        return appCurrencyRepository.getAppCurrency().map { it ?: AppCurrency.Dollar }
    }
}