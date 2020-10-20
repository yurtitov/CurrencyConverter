package ru.okcode.currencyconverter.ui.editcurrencylist

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.schedulers.Schedulers
import ru.okcode.currencyconverter.data.repository.ConfigRepository
import ru.okcode.currencyconverter.ui.editcurrencylist.EditCurrenciesListAction.*
import ru.okcode.currencyconverter.ui.editcurrencylist.EditCurrenciesListResult.*
import javax.inject.Inject

class EditCurrenciesListProcessorHolder @Inject constructor(
    private val configRepository: ConfigRepository
) {
    /**
     * actionProcessor
     */
    internal val actionProcessor:
            ObservableTransformer<EditCurrenciesListAction, EditCurrenciesListResult> =
        ObservableTransformer { actions ->
            actions.publish { shared ->
                Observable.merge(
                    shared.ofType(AddCurrencyAction::class.java)
                        .compose(addCurrencyProcessor),
                    shared.ofType(RemoveCurrencyAction::class.java)
                        .compose(removeCurrencyProcessor),
                    shared.ofType(MoveCurrencyAction::class.java)
                        .compose(moveCurrencyProcessor)
                )
                    .mergeWith(
                        shared.ofType(LoadCurrenciesFromConfigAction::class.java)
                            .compose(loadCurrenciesFromConfig)
                    )
                    .mergeWith(
                        shared.ofType(SaveCurrenciesToConfigAction::class.java)
                            .compose(saveCurrenciesToConfig)
                    )
                    .mergeWith(
                        shared.filter { action ->
                            action !is AddCurrencyAction
                                    && action !is RemoveCurrencyAction
                                    && action !is MoveCurrencyAction
                                    && action !is LoadCurrenciesFromConfigAction
                                    && action !is SaveCurrenciesToConfigAction
                        }.flatMap { action ->
                            Observable.error(
                                IllegalArgumentException("Unknown Action type: $action")
                            )
                        }
                    )
            }
        }

    private val loadCurrenciesFromConfig:
            ObservableTransformer<LoadCurrenciesFromConfigAction, LoadCurrenciesFromConfigResult> =
        ObservableTransformer { actions ->
            actions.flatMap {
                configRepository.getConfigSingle()
                    .toObservable()
                    .map { config ->
                        LoadCurrenciesFromConfigResult.Success(config.configuredCurrencies)
                    }
                    .cast(LoadCurrenciesFromConfigResult::class.java)
                    .onErrorReturn(LoadCurrenciesFromConfigResult::Failure)
                    .subscribeOn(Schedulers.io())
            }
        }

    private val saveCurrenciesToConfig:
            ObservableTransformer<SaveCurrenciesToConfigAction, SaveCurrenciesToConfigResult> =
        ObservableTransformer { actions ->
            actions.flatMap { action ->
                configRepository.getConfigSingle()
                    .toObservable()
                    .flatMap { config ->
                        val newConfig = config.copy(
                            configuredCurrencies = action.configuredCurrencies
                        )
                        configRepository.saveConfig(newConfig)
                            .andThen(Observable.just(SaveCurrenciesToConfigResult.Success))
                    }
                    .cast(SaveCurrenciesToConfigResult::class.java)
                    .onErrorReturn(SaveCurrenciesToConfigResult::Failure)
                    .subscribeOn(Schedulers.io())
            }
        }

    private val addCurrencyProcessor:
            ObservableTransformer<AddCurrencyAction, AddCurrencyResult> =
        ObservableTransformer { actions ->
            actions.map {action ->
                AddCurrencyResult.Success(action.configuredCurrencies)
            }
                .cast(AddCurrencyResult::class.java)
                .onErrorReturn(AddCurrencyResult::Failure)
        }

    private val moveCurrencyProcessor:
            ObservableTransformer<MoveCurrencyAction, MoveCurrencyResult> =
        ObservableTransformer { actions ->
            actions.map {action ->
                MoveCurrencyResult.Success(action.configuredCurrencies)
            }
                .cast(MoveCurrencyResult::class.java)
                .onErrorReturn(MoveCurrencyResult::Failure)
        }

    private val removeCurrencyProcessor:
            ObservableTransformer<RemoveCurrencyAction, RemoveCurrencyResult> =
        ObservableTransformer { actions ->
            actions.map {action ->
                RemoveCurrencyResult.Success(action.configuredCurrencies)
            }
                .cast(RemoveCurrencyResult::class.java)
                .onErrorReturn(RemoveCurrencyResult::Failure)
        }


}