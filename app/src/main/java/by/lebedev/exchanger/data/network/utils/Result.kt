package by.lebedev.exchanger.data.network.utils

sealed class Result<out T> {
    open fun get(): T? = null
}

data class Success<T>(val data: T) : Result<T>() {
    override fun get(): T = data
}

data class Failure(
    val message: String? = null,
    val cause: Throwable? = null,
    val type: ErrorType = ErrorType.Unclassified

) : Result<Nothing>() {
    val throwable = ThrowableResult(message, cause, type)
}

inline fun <T> Result<T>.onSuccess(block: (data: T) -> Unit) {
    if (this is Success) {
        block(data)
    }
}

inline fun Result<*>.onFailure(block: (message: String?, throwable: Throwable?) -> Unit) {
    if (this is Failure) {
        block(message, throwable)
    }
}

fun <T : Any> Result<T>.getOrThrow(): T = when (this) {
    is Success -> data
    is Failure -> throw throwable
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Success -> Success(transform(data))
        is Failure -> this
    }
}

class ThrowableResult(
    message: String? = null,
    cause: Throwable? = null,
    val type: ErrorType
) : Throwable(message, cause)

fun throwThrowableResult(type: ErrorType, message: String? = null): Nothing {
    throw ThrowableResult(type = type, message = message)
}