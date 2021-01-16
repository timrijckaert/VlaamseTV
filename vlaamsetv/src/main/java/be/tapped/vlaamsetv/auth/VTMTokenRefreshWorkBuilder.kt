package be.tapped.vlaamsetv.auth

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import arrow.core.Either
import java.util.concurrent.TimeUnit

class VTMTokenRefreshWorkBuilder(
    appContext: Context,
    params: WorkerParameters,
    private val vtmTokenUseCase: TokenUseCase,
) : CoroutineWorker(appContext, params) {

    companion object : TokenRefreshWorkBuilder {
        private const val VTM_GO = "VTM_GO"
        fun create(context: Context) {
            PeriodicWorkRequestBuilder<VRTTokenRefreshWorker>(1, TimeUnit.DAYS)
                .tokenRefreshParameters()
                .addTag(VTM_GO)
                .build()
        }
    }

    override suspend fun doWork(): Result =
        when (vtmTokenUseCase.refresh()) {
            is Either.Left -> Result.failure()
            is Either.Right -> Result.success()
        }
}
