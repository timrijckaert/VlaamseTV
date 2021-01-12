package be.tapped.vlaamsetv

import androidx.annotation.StringRes
import be.tapped.vrtnu.profile.LoginFailure
import be.tapped.vier.ApiResponse as VIERApiResponse
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
                ErrorMessage(R.string.failure_vrtnu_missing_cookies, failure.cookieValues)
            is VRTApiResponse.Failure.Content.SearchQuery ->
                ErrorMessage(R.string.failure_vrtnu_invalid_search_query, failure.messages)
        }

    private fun mapFailedToLoginToErrorMessage(failure: be.tapped.vrtnu.ApiResponse.Failure.Authentication.FailedToLogin): ErrorMessage =
        when (failure.loginResponseFailure.loginFailure) {
            LoginFailure.LoginFailure.INVALID_CREDENTIALS -> ErrorMessage(R.string.failure_vrtnu_invalid_credentials)
            LoginFailure.LoginFailure.MISSING_LOGIN_ID -> ErrorMessage(R.string.failure_vrtnu_missing_login_id)
            LoginFailure.LoginFailure.MISSING_PASSWORD -> ErrorMessage(R.string.failure_vrtnu_incorrect_pass)
            LoginFailure.LoginFailure.UNKNOWN -> ErrorMessage(
                R.string.failure_vrtnu_unknown, listOf(
                    failure.loginResponseFailure.errorCode,
                    failure.loginResponseFailure.statusCode,
                    failure.loginResponseFailure.statusReason,
                )
            )
        }
}

class VTMErrorMessageConverter : ErrorMessageConverter<VTMApiResponse.Failure> {
    override fun mapToHumanReadableError(failure: be.tapped.vtmgo.ApiResponse.Failure): ErrorMessage =
        when (failure) {
            is be.tapped.vtmgo.ApiResponse.Failure.NetworkFailure ->
                ErrorMessage(
                    R.string.failure_vtmgo_network,
                    listOf(failure.responseCode)
                )
            is be.tapped.vtmgo.ApiResponse.Failure.JsonParsingException ->
                ErrorMessage(
                    R.string.failure_vtmgo_json_parsing,
                    listOf(failure.throwable.message)
                )
            be.tapped.vtmgo.ApiResponse.Failure.EmptyJson ->
                ErrorMessage(R.string.failure_vrtnu_empty_json)
            is be.tapped.vtmgo.ApiResponse.Failure.Authentication.MissingCookieValues ->
                ErrorMessage(R.string.failure_vtmgo_missing_cookies, failure.cookieValues)
            be.tapped.vtmgo.ApiResponse.Failure.Authentication.NoAuthorizeResponse,
            be.tapped.vtmgo.ApiResponse.Failure.Authentication.NoCodeFound,
            be.tapped.vtmgo.ApiResponse.Failure.Authentication.NoStateFound ->
                ErrorMessage(R.string.failure_vtmgo_general_auth, listOf(failure))
            is be.tapped.vtmgo.ApiResponse.Failure.Stream.NoStreamFoundForType ->
                ErrorMessage(R.string.failure_vtmgo_no_stream_found, listOf(failure.streamType))
            be.tapped.vtmgo.ApiResponse.Failure.Stream.NoJSONFoundInAnvatoJavascriptFunction,
            be.tapped.vtmgo.ApiResponse.Failure.Stream.NoDashStreamFound,
            be.tapped.vtmgo.ApiResponse.Failure.Stream.NoHlsStreamFound,
            be.tapped.vtmgo.ApiResponse.Failure.Stream.NoAnvatoStreamFound,
            be.tapped.vtmgo.ApiResponse.Failure.Stream.NoPublishedEmbedUrlFound,
            be.tapped.vtmgo.ApiResponse.Failure.Stream.NoMPDManifestUrlFound ->
                ErrorMessage(R.string.failure_vtmgo_general_stream, listOf(failure))
        }
}

class VIERErrorMessageConverter : ErrorMessageConverter<VIERApiResponse.Failure> {
    override fun mapToHumanReadableError(failure: be.tapped.vier.ApiResponse.Failure): ErrorMessage =
        when (failure) {
            is be.tapped.vier.ApiResponse.Failure.NetworkFailure -> TODO()
            is be.tapped.vier.ApiResponse.Failure.JsonParsingException -> TODO()
            be.tapped.vier.ApiResponse.Failure.HTML.EmptyHTML -> TODO()
            is be.tapped.vier.ApiResponse.Failure.HTML.MissingAttributeValue -> TODO()
            is be.tapped.vier.ApiResponse.Failure.HTML.NoSelection -> TODO()
            is be.tapped.vier.ApiResponse.Failure.HTML.NoChildAtPosition -> TODO()
            is be.tapped.vier.ApiResponse.Failure.HTML.Parsing -> TODO()
            is be.tapped.vier.ApiResponse.Failure.Authentication.AWS -> TODO()
            be.tapped.vier.ApiResponse.Failure.Authentication.Login -> TODO()
            be.tapped.vier.ApiResponse.Failure.Authentication.Refresh -> TODO()
            be.tapped.vier.ApiResponse.Failure.Authentication.Profile -> TODO()
            be.tapped.vier.ApiResponse.Failure.Content.NoEpisodeFound -> TODO()
            is be.tapped.vier.ApiResponse.Failure.Stream.NoStreamFound -> TODO()
            is be.tapped.vier.ApiResponse.Failure.Epg.NoEpgDataFor -> TODO()
        }
}
