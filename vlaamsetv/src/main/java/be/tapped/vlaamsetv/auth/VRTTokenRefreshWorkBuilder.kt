package be.tapped.vlaamsetv.auth

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import arrow.core.Either
import be.tapped.vrtnu.ApiResponse
import java.util.concurrent.TimeUnit

class VRTTokenRefreshWorkBuilder(
    appContext: Context,
    params: WorkerParameters,
    private val vrtTokenUseCase: TokenUseCase<ApiResponse.Failure>,
) : CoroutineWorker(appContext, params), TokenRefreshWorkBuilder {

    companion object : TokenRefreshWorkBuilder {
        private const val VRT_NU = "VRT_NU"
        fun create(context: Context) {
            PeriodicWorkRequestBuilder<VRTTokenRefreshWorkBuilder>(1, TimeUnit.DAYS)
                .tokenRefreshParameters()
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
