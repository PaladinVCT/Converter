package by.lebedev.exchanger.di.mvrx

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel

interface AssistedViewModelFactory<VM : MavericksViewModel<S>, S : MavericksState> {
    fun create(state: S): VM
}
