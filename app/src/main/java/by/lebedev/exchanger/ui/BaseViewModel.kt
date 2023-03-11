package by.lebedev.exchanger.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.lebedev.exchanger.data.network.utils.Event
import by.lebedev.exchanger.ui.utils.BaseNavEvent
import by.lebedev.exchanger.ui.utils.MessageCluster
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn


open class BaseViewModel <S : MavericksState>(initialState: S) :
MavericksViewModel<S>(initialState) {

    protected val errorEventBus = MutableLiveData<Event<Throwable>>()
    val onErrorEvent: LiveData<Event<Throwable>>
        get() = errorEventBus

    protected val alertEventBus = MutableLiveData<Event<String>>()
    val onAlertEvent: LiveData<Event<String>>
        get() = alertEventBus

    protected val messageEvent = MutableLiveData<Event<MessageCluster>>()
    val onMessageEvent: LiveData<Event<MessageCluster>>
        get() = messageEvent

    protected val navigateEvent = MutableLiveData<Event<BaseNavEvent>>()
    val onNavigateEvent: LiveData<Event<BaseNavEvent>>
        get() = navigateEvent

    fun <T> Flow<T>.launch(): Job = launchIn(viewModelScope)
}