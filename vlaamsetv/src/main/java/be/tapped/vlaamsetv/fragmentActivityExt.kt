package be.tapped.vlaamsetv

import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment

val FragmentActivity.navHostFragment: NavHostFragment
    get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
