package be.tapped.vlaamsetv.browse.presenter

sealed class Item {

    abstract val index: Int

    data class ImageCard(
        override val index: Int,
        val title: String? = null,
        val description: String? = null,
        val imageViewUrl: String? = null,
    ) : Item()
}
