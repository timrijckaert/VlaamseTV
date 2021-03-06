package be.tapped.vlaamsetv.auth

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import arrow.core.Either
import java.util.concurrent.TimeUnit

class TokenRefreshWorkScheduler(private val workManager: WorkManager) {

    fun scheduleTokenRefreshVRT() {
        workManager.enqueue(tokenRefreshVRT)
    }

    fun scheduleTokenRefreshVTM() {
        workManager.enqueue(tokenRefreshVTM)
    }

    fun scheduleTokenRefreshGoPlay() {
        workManager.enqueue(tokenRefreshGoPlay)
    }

    private val tokenRefreshVRT: WorkRequest
        get() = create<VRTTokenRefreshWorker>(VRTTokenRefreshWorker.brandTag)

    private val tokenRefreshVTM: WorkRequest
        get() = create<VTMTokenRefreshWorker>(VTMTokenRefreshWorker.brandTag)

    private val tokenRefreshGoPlay: WorkRequest
        get() = create<GoPlayTokenRefreshWorker>(GoPlayTokenRefreshWorker.brandTag)

    private inline fun <reified T : TokenRefreshWorker> create(brandTag: String): WorkRequest {
        return PeriodicWorkRequest
            .Builder(T::class.java, 1, TimeUnit.DAYS)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setBackoffCriteria(BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
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

internal class VRTTokenRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
    vrtTokenUseCase: TokenUseCase,
) : TokenRefreshWorker(appContext, params, vrtTokenUseCase) {

    internal companion object {

        const val brandTag: String = "VRT_NU"
    }
}

internal class VTMTokenRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
    vtmTokenUseCase: TokenUseCase,
) : TokenRefreshWorker(appContext, params, vtmTokenUseCase) {

    internal companion object {

        const val brandTag: String = "VTM_GO"
    }
}

internal class GoPlayTokenRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
    tokenUseCase: TokenUseCase
) :
    TokenRefreshWorker(appContext, params, tokenUseCase) {

    internal companion object {

        const val brandTag: String = "GO_PLAY"
    }
}
