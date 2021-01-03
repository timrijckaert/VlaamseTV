package be.tapped.vlaamsetv.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import be.tapped.vlaamsetv.VideoItem
import be.tapped.vlaamsetv.VideoPlayerFragment

public class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(
                    R.id.fragment_container_view,
                    VideoPlayerFragment.newInstance(
                        VideoItem(
                            "https://dcs-vod.apis.anvato.net/vod/p/manifest.mpd?encp=GmxOn2Ya2VSEO4LM107rLQ:BQK4gqiyQwHH2Z5rNp_eG4GxANXPxPlbm1h9iqnPx1vlbkgk3LC9XZOGJihgqfJeKTRX-nxgFcNmF0W7SeQhCKBVX4U9-OjkSE7QGHjWFBkd_dcWgj9cocnrU8hfq5T49-Mofgu-NrwOdtcYyczU5r9cep_y1tSOg9KYniyrkbg7ndvp2X3UFF2Pk1_w0kVWqP5oCBN2ucKivo1Dp9HVgIkilytmOjb1spwrgUz-uoLyxmJGcyy7GxvUYbAUGaPeQ9DuN--vcJe-FbR3awlP9eB3rMnt1Btp9hOhGM4KOjTHAmDNhFTdTw9CU_lcZoMVlo-8O_5b2CJfYQjl2T5jPe3Ld0rwqSHfBYUmWhtUKrHuWKzgm9XEoJpxwGNt7JBiHXN0StFQwsKoAaXkly_A_5gNNKzJwJg44fDuLkdj7ZpM9Gw4aR8ZiuECRlOm4OW8brVf6DmsWAf3eryX9VTEorBxaRPlc1S2DuhvrGBk5im1j4NZC_3Q3HHBNEZbomDFjnhj-8Cyb9XUOWHNBybyickrMbOCbMB_03yiqNRFISEVi8UrDY-oXwLxejQ3-iM6JreVHQxjl7cjjOE6IhKN0MJjx0Xg9QKQfwVpQizZpJtoyImWepVp0s7S0PoLz7jeQVuMeMHb5w6CXEDOacDZvhREqza5DzAh8UdDJvN_-kh-7q_dYt9zysT8fC5xjG3sMmeL0P8SOcLm2-DUoLYKOQbQ9Dvtiy8P1cVaCkUX738DE8SLT6cSLcrSstJMXwyJ7MZ-w0cveom5jfg3t7irvJYOHHtu8A0Vem9_bqbNfmcxK3QbAIRb3MPNkG2geGiS3q9zk0403x_-BRKMsiXuud55n6I8i6j7yHTfasKk_UNrUTdT3yV24_pOwDtSfpeW7r4AgDUTHPWw2ao19MPIE1LrIba_wH4xuK78b1wrzNc-_UnFzc8T5OpAcnKmxSWsMDvKOljkOPHQx3-Kc62Vm7TlLcTB92ELkYkOT2v2jU4iNJ0nXf1OIN_3nLiy_GDmEToHLKf92vsfu5YAn1tRjRTqNkuewIwrTf0WVenRI3TYeTRxeh0TRCPIPHSAreFI6DgnBKSJEmY5BitDquySzv_tY-qe3AdzGmpzytOHdppMqepsSmk6pQ7zzPCKN868RlZ--xQVUwLl09DHrSTnq6-uYfKyaRI6nmlljoQVCsjlOb4K-IOjKv17qqC2r7PHUm4xL-yClecv5J7t0ilR-A&anvtrid=d09ad1b71df6a18fb4e3a0bcac9a0129&anvauth=tb=0~te=1609704577~sgn=61b735e9934ac047b9a43cb4cb066f1b77230311e686f8d629667217917f52db&t=1609704487",
                            VideoItem.Drm(
                                "https://drm.apis.anvato.net/cenc?eqp=amy9EM5fVwZPzvhcP2reWDbUe9p0wtusJuPpWW1oJRJA_zmAtVGG_R--bG5ntlZvzKta0FzrL17bpKCl_C4eeundgib2MQvmliGh8Me6bkQjRuKgrCGk5en0j7XyRL7esNtZQIUAV19UgTBU4P_rFvwzMHRC7qKQvUWPnnc-ZXpbacLBTFjQObmouJNXiqx23I_39zDsJvotr-nkak714YYj_mgoPCsASW7k1jcssXBPWz518KjL4CxvNh7ug9Gd7jTuWHMKhSqppq5A4r0XmM-pQcIUW4v2lfhqRJU7iuM&anvauth=tb=0~te=1609715287~sgn=46fd2c8ba865f6cf59823f86c8fe3b2cc9fb8c30823ac06fb0fe31474ca9c3f8&t=1609704487",
                                VideoItem.Drm.DrmType.WIDEVINE,
                            ),
                        )
                    ),
                )
            }
        }
    }
}
