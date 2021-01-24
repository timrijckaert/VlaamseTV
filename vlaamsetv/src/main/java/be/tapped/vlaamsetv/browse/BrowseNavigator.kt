package be.tapped.vlaamsetv.browse

import androidx.navigation.NavController
import be.tapped.vlaamsetv.detail.DetailNavigation

interface BrowseNavigator : DetailNavigation {
    companion object {

        fun create(navController: NavController): BrowseNavigator =
            object : BrowseNavigator {
                override fun navigateToDetail() {
                    navController.navigate(BrowseFragmentDirections.actionBrowseFragmentToDetailActivity())
                }
            }
    }
}

