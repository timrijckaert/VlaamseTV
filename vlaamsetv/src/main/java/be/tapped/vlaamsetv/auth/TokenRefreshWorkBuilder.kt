package be.tapped.vlaamsetv.auth

import android.content.Context
import androidx.work.*
import arrow.core.Either
import java.util.concurrent.TimeUnit

object TokenRefreshWorkBuilder {

    fun createTokenRefreshVRT(): WorkRequest =
        create<VRTTokenRefreshWorker>(VRTTokenRefreshWorker.brandTag)

    fun createTokenRefreshVTM(): WorkRequest =
        create<VTMTokenRefreshWorker>(VTMTokenRefreshWorker.brandTag)

    fun createTokenRefreshVIER(): WorkRequest =
        create<VIERTokenRefreshWorker>(VIERTokenRefreshWorker.brandTag)

    private inline fun <reified T : TokenRefreshWorker> create(brandTag: String): WorkRequest {
        return PeriodicWorkRequest.Builder(T::class.java, 1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag("token-refresh")
            .addTag(brandTag)
            .build()
    }
}

abstract class TokenRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
    private val useCase: TokenUseCase,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result =
        when (useCase.refresh()) {
            is Either.Left -> Result.failure()
            is Either.Right -> Result.success()
        }
}

private class VRTTokenRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
    vrtTokenUseCase: TokenUseCase,
) : TokenRefreshWorker(appContext, params, vrtTokenUseCase) {
    internal companion object {
        const val brandTag: String = "VRT_NU"
    }
}

class VTMTokenRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
    vtmTokenUseCase: TokenUseCase,
) : TokenRefreshWorker(appContext, params, vtmTokenUseCase) {

    internal companion object {
        const val brandTag: String = "VTM_GO"
    }
}

class VIERTokenRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
    tokenUseCase: TokenUseCase
) : TokenRefreshWorker(appContext, params, tokenUseCase) {
    internal companion object {
        const val brandTag: String = "VIER"
    }
}
