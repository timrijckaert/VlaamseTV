package be.tapped.vlaamsetv

import androidx.work.DelegatingWorkerFactory
import androidx.work.WorkerFactory

class WorkerFactory(workerFactories: List<WorkerFactory>) : DelegatingWorkerFactory() {
    init {
        workerFactories.forEach(::addFactory)
    }
}
