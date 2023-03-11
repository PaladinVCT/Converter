package by.lebedev.exchanger.data.network.utils

import by.lebedev.exchanger.data.mappers.errors.HttpErrorMapper
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLHandshakeException

@Singleton
class ResultErrorCatcher @Inject constructor(private val errorMapper: HttpErrorMapper) {

    fun handleHttpException(exception: HttpException): Failure {
        return try {
            val message = errorMapper.map(exception)
            when (exception.code()) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> Failure(
                    message,
                    exception,
                    ErrorType.Unauthorized
                )
                HttpURLConnection.HTTP_INTERNAL_ERROR -> Failure(
                    "internal error__${exception.response()?.raw()?.request?.url}",
                    exception,
                    ErrorType.Network
                )
                else -> Failure(message, exception, ErrorType.Network)
            }
        } catch (e: Exception) {
            Failure(
                message = "error message mapping failed",
                cause = exception,
                type = ErrorType.Unclassified
            )
        }
    }

    inline fun <T> catch(block: () -> T): Result<T> {
        return try {
            Success(block())
        } catch (exception: ThrowableResult) {
            Failure(message = exception.message, cause = exception.cause, type = exception.type)
        } catch (exception: HttpException) {
            handleHttpException(exception)
        } catch (exception: SocketTimeoutException) {
            Failure(cause = exception, type = ErrorType.Timeout)
        } catch (exception: ConnectException) {
            Failure(cause = exception, type = ErrorType.NetworkConnection)
        } catch (exception: SocketException) {
            Failure(cause = exception, type = ErrorType.NetworkConnection)
        } catch (exception: JsonDataException) {
            val message = exception.message
            Failure(message = message, cause = exception, type = ErrorType.JsonParsing)
        } catch (exception: JsonEncodingException) {
            Failure(cause = exception, type = ErrorType.JsonParsing)
        } catch (exception: SSLHandshakeException) {
            val message = "SSL Handshake failed"
            Failure(message = message, cause = exception, type = ErrorType.NetworkConnection)
        } catch (exception: IOException) {
            Failure(cause = exception, type = ErrorType.IO)
        } catch (exception: CancellationException) {
            Failure(cause = exception, type = ErrorType.Unclassified)
        } catch (exception: Exception) {
            Failure(cause = exception, type = ErrorType.Unclassified)
        }
    }
}