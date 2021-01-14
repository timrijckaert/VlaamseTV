package be.tapped.vlaamsetv

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainFragment(private val rootNavigator: RootNavigator) : Fragment(R.layout.fragment_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            lifecycleScope.launch {
                rootNavigator.moveToStartDestination()
            }
        }
    }
}
