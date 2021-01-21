package be.tapped.vlaamsetv.browse.vrt

class VRTBrowseUseCase(
    azUseCase: VRTNUAZUseCase,
    liveTVUseCase: LiveTVUseCase,
    categoryUseCase: CategoriesUseCase,
) : VRTNUAZUseCase by azUseCase,
    LiveTVUseCase by liveTVUseCase,
    CategoriesUseCase by categoryUseCase
