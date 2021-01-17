package be.tapped.vlaamsetv

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.navigation.fragment.NavHostFragment
import be.tapped.vlaamsetv.auth.prefs.CompositeTokenStorage

class TvMainActivity : FragmentActivity(R.layout.activity_tv_main) {

    private val app get() = application as App

    private val navHostFragment
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
                when (className) {
                    MainFragment::class.java.name ->
                        MainFragment(
                            RootNavigator.create(
                                Navigator(navHostFragment.navController),
                                CompositeTokenStorage(
                                    app.vrtTokenStore,
                                    app.vtmTokenStore,
                                    app.vierTokenStore
                                )
                            )
                        )
                    else                          -> super.instantiate(classLoader, className)
                }
        }
        super.onCreate(savedInstanceState)
    }
}
