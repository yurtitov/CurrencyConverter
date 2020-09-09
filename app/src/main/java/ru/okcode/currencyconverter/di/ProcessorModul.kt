package ru.okcode.currencyconverter.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import ru.okcode.currencyconverter.model.processor.TextProcessor
import ru.okcode.currencyconverter.model.processor.TextProcessorImpl

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ProcessorModule {

    @Binds
    @ActivityRetainedScoped
    abstract fun bindTextProcessor(impl: TextProcessorImpl): TextProcessor
}