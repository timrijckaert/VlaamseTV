package be.tapped.vlaamsetv.auth

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
public class AuthenticationFragmentTest {

    @Test
    public fun name() {
        onView(isRoot()).check(matches(isCompletelyDisplayed()))
        // launchFragment<AuthenticationFragment>()
    }
}
