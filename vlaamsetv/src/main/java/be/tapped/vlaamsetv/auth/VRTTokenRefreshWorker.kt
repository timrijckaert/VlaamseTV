package be.tapped.vlaamsetv.auth

import android.content.Context
import androidx.work.*
import arrow.core.Either
import be.tapped.vrtnu.ApiResponse
import java.util.concurrent.TimeUnit

class VRTTokenRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
    private val vrtTokenUseCase: TokenUseCase<ApiResponse.Failure>,
) : CoroutineWorker(appContext, params) {

    companion object {
        fun create(context: Context) {
            PeriodicWorkRequestBuilder<VRTTokenRefreshWorker>(1, TimeUnit.DAYS)
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
                .addTag(TOKEN_REFRESH_TAG)
                .addTag(VRT_NU)
                .build()
        }
    }

    override suspend fun doWork(): Result =
        when (vrtTokenUseCase.refresh()) {
            is Either.Left -> Result.failure()
            is Either.Right -> Result.success()
        }
}
