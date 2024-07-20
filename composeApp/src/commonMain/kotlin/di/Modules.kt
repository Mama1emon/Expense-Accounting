package di

import data.AppCurrencyRepositoryImpl
import data.MockRateRepository
import data.TransactionsRepositoryImpl
import data.local.getAppCurrencyStore
import data.local.getPreferenceStore
import data.local.getTransactionsStore
import domain.AddTransactionUseCase
import domain.CalculateExpensesUseCase
import domain.ConvertCurrencyUseCase
import domain.GetAllTransactionsUseCase
import domain.TransactionsRepository
import domain.appcurrency.AppCurrencyRepository
import domain.appcurrency.ChangeAppCurrencyUseCase
import domain.appcurrency.GetAppCurrencyUseCase
import domain.rate.RateRepository
import domain.selectedmonth.ChangeSelectedMonthUseCase
import domain.selectedmonth.GetSelectedMonthUseCase
import domain.transactions.ChangeTransactionUseCase
import domain.transactions.DeleteTransactionUseCase
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import presentation.MainViewModel

object Modules {

    val dataModule = module {
        single { getPreferenceStore() }

        single {
            TransactionsRepositoryImpl(
                transactionsStore = getTransactionsStore(),
                preferencesDataStore = get(),
            )
        }
            .bind<TransactionsRepository>()

        single { AppCurrencyRepositoryImpl(appCurrencyStore = getAppCurrencyStore()) }
            .bind<AppCurrencyRepository>()

        singleOf(::MockRateRepository).bind<RateRepository>()
    }

    val domainModule = module {
        singleOf(::AddTransactionUseCase)
        singleOf(::CalculateExpensesUseCase)
        singleOf(::GetAllTransactionsUseCase)
        singleOf(::GetAppCurrencyUseCase)
        singleOf(::ChangeAppCurrencyUseCase)
        singleOf(::ConvertCurrencyUseCase)
        singleOf(::ChangeTransactionUseCase)
        singleOf(::DeleteTransactionUseCase)
        singleOf(::ChangeSelectedMonthUseCase)
        singleOf(::GetSelectedMonthUseCase)
    }

    val presentationModule = module {
        viewModelOf(::MainViewModel)
    }
}

fun initKoin(
): KoinApplication = startKoin {
    modules(
        Modules.dataModule,
        Modules.domainModule,
        Modules.presentationModule
    )
}