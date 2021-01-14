package be.tapped.vlaamsetv.auth

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.App
import be.tapped.vlaamsetv.gen
import be.tapped.vlaamsetv.prefs.Credential
import be.tapped.vlaamsetv.prefs.vrt.VRTTokenStore
import be.tapped.vlaamsetv.vrtTokenWrapperArb
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.profile.RefreshToken
import be.tapped.vrtnu.profile.TokenRepo
import be.tapped.vrtnu.profile.TokenWrapper
import be.tapped.vrtnu.profile.XVRTToken
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VRTTokenRefreshWorkerTest {

    private val context get() = ApplicationProvider.getApplicationContext<App>()

    @Test
    fun noRefreshTokenInStoreShouldFailure() {
        val worker = TestListenableWorkerBuilder<VRTTokenRefreshWorker>(context)
            .setWorkerFactory(AuthenticationWorkerFactory)
            .build() as VRTTokenRefreshWorker

        runBlocking {
            val result = worker.doWork()
            result shouldBe ListenableWorker.Result.failure()
        }
    }

    @Test
    fun tokenRefreshFailedShouldFailure() {
        val worker = TestListenableWorkerBuilder<VRTTokenRefreshWorker>(context)
            .setWorkerFactory(
                buildWorkerFactory(
                    { ApiResponse.Failure.EmptyJson.left() },
                    { vrtTokenWrapperArb.gen() })
            )
            .build() as VRTTokenRefreshWorker

        runBlocking {
            val result = worker.doWork()
            result shouldBe ListenableWorker.Result.failure()
        }
    }

    @Test
    fun tokenRefreshShouldBeSuccessful() {
        val worker = TestListenableWorkerBuilder<VRTTokenRefreshWorker>(context)
            .setWorkerFactory(
                buildWorkerFactory(
                    { ApiResponse.Success.Authentication.Token(vrtTokenWrapperArb.gen()).right() },
                    { vrtTokenWrapperArb.gen() }
                )
            )
            .build() as VRTTokenRefreshWorker

        runBlocking {
            val result = worker.doWork()
            result shouldBe ListenableWorker.Result.success()
        }
    }

    private fun buildWorkerFactory(
        refreshTokenWrapperF: () -> Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token>,
        tokenFunc: () -> TokenWrapper
    ): WorkerFactory {
        return object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
            ): ListenableWorker {
                return VRTTokenRefreshWorker(
                    appContext, workerParameters,
                    object : TokenRepo {
                        override suspend fun fetchTokenWrapper(
                            userName: String,
                            password: String
                        ): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token> {
                            TODO("Not yet implemented")
                        }

                        override suspend fun refreshTokenWrapper(refreshToken: RefreshToken): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token> =
                            refreshTokenWrapperF()

                        override suspend fun fetchXVRTToken(
                            userName: String,
                            password: String
                        ): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.VRTToken> {
                            TODO("Not yet implemented")
                        }

                        override suspend fun fetchVRTPlayerToken(xVRTToken: XVRTToken): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.PlayerToken> {
                            TODO("Not yet implemented")
                        }
                    },
                    object : VRTTokenStore {
                        override suspend fun saveVRTCredentials(
                            username: String,
                            password: String
                        ) {
                            TODO("Not yet implemented")
                        }

                        override suspend fun vrtCredentials(): Credential? {
                            TODO("Not yet implemented")
                        }

                        override suspend fun token(): TokenWrapper = tokenFunc()

                        override suspend fun saveTokenWrapper(tokenWrapper: TokenWrapper) {
                        }

                        override suspend fun saveXVRTToken(xVRTToken: XVRTToken) {
                            TODO("Not yet implemented")
                        }

                        override suspend fun xVRTToken(): XVRTToken? {
                            TODO("Not yet implemented")
                        }

                    }
                )
            }
        }

    }
}
