package be.tapped.vlaamsetv.auth

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class VIERTokenRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
    private val tokenUseCase: TokenUseCase
) :
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}
