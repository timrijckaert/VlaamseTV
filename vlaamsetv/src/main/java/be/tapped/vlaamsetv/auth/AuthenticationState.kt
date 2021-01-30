package be.tapped.vlaamsetv.auth

class AuthenticationState {

    private val _state: MutableMap<Brand, Type> =
        mutableMapOf(Brand.VRT to Type.UNDEFINED, Brand.VTM to Type.UNDEFINED, Brand.GO_PLAY to Type.UNDEFINED)

    fun updateAuthenticationState(brand: Brand, authenticationState: Type) {
        _state[brand] = authenticationState
    }

    fun stateForBrand(brand: Brand): Type = _state.getValue(brand)

    enum class Brand {
        VRT,
        VTM,
        GO_PLAY
    }

    enum class Type {
        UNDEFINED,
        SKIPPED,
        LOGGED_IN
    }
}
