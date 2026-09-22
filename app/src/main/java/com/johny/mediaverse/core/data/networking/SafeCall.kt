package com.johny.mediaverse.core.data.networking

import com.johny.mediaverse.core.domain.utils.NetworkError
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import com.johny.mediaverse.core.domain.utils.Result

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, NetworkError> {
    val response = try {
        execute()
    }catch (_: UnresolvedAddressException){
        return Result.Error(NetworkError.NoInternet)
    }catch (_: SerializationException){
        return Result.Error(NetworkError.SerializationError)
    }catch (_:Exception){
        currentCoroutineContext().ensureActive()
        return Result.Error(NetworkError.Unknown())
    }

    return try {
        responseToResult(response)
    }catch (_: SerializationException){
        Result.Error(NetworkError.SerializationError)
    }catch (_: Exception){
        currentCoroutineContext().ensureActive()
        Result.Error(NetworkError.Unknown(response.status.value))
    }
}