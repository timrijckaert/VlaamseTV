package be.tapped.vlaamsetv.auth

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.App
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.errorMessageArb
import be.tapped.vlaamsetv.gen
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VRTTokenRefreshWorkerTest {

    private val context get() = ApplicationProvider.getApplicationContext<App>()

    @Test
    fun tokenRefreshFailedShouldResultInAFailure() {
        val worker = TestListenableWorkerBuilder<VRTTokenRefreshWorker>(context)
            .setWorkerFactory(
                buildWorkerFactory { errorMessageArb.gen().left() }
            )
            .build() as CoroutineWorker

        runBlocking {
            val result = worker.doWork()
            result shouldBe ListenableWorker.Result.failure()
        }
    }

    @Test
    fun tokenRefreshWasSuccessFulShouldResultInASuccess() {
        val worker = TestListenableWorkerBuilder<VRTTokenRefreshWorker>(context)
            .setWorkerFactory(
                buildWorkerFactory { true.right() }
            )
            .build() as CoroutineWorker

        runBlocking {
            val result = worker.doWork()
            result shouldBe ListenableWorker.Result.success()
        }
    }

    private fun buildWorkerFactory(refreshFunc: () -> Either<ErrorMessage, Boolean>): WorkerFactory {
        return object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
            ): ListenableWorker {
                return VRTTokenRefreshWorker(
                    appContext, workerParameters,
                    object : TokenUseCase {
                        override suspend fun performLogin(
                            username: String,
                            password: String
                        ): Either<ErrorMessage, Unit> {
                            throw RuntimeException("Test is not allowed to call this method.")
                        }

                        override suspend fun refresh(): Either<ErrorMessage, Boolean> =
                            refreshFunc()
                    }
                )
            }
        }
    }
}
