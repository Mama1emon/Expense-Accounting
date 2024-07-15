package di

import data.AppCurrencyRepositoryImpl
import data.TransactionsRepositoryImpl
import data.local.getAppCurrencyStore
import data.local.getTransactionsStore
import domain.AddTransactionUseCase
import domain.CalculateExpensesUseCase
import domain.GetAllTransactionsUseCase
import domain.TransactionsRepository
import domain.appcurrency.AppCurrencyRepository
import domain.appcurrency.ChangeAppCurrencyUseCase
import domain.appcurrency.GetAppCurrencyUseCase
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import presentation.MainViewModel

object Modules {

    val dataModule = module {
        single { TransactionsRepositoryImpl(transactionsStore = getTransactionsStore()) }
            .bind<TransactionsRepository>()

        single { AppCurrencyRepositoryImpl(appCurrencyStore = getAppCurrencyStore()) }
            .bind<AppCurrencyRepository>()
    }

    val domainModule = module {
        singleOf(::AddTransactionUseCase)
        singleOf(::CalculateExpensesUseCase)
        singleOf(::GetAllTransactionsUseCase)
        singleOf(::GetAppCurrencyUseCase)
        singleOf(::ChangeAppCurrencyUseCase)
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