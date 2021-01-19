package be.tapped.vlaamsetv.browse.vrt

class VRTBrowseUseCase(
    azUseCase: VRTNUAZUseCase,
    liveTVUseCase: LiveTVUseCase,
) : VRTNUAZUseCase by azUseCase,
    LiveTVUseCase by liveTVUseCase
