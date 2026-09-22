package com.johny.mediaverse.core.data.networking

import com.johny.mediaverse.core.domain.utils.NetworkError
import com.johny.mediaverse.core.domain.utils.Result
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.ContentConvertException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException

suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, NetworkError> {
    return when (val code = response.status.value) {
        in 200..290 -> {
            try {
                Result.Success(response.body<T>())
            } catch (_: NoTransformationFoundException) {
                Result.Error(NetworkError.SerializationError)
            } catch (_: ContentConvertException) {
                Result.Error(NetworkError.SerializationError)
            } catch (_: SerializationException) {
                Result.Error(NetworkError.SerializationError)
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()
                Result.Error(NetworkError.Unknown(code))
            }
        }
        408 -> Result.Error(NetworkError.RequestTimeout)
        429 -> Result.Error(NetworkError.TooManyRequest)
        in 500..599 -> Result.Error(NetworkError.ServerError(code))
        else -> Result.Error(NetworkError.Unknown(code))
    }
}