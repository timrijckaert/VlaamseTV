package be.tapped.vlaamsetv

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class AppState {
    sealed class Detail : AppState() {
        sealed class VRT : Detail() {
            data class Program(val p: be.tapped.vrtnu.content.Program) : VRT()
        }
    }

    interface Controller {

        val appState: Flow<AppState>
        val currentState: AppState?

        fun pushState(newState: AppState)

        companion object {

            internal operator fun invoke(): Controller {
                return object : Controller {
                    private val mutableAppState =
                        MutableSharedFlow<AppState>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
                    override val appState: Flow<AppState> get() = mutableAppState
                    override val currentState: AppState? get() = mutableAppState.replayCache.firstOrNull()

                    override fun pushState(newState: AppState) {
                        mutableAppState.tryEmit(newState)
                    }
                }
            }
        }
    }

    interface Provider {

        val appStateController: Controller
    }
}
