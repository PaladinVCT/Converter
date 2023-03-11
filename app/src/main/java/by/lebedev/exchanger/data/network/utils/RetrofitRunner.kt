

package by.lebedev.exchanger.data.network.utils


import by.lebedev.exchanger.data.mappers.MapFunction
import by.lebedev.exchanger.data.mappers.Mapper
import by.lebedev.exchanger.data.mappers.toMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitRunner @Inject constructor(
    private val errorCatcher: ResultErrorCatcher
) {

    suspend fun <R, E> invoke(
        mapper: Mapper<R, E>,
        request: suspend () -> R
    ): Result<E> {
        return errorCatcher.catch {
            val response = request()
            mapper.map(response)
        }
    }

    suspend fun <R, E> invoke(
        mapper: MapFunction<R, E>,
        request: suspend () -> R
    ): Result<E> =
        invoke(mapper.toMapper(), request)


    suspend operator fun <R> invoke(request: suspend () -> R): Result<R> =
        invoke({ it }, request)

}