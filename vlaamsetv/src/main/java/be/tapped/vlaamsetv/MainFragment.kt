package be.tapped.vlaamsetv

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainFragment(private val rootNavigator: RootNavigator) : Fragment(R.layout.fragment_main) {

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            rootNavigator.moveToStartDestination()
        }
    }
}
