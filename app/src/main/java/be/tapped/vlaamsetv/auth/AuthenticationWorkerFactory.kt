package be.tapped.vlaamsetv.auth

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import be.tapped.goplay.profile.HttpProfileRepo
import be.tapped.vlaamsetv.App
import be.tapped.vrtnu.profile.ProfileRepo
import be.tapped.vtmgo.profile.HttpAuthenticationRepo

object AuthenticationWorkerFactory : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        val tokenRefreshWorkScheduler = TokenRefreshWorkScheduler(WorkManager.getInstance(appContext))
        return when (workerClassName) {
            VRTTokenRefreshWorker::class.java.name ->
                VRTTokenRefreshWorker(
                    appContext,
                    workerParameters,
                    VRTTokenUseCase(
                        ProfileRepo(),
                        (appContext as App).vrtTokenStore,
                        tokenRefreshWorkScheduler
                    )
                )
            VTMTokenRefreshWorker::class.java.name ->
                VTMTokenRefreshWorker(
                    appContext,
                    workerParameters,
                    VTMTokenUseCase(
                        HttpAuthenticationRepo(),
                        (appContext as App).vtmTokenStore,
                        tokenRefreshWorkScheduler
                    )
                )
            GoPlayTokenRefreshWorker::class.java.name ->
                GoPlayTokenRefreshWorker(
                    appContext,
                    workerParameters,
                    GoPlayTokenUseCase(
                        HttpProfileRepo(),
                        (appContext as App).goPlayTokenStore,
                        tokenRefreshWorkScheduler
                    )
                )
            else -> null
        }
    }
}
