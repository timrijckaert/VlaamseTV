package be.tapped.vlaamsetv.browse.presenter

sealed class Item {
    data class ImageCard(
        val title: String,
        val description: String,
        val imageViewUrl: String,
    ) : Item()
}
