package be.tapped.vlaamsetv.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import be.tapped.vlaamsetv.App
import be.tapped.vlaamsetv.R

class DetailActivity : FragmentActivity(R.layout.activity_detail) {

    private val app get() = application as App

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            override fun instantiate(cls: ClassLoader, className: String): Fragment =
                when (className) {
                    DetailFragment::class.java.name -> DetailFragment(app.appStateController)
                    else -> super.instantiate(cls, className)
                }
        }
        super.onCreate(savedInstanceState)
    }
}
