package be.tapped.vlaamsetv.auth

import android.content.Context
import androidx.work.*
import arrow.core.Either
import be.tapped.vlaamsetv.prefs.vrt.VRTTokenStore
import be.tapped.vrtnu.profile.TokenRepo
import java.util.concurrent.TimeUnit

class VRTTokenRefreshWorker(
        appContext: Context,
        params: WorkerParameters,
        private val tokenRepo: TokenRepo,
        private val vrtDataStore: VRTTokenStore,
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

    override suspend fun doWork(): Result {
        //TODO log this somewhere because we should not schedule a task if we do not have a refresh token anyway
        val refreshToken = vrtDataStore.token()?.refreshToken ?: return Result.failure()
        return when (val tokenWrapper = tokenRepo.refreshTokenWrapper(refreshToken)) {
            is Either.Left -> Result.failure()
            is Either.Right -> {
                vrtDataStore.saveTokenWrapper(tokenWrapper.b.tokenWrapper)
                Result.success()
            }
        }
    }
}
