package ru.okcode.currencyconverter.ui.overview

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.okcode.currencyconverter.data.model.Config
import ru.okcode.currencyconverter.data.model.Rates
import ru.okcode.currencyconverter.data.repository.CacheRepository
import ru.okcode.currencyconverter.data.repository.ConfigRepository
import ru.okcode.currencyconverter.data.repository.ReadyRepository

class OverviewViewModel @ViewModelInject constructor(
    private val cacheRepository: CacheRepository,
    private val configRepository: ConfigRepository,
    private val readyRepository: ReadyRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Coroutines scope
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    // Messages ---------------------------------------------------------------------
    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    //Cache data ---------------------------------------------------------------------
    private val cacheDataSource: LiveData<Rates>
        get() = cacheRepository.cacheDataSource

    private val cacheObserver: Observer<Rates> = Observer {
        updateReadyRates()
    }

    //Config data ---------------------------------------------------------------------
    private val configDataSource: LiveData<Config>
        get() = configRepository.configDataSource

    private val configObserver: Observer<Config> = Observer {
        updateReadyRates()
    }

    // ReadyRates
    val readyRatesDataSource: LiveData<Rates>
        get() = readyRepository.readyRatesDataSource

    // Getting data status
    val statusDataSource = cacheRepository.apiStatusDataSource

    init {
        startObserve()
    }

    override fun onCleared() {
        stopObserve()
        job.cancel()
        super.onCleared()
    }

    private fun startObserve() {
        cacheDataSource.observeForever(cacheObserver)
        configDataSource.observeForever(configObserver)
    }

    private fun stopObserve() {
        cacheDataSource.removeObserver(cacheObserver)
        configDataSource.removeObserver(configObserver)
    }

    private fun updateReadyRates() {
        scope.launch {
            val config: Config =
                configRepository.getConfigAsync().await() ?: Config.getDefaultConfig()

            val cachedRates: Rates? = cacheRepository.getCacheRatesAsync().await()

            if (cachedRates != null) {
                readyRepository.updateReadyRates(cachedRates, config)
            } else {
                cacheRepository.refreshCacheRates()
            }
        }
    }
}