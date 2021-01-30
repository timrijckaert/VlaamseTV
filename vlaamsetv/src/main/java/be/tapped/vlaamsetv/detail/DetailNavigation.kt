package be.tapped.vlaamsetv.detail

import be.tapped.vlaamsetv.browse.presenter.Item

interface DetailNavigation {

    fun navigateToDetail(clickedItem: Item)
}
