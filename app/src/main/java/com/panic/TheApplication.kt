package com.panic

import android.app.Application
import androidx.room.Room
import com.module.database.AlarmDatabase
import com.module.database.DatabaseAlarmRepositoryImpl
import com.module.domain.usecases.GetAlarmsUseCase
import com.module.domain.repositories.DatabaseAlarmRepository
import com.module.domain.usecases.AddAlarmUseCase
import com.panic.ui.history.HistoryViewModel
import com.panic.ui.home.HomeViewModel
import com.panic.ui.home.pages.responders.RespondersViewModel
import com.panic.ui.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level
import org.koin.dsl.module

class TheApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //region Setup Koin
        GlobalContext.startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@TheApplication)
            modules(
                databaseModule,
                viewModelsModule,
                useCasesModule
            )
        }
        //endregion
    }

    private val databaseModule = module {

        // single instance of HelloRepository
        single {
            Room.databaseBuilder(
                get(),
                AlarmDatabase::class.java,
                AlarmDatabase.DATABASE_NAME
            ).build()
        }

        single<DatabaseAlarmRepository> { DatabaseAlarmRepositoryImpl(alarmDatabase = get()) }
    }

    private val viewModelsModule = module {
        viewModel { HomeViewModel() }
        viewModel { RespondersViewModel(application = get()) }
        viewModel { MainViewModel(application = get(), addAlarmUseCase = get()) }
        viewModel { HistoryViewModel(application = get(), getAlarmsUseCase = get()) }
    }

    private val useCasesModule = module {
        single { GetAlarmsUseCase(databaseAlarmRepository = get()) }
        single { AddAlarmUseCase(databaseAlarmRepository = get()) }
    }

}