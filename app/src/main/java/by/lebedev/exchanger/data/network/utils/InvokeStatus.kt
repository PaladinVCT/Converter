package by.lebedev.exchanger.data.network.utils

sealed class InvokeStatus<out R>

object InvokeStarted : InvokeStatus<Nothing>()

data class InvokeSuccess<T>(val data: T) : InvokeStatus<T>(), InvokeCompleted
data class InvokeError(val throwable: Throwable) : InvokeStatus<Nothing>(), InvokeCompleted

interface InvokeCompleted