package be.tapped.vlaamsetv.auth

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import be.tapped.vier.profile.HttpProfileRepo
import be.tapped.vlaamsetv.App
import be.tapped.vlaamsetv.VIERErrorMessageConverter
import be.tapped.vlaamsetv.VRTErrorMessageConverter
import be.tapped.vlaamsetv.VTMErrorMessageConverter
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
                VRTTokenRefreshWorker(appContext,
                    workerParameters,
                    VRTTokenUseCase(ProfileRepo(),
                        (appContext as App).vrtTokenStore,
                        VRTErrorMessageConverter(),
                        tokenRefreshWorkScheduler)
                )
            VTMTokenRefreshWorker::class.java.name ->
                VTMTokenRefreshWorker(appContext,
                    workerParameters,
                    VTMTokenUseCase(HttpAuthenticationRepo(),
                        (appContext as App).vtmTokenStore,
                        VTMErrorMessageConverter(),
                        tokenRefreshWorkScheduler)
                )
            VIERTokenRefreshWorker::class.java.name ->
                VIERTokenRefreshWorker(appContext,
                    workerParameters,
                    VIERTokenUseCase(HttpProfileRepo(),
                        (appContext as App).vierTokenStore,
                        VIERErrorMessageConverter(),
                        tokenRefreshWorkScheduler)
                )
            else                                    -> null
        }
    }
}
