package be.tapped.vlaamsetv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.createDataStore
import androidx.lifecycle.lifecycleScope
import be.tapped.vlaamsetv.prefs.AesCipherProvider
import be.tapped.vlaamsetv.prefs.CryptoImpl
import be.tapped.vlaamsetv.prefs.TokenWrapperProtoSerializer
import be.tapped.vrtnu.profile.ProfileRepo
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.security.KeyStore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
