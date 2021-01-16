package be.tapped.vlaamsetv.auth

import androidx.work.*
import java.util.concurrent.TimeUnit

interface TokenRefreshWorkBuilder {
    companion object {
        internal const val TOKEN_REFRESH_TAG = "token-refresh"
    }

    fun PeriodicWorkRequest.Builder.tokenRefreshParameters(): PeriodicWorkRequest.Builder =
        setConstraints(
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
}
