package by.lebedev.exchanger.data.network.utils

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

fun <T> Flow<InvokeStatus<T>>.trackLoading(loader: ObservableLoadingCounter): Flow<InvokeStatus<T>> =
    onEach { status ->
        if (status == InvokeStarted) {
            loader.addLoader()
        } else if (status is InvokeCompleted) {
            loader.removeLoader()
        }
    }

inline fun <T, R> Flow<InvokeStatus<T>>.mapInvokeData(crossinline transform: suspend (value: T) -> R): Flow<InvokeStatus<R>> =
    map {
        when (it) {
            is InvokeSuccess -> InvokeSuccess(transform(it.data))
            InvokeStarted -> InvokeStarted
            is InvokeError -> it
        }
    }

inline fun <T> Flow<InvokeStatus<T>>.trackLoading(
    crossinline action: suspend (loading: Boolean) -> Unit
): Flow<InvokeStatus<T>> =
    onEach { status ->
        action(status !is InvokeCompleted)
    }


fun <T> Flow<InvokeStatus<T>>.trackError(eventBus: MutableLiveData<Event<Throwable>>): Flow<InvokeStatus<T>> =
    onEach { status ->
        if (status is InvokeError) {
            eventBus.postValue(Event(status.throwable))
        }
    }

inline fun <T> Flow<InvokeStatus<T>>.trackError(
    crossinline action: suspend (throwable: Throwable) -> Unit
): Flow<InvokeStatus<T>> =
    onEach { status ->
        if (status is InvokeError)
            action(status.throwable)
    }

inline fun <T> Flow<InvokeStatus<T>>.trackSuccess(
    crossinline action: suspend (data: T) -> Unit
): Flow<InvokeStatus<T>> =
    onEach { status ->
        if (status is InvokeSuccess)
            action(status.data)
    }

inline fun <T> Flow<InvokeStatus<T>>.trackCompleted(
    crossinline action: suspend () -> Unit
): Flow<InvokeStatus<T>> =
    onEach { status ->
        if (status is InvokeCompleted)
            action()
    }

suspend inline fun <T> Flow<InvokeStatus<T>>.collectData(crossinline action: suspend (value: T) -> Unit) {
    collect {
        if (it is InvokeSuccess) {
            action(it.data)
        }
    }
}