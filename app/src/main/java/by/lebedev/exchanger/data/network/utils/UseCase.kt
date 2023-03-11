
package by.lebedev.exchanger.data.network.utils


import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit


abstract class UseCase<P : Any, T> {

    abstract val dispatcher: CoroutineDispatcher

    operator fun invoke(params: P): Flow<InvokeStatus<T>> = flow {
        emit(InvokeStarted)
        try {
            val result = doWork(params)
            emit(InvokeSuccess(result))
        } catch (t: Throwable) {
            // Skip coroutine CancellationException
            if (t is CancellationException || t.cause is CancellationException) {
                return@flow
            }
            emit(InvokeError(t))
        }
    }.flowOn(dispatcher)

    protected abstract suspend fun doWork(params: P): T

}

abstract class WorkUseCase<in P> {
    protected abstract val scope: CoroutineScope

    @OptIn(ObsoleteCoroutinesApi::class)
    operator fun invoke(params: P, timeoutMs: Long = defaultTimeoutMs): Flow<InvokeStatus<Unit>> {
        val channel = ConflatedBroadcastChannel<InvokeStatus<Unit>>()
        scope.launch {
            try {
                withTimeout(timeoutMs) {
                    channel.send(InvokeStarted)
                    try {
                        doWork(params)
                        channel.send(InvokeSuccess(Unit))
                    } catch (t: Throwable) {
                        // Skip coroutine CancellationException
                        if (t is CancellationException || t.cause is CancellationException) {
                            return@withTimeout
                        }
                        channel.send(InvokeError(t))
                    }
                }
            } catch (t: TimeoutCancellationException) {
                val throwable = ThrowableResult(cause = t, type = ErrorType.Timeout)
                channel.send(InvokeError(throwable))
            } finally {
                delay(400)
                channel.close()
            }
        }
        return channel.asFlow()
    }


    suspend fun executeSync(params: P) = withContext(scope.coroutineContext) { doWork(params) }

    protected abstract suspend fun doWork(params: P)

    companion object {
        private val defaultTimeoutMs = TimeUnit.MINUTES.toMillis(5)
    }
}

interface ObservableUseCase<T> {
    val dispatcher: CoroutineDispatcher
    fun observe(): Flow<T>
}

abstract class SuspendingWorkUseCase<P : Any, T : Any> : ObservableUseCase<T> {
    @OptIn(ObsoleteCoroutinesApi::class)
    private val channel = ConflatedBroadcastChannel<T>()

    @OptIn(ObsoleteCoroutinesApi::class)
    suspend operator fun invoke(params: P) = channel.send(doWork(params))

    abstract suspend fun doWork(params: P): T

    @OptIn(ObsoleteCoroutinesApi::class)
    override fun observe(): Flow<T> = channel.asFlow()

}

abstract class SubjectUseCase<P : Any, T> : ObservableUseCase<T> {
    @OptIn(ObsoleteCoroutinesApi::class)
    private val channel = ConflatedBroadcastChannel<P>()

    @OptIn(ObsoleteCoroutinesApi::class)
    operator fun invoke(params: P) = channel.trySendBlocking(params)

    protected abstract fun createObservable(params: P): Flow<T>

    @OptIn(ObsoleteCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun observe(): Flow<T> = channel.asFlow()
        .distinctUntilChanged()
        .flatMapLatest { createObservable(it) }
}

abstract class ResultUseCase<in P, R> {
    abstract val dispatcher: CoroutineDispatcher

    suspend operator fun invoke(params: P): R {
        return withContext(dispatcher) { doWork(params) }
    }

    protected abstract suspend fun doWork(params: P): R
}


operator fun WorkUseCase<Unit>.invoke() = invoke(Unit)
operator fun <T> UseCase<Unit, T>.invoke() = invoke(Unit)
suspend operator fun <T> ResultUseCase<Unit, T>.invoke() = invoke(Unit)
operator fun <T> SubjectUseCase<Unit, T>.invoke() = invoke(Unit)

fun <I : ObservableUseCase<T>, T> CoroutineScope.launchObserve(
    useCase: I,
    block: suspend (Flow<T>) -> Unit
) {
    launch(useCase.dispatcher) {
        block(useCase.observe())
    }
}
