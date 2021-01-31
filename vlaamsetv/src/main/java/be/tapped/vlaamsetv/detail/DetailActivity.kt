package be.tapped.vlaamsetv.detail

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.navigation.navArgs
import be.tapped.vlaamsetv.App
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vrtnu.content.LiveStreams
import kotlinx.android.parcel.Parcelize

class DetailActivity : FragmentActivity(R.layout.activity_detail) {

    sealed class InputArgument : Parcelable {
        sealed class VRT : InputArgument() {
            @Parcelize
            data class Program(val programName: String) : VRT()
        }
    }

    private val app get() = application as App
    private val navArgs by navArgs<DetailActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            override fun instantiate(cls: ClassLoader, className: String): Fragment =
                when (className) {
                    DetailFragment::class.java.name -> DetailFragment(
                        DetailFragment.Input(
                            "Terzake",
                            "Van maandag tot en met vrijdag brengt Terzake duiding bij het nieuws van de dag. Voor de kijker die beter wil begrijpen, kaderen Annelies Beck en Kathleen Cools de actualiteit aan de hand van kritische en verhelderende interviews.",
                            "https://images.vrt.be/orig/2020/09/01/939e2c56-ec5a-11ea-aae0-02b7b76bf47f.jpg",
                            "https://images.vrt.be/orig/2020/08/31/71a43a3c-eba9-11ea-aae0-02b7b76bf47f.jpg",
                            listOf(
                                DetailFragment.Input.Season(
                                    "Seizoen 1",
                                    listOf(
                                        Item.ImageCard.Live.VRT(
                                            LiveStreams.canvas,
                                            "Canvas",
                                            "https://www.google.com/url?sa=i&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FCanvas_(TV_channel)&psig=AOvVaw1dyBXtsJMI_0F8FW6vZPm3&ust=1612176443778000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCNDXqLX_xe4CFQAAAAAdAAAAABAD",
                                            null
                                        )
                                    )
                                )
                            )
                        )
                    )
                    else -> super.instantiate(cls, className)
                }
        }
        super.onCreate(savedInstanceState)
    }
}
