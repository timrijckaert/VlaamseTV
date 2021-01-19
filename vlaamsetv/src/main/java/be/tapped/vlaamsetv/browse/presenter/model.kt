package be.tapped.vlaamsetv.browse.presenter

sealed class Item {
    data class ImageCard(
        val title: String? = null,
        val description: String? = null,
        val imageViewUrl: String? = null,
    ) : Item()
}
