package be.tapped.vlaamsetv

import androidx.annotation.StringRes
import be.tapped.vrtnu.profile.LoginFailure
import be.tapped.vrtnu.ApiResponse as VRTApiResponse
import be.tapped.vtmgo.ApiResponse as VTMApiResponse

data class ErrorMessage(@StringRes val error: Int, val extras: List<Any?> = emptyList())

interface ErrorMessageConverter<in T> {
    fun mapToHumanReadableError(failure: T): ErrorMessage
}

class VRTErrorMessageConverter : ErrorMessageConverter<VRTApiResponse.Failure> {

    override fun mapToHumanReadableError(failure: VRTApiResponse.Failure): ErrorMessage =
        when (failure) {
            is VRTApiResponse.Failure.NetworkFailure ->
                ErrorMessage(R.string.failure_vrtnu_network, listOf(failure.responseCode))
            is VRTApiResponse.Failure.JsonParsingException -> {
                ErrorMessage(
                    R.string.failure_vrtnu_json_parsing,
                    listOf(failure.throwable.message)
                )
            }
            VRTApiResponse.Failure.EmptyJson ->
                ErrorMessage(R.string.failure_vrtnu_empty_json)
            is VRTApiResponse.Failure.Authentication.FailedToLogin ->
                mapFailedToLoginToErrorMessage(failure)
            is VRTApiResponse.Failure.Authentication.MissingCookieValues ->
                ErrorMessage(R.string.failure_vrtnu_empty_json, failure.cookieValues)
            is VRTApiResponse.Failure.Content.SearchQuery ->
                ErrorMessage(R.string.failure_invalid_search_query, failure.messages)
        }

    private fun mapFailedToLoginToErrorMessage(failure: be.tapped.vrtnu.ApiResponse.Failure.Authentication.FailedToLogin): ErrorMessage =
        when (failure.loginResponseFailure.loginFailure) {
            LoginFailure.LoginFailure.INVALID_CREDENTIALS -> ErrorMessage(R.string.failure_invalid_credentials)
            LoginFailure.LoginFailure.MISSING_LOGIN_ID -> ErrorMessage(R.string.failure_missing_login_id)
            LoginFailure.LoginFailure.MISSING_PASSWORD -> ErrorMessage(R.string.failure_incorrect_pass)
            LoginFailure.LoginFailure.UNKNOWN -> ErrorMessage(
                R.string.failure_unknown, listOf(
                    failure.loginResponseFailure.errorCode,
                    failure.loginResponseFailure.statusCode,
                    failure.loginResponseFailure.statusReason,
                )
            )
        }
}

class VTMErrorMessageConverter : ErrorMessageConverter<VTMApiResponse> {
    override fun mapToHumanReadableError(failure: be.tapped.vtmgo.ApiResponse): ErrorMessage =
        when (failure) {
            is be.tapped.vtmgo.ApiResponse.Success.Content.Catalog -> TODO()
            is be.tapped.vtmgo.ApiResponse.Success.Content.Categories -> TODO()
            is be.tapped.vtmgo.ApiResponse.Success.Content.LiveChannels -> TODO()
            is be.tapped.vtmgo.ApiResponse.Success.Content.StoreFrontRows -> TODO()
            is be.tapped.vtmgo.ApiResponse.Success.Content.Programs -> TODO()
            is be.tapped.vtmgo.ApiResponse.Success.Content.Favorites -> TODO()
            is be.tapped.vtmgo.ApiResponse.Success.Content.Search -> TODO()
            is be.tapped.vtmgo.ApiResponse.Success.ProgramGuide -> TODO()
            is be.tapped.vtmgo.ApiResponse.Success.Authentication.Token -> TODO()
            is be.tapped.vtmgo.ApiResponse.Success.Authentication.Profiles -> TODO()
            is be.tapped.vtmgo.ApiResponse.Success.Stream.Anvato -> TODO()
            is be.tapped.vtmgo.ApiResponse.Success.Stream.Dash -> TODO()
            is be.tapped.vtmgo.ApiResponse.Success.Stream.Hls -> TODO()
            is be.tapped.vtmgo.ApiResponse.Failure.NetworkFailure -> TODO()
            is be.tapped.vtmgo.ApiResponse.Failure.JsonParsingException -> TODO()
            be.tapped.vtmgo.ApiResponse.Failure.EmptyJson -> TODO()
            is be.tapped.vtmgo.ApiResponse.Failure.Authentication.MissingCookieValues -> TODO()
            be.tapped.vtmgo.ApiResponse.Failure.Authentication.NoAuthorizeResponse -> TODO()
            be.tapped.vtmgo.ApiResponse.Failure.Authentication.NoCodeFound -> TODO()
            be.tapped.vtmgo.ApiResponse.Failure.Authentication.NoStateFound -> TODO()
            is be.tapped.vtmgo.ApiResponse.Failure.Stream.UnsupportedTargetType -> TODO()
            is be.tapped.vtmgo.ApiResponse.Failure.Stream.NoStreamFoundForType -> TODO()
            be.tapped.vtmgo.ApiResponse.Failure.Stream.NoJSONFoundInAnvatoJavascriptFunction -> TODO()
            be.tapped.vtmgo.ApiResponse.Failure.Stream.NoDashStreamFound -> TODO()
            be.tapped.vtmgo.ApiResponse.Failure.Stream.NoHlsStreamFound -> TODO()
            be.tapped.vtmgo.ApiResponse.Failure.Stream.NoAnvatoStreamFound -> TODO()
            be.tapped.vtmgo.ApiResponse.Failure.Stream.NoPublishedEmbedUrlFound -> TODO()
            be.tapped.vtmgo.ApiResponse.Failure.Stream.NoMPDManifestUrlFound -> TODO()
        }
}
