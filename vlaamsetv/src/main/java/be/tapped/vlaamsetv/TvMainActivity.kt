package be.tapped.vlaamsetv

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import be.tapped.vlaamsetv.auth.AuthenticationUseCase
import be.tapped.vlaamsetv.auth.VRTAuthenticationFragment
import be.tapped.vrtnu.profile.ProfileRepo
import be.tapped.vrtnu.profile.TokenRepo

public class TvMainActivity : FragmentActivity(R.layout.activity_tv_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            private val profileRepo: TokenRepo = ProfileRepo()

            override fun instantiate(cls: ClassLoader, className: String): Fragment {
                return when (className) {
                    VRTAuthenticationFragment::class.java.name ->
                        VRTAuthenticationFragment(
                            AuthenticationUseCase.vrtAuthenticationUseCase(
                                profileRepo
                            )
                        )
                    else -> super.instantiate(cls, className)
                }
            }
        }
        super.onCreate(savedInstanceState)
    }
}
