package by.lebedev.exchanger.di

import by.lebedev.exchanger.di.mvrx.AssistedViewModelFactory
import by.lebedev.exchanger.di.mvrx.MavericksViewModelComponent
import by.lebedev.exchanger.di.mvrx.ViewModelKey
import by.lebedev.exchanger.ui.converter.ConverterViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.multibindings.IntoMap

@Module
@InstallIn(MavericksViewModelComponent::class)
interface ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ConverterViewModel::class)
    fun bindConverterViewModel(factory: ConverterViewModel.Factory): AssistedViewModelFactory<*, *>

}