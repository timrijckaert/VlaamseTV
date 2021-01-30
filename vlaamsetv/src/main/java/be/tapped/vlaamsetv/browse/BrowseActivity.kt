package be.tapped.vlaamsetv.browse

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.leanback.app.BackgroundManager
import be.tapped.vlaamsetv.App
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.navHostFragment

class BrowseActivity : FragmentActivity(R.layout.activity_tv_browse) {

    private val backgroundManager by lazy { BackgroundManager.getInstance(this) }
    private val app get() = application as App

    private val browseNavigator by lazy { BrowseNavigator.create(navHostFragment.navController, app.appStateController) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        backgroundManager.attach(window)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            override fun instantiate(cls: ClassLoader, className: String): Fragment =
                when (className) {
                    BrowseFragment::class.java.name -> BrowseFragment(backgroundManager, browseNavigator)
                    else -> super.instantiate(cls, className)
                }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundManager.release()
    }
}
