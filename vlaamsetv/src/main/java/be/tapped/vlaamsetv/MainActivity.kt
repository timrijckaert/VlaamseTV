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

        val dataStore = createDataStore(
            fileName = "ComplexDataStoreTest.pb",
            serializer = TokenWrapperProtoSerializer(
                CryptoImpl(
                    AesCipherProvider(
                        "SimpleDataKey",
                        KeyStore.getInstance("AndroidKeyStore").apply { load(null) },
                        "AndroidKeyStore"
                    )
                )
            )
        )
        // val dataStore2 = EncryptedDataStore(
        //     context,
        //     CryptoImpl(AesCipherProvider("VlaamseTvKey", app.keyStore, KEYSTORE_NAME))
        // )

        lifecycleScope.launch {
            val a =
                ProfileRepo().fetchTokenWrapper("DLTODPCDigitaleOpdrachtNative@vrt.be", "native22")
            val b = a.orNull()!!.tokenWrapper
            dataStore.updateData {
                it.copy(
                    access_token = "eyJraWQiOiJyc2ExIiwiYWxnIjoiUlMyNTYifQeyJhdWQiOiJ2cnRudS1zaXRlIiwic3ViIjoiNmRlNjg1MjctNGVjMi00MmUwLTg0YmEtNGU5ZjE3ZTQ4MmY2IiwiaXNzIjoiaHR0cHM6XC9cL2xvZ2luLnZydC5iZSIsInNjb3BlcyI6ImFkZHJlc3Msb3BlbmlkLHByb2ZpbGUsbGVnYWN5aWQsbWlkLGVtYW",
                    refresh_token = "r",
                    expiry = 10L
                )
            }
            // dataStore2.saveTokenWrapper(
            //     TokenWrapper(
            //         AccessToken("acccccccc"),
            //         RefreshToken("refreesherugheruherg"),
            //         Expiry(42L)
            //     )
            // )
            dataStore.data.collect {
                println(it)
            }
        }

        setContentView(R.layout.activity_main)
    }
}
