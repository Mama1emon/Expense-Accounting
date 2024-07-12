package di

import data.TransactionsRepositoryImpl
import data.local.getTransactionsStore
import domain.AddTransactionUseCase
import domain.CalculateExpensesByCategoryUseCase
import domain.GetAllTransactionsUseCase
import domain.TransactionsRepository
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
    }

    val domainModule = module {
        singleOf(::AddTransactionUseCase)
        singleOf(::CalculateExpensesByCategoryUseCase)
        singleOf(::GetAllTransactionsUseCase)
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