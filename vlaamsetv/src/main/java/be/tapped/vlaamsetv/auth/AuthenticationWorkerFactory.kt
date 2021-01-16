package be.tapped.vlaamsetv.auth

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import be.tapped.vier.profile.HttpProfileRepo
import be.tapped.vlaamsetv.App
import be.tapped.vlaamsetv.VIERErrorMessageConverter
import be.tapped.vlaamsetv.VRTErrorMessageConverter
import be.tapped.vlaamsetv.VTMErrorMessageConverter
import be.tapped.vlaamsetv.auth.prefs.vier.VIERTokenStoreImpl
import be.tapped.vlaamsetv.auth.prefs.vrt.VRTTokenStoreImpl
import be.tapped.vlaamsetv.auth.prefs.vtm.VTMTokenStoreImpl
import be.tapped.vrtnu.profile.ProfileRepo
import be.tapped.vtmgo.profile.HttpAuthenticationRepo

object AuthenticationWorkerFactory : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? =
        when (workerClassName) {
            VRTTokenRefreshWorker::class.java.name -> VRTTokenRefreshWorker(
                appContext,
                workerParameters,
                VRTTokenUseCase(
                    ProfileRepo(),
                    VRTTokenStoreImpl(appContext, (appContext as App).crypto),
                    VRTErrorMessageConverter()
                )
            )
            VTMTokenRefreshWorker::class.java.name -> VTMTokenRefreshWorker(
                appContext,
                workerParameters,
                VTMTokenUseCase(
                    HttpAuthenticationRepo(),
                    VTMTokenStoreImpl(appContext, (appContext as App).crypto),
                    VTMErrorMessageConverter()
                )
            )
            VIERTokenRefreshWorker::class.java.name -> VIERTokenRefreshWorker(
                appContext,
                workerParameters,
                VIERTokenUseCase(
                    HttpProfileRepo(),
                    VIERTokenStoreImpl(appContext, (appContext as App).crypto),
                    VIERErrorMessageConverter()
                )
            )
            else -> null
        }
}
