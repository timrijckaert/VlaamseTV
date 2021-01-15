package be.tapped.vlaamsetv.auth

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import be.tapped.vlaamsetv.App
import be.tapped.vlaamsetv.prefs.vrt.VRTTokenStoreImpl
import be.tapped.vrtnu.profile.ProfileRepo

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
                    VRTTokenStoreImpl(appContext, (appContext as App).crypto)
                )
            )
            else -> null
        }
}
