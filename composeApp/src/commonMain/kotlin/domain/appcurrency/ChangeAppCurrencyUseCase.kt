package domain.appcurrency

/**
 * @author Andrew Khokhlov on 15/07/2024
 */
class ChangeAppCurrencyUseCase(
    private val appCurrencyRepository: AppCurrencyRepository,
) {

    suspend operator fun invoke(appCurrency: AppCurrency) {
        return appCurrencyRepository.saveAppCurrency(appCurrency)
    }
}