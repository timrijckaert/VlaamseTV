package be.tapped.vlaamsetv

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode

public class KotestTestProjectConfig : AbstractProjectConfig() {

    override val parallelism: Int get() = 2
    override val isolationMode: IsolationMode = IsolationMode.InstancePerLeaf
}
