package be.tapped.vlaamsetv

import android.content.Context
import androidx.annotation.StringRes
import be.tapped.vrtnu.profile.LoginFailure
import be.tapped.goplay.ApiResponse as GoPlayApiResponse
import be.tapped.vrtnu.ApiResponse as VRTApiResponse
import be.tapped.vtmgo.ApiResponse as VTMApiResponse

data class ErrorMessage(@StringRes val error: Int, val extras: List<Any?> = emptyList()) {

    fun toString(context: Context): String = context.getString(error, extras)
}

interface ErrorMessageConverter<in T> {

    fun mapToHumanReadableError(failure: T): ErrorMessage
}

class VRTErrorMessageConverter : ErrorMessageConverter<VRTApiResponse.Failure> {

    override fun mapToHumanReadableError(failure: VRTApiResponse.Failure): ErrorMessage = when (failure) {
        is VRTApiResponse.Failure.NetworkFailure -> ErrorMessage(
            R.string.failure_vrtnu_network,
            listOf(failure.responseCode)
        )
        is VRTApiResponse.Failure.JsonParsingException -> {
            ErrorMessage(R.string.failure_vrtnu_json_parsing, listOf(failure.throwable.message))
        }
        VRTApiResponse.Failure.EmptyJson -> ErrorMessage(R.string.failure_vrtnu_empty_json)
        is VRTApiResponse.Failure.Authentication.FailedToLogin -> mapFailedToLoginToErrorMessage(failure)
        is VRTApiResponse.Failure.Authentication.MissingCookieValues -> ErrorMessage(
            R.string.failure_vrtnu_missing_cookies,
            failure.cookieValues
        )
        is VRTApiResponse.Failure.Content.SearchQuery -> ErrorMessage(
            R.string.failure_vrtnu_invalid_search_query,
            failure.messages
        )
    }

    private fun mapFailedToLoginToErrorMessage(failure: be.tapped.vrtnu.ApiResponse.Failure.Authentication.FailedToLogin): ErrorMessage =
        when (failure.loginResponseFailure.loginFailure) {
            LoginFailure.LoginFailure.INVALID_CREDENTIALS -> ErrorMessage(R.string.failure_vrtnu_invalid_credentials)
            LoginFailure.LoginFailure.MISSING_LOGIN_ID -> ErrorMessage(R.string.failure_vrtnu_missing_login_id)
            LoginFailure.LoginFailure.MISSING_PASSWORD -> ErrorMessage(R.string.failure_vrtnu_incorrect_pass)
            LoginFailure.LoginFailure.UNKNOWN -> ErrorMessage(
                R.string.failure_vrtnu_unknown,
                listOf(
                    failure.loginResponseFailure.errorCode,
                    failure.loginResponseFailure.statusCode,
                    failure.loginResponseFailure.statusReason,
                )
            )
        }
}

class VTMErrorMessageConverter : ErrorMessageConverter<VTMApiResponse.Failure> {

    override fun mapToHumanReadableError(failure: be.tapped.vtmgo.ApiResponse.Failure): ErrorMessage = when (failure) {
        is be.tapped.vtmgo.ApiResponse.Failure.NetworkFailure -> ErrorMessage(
            R.string.failure_vtmgo_network,
            listOf(failure.responseCode)
        )
        is be.tapped.vtmgo.ApiResponse.Failure.JsonParsingException -> ErrorMessage(
            R.string.failure_vtmgo_json_parsing,
            listOf(failure.throwable.message)
        )
        be.tapped.vtmgo.ApiResponse.Failure.EmptyJson -> ErrorMessage(
            R.string.failure_vrtnu_empty_json
        )
        is be.tapped.vtmgo.ApiResponse.Failure.Authentication.MissingCookieValues -> ErrorMessage(
            R.string.failure_vtmgo_missing_cookies,
            failure.cookieValues
        )
        be.tapped.vtmgo.ApiResponse.Failure.Authentication.NoAuthorizeResponse, be.tapped.vtmgo.ApiResponse.Failure.Authentication.NoCodeFound, be.tapped.vtmgo.ApiResponse.Failure.Authentication.NoStateFound -> ErrorMessage(
            R.string.failure_vtmgo_general_auth,
            listOf(failure)
        )
        is be.tapped.vtmgo.ApiResponse.Failure.Stream.NoStreamFoundForType -> ErrorMessage(
            R.string.failure_vtmgo_no_stream_found,
            listOf(failure.streamType)
        )
        be.tapped.vtmgo.ApiResponse.Failure.Stream.NoJSONFoundInAnvatoJavascriptFunction, be.tapped.vtmgo.ApiResponse.Failure.Stream.NoDashStreamFound, be.tapped.vtmgo.ApiResponse.Failure.Stream.NoHlsStreamFound, be.tapped.vtmgo.ApiResponse.Failure.Stream.NoAnvatoStreamFound, be.tapped.vtmgo.ApiResponse.Failure.Stream.NoPublishedEmbedUrlFound, be.tapped.vtmgo.ApiResponse.Failure.Stream.NoMPDManifestUrlFound -> ErrorMessage(
            R.string.failure_vtmgo_general_stream,
            listOf(failure)
        )
    }
}

class GoPlayErrorMessageConverter : ErrorMessageConverter<GoPlayApiResponse.Failure> {

    override fun mapToHumanReadableError(failure: be.tapped.goplay.ApiResponse.Failure): ErrorMessage = when (failure) {
        is be.tapped.goplay.ApiResponse.Failure.NetworkFailure -> ErrorMessage(
            R.string.failure_goplay_network,
            listOf(failure.responseCode)
        )
        is be.tapped.goplay.ApiResponse.Failure.JsonParsingException -> ErrorMessage(
            R.string.failure_goplay_json_parsing,
            listOf(failure.throwable.message)
        )
        be.tapped.goplay.ApiResponse.Failure.HTML.EmptyHTML -> ErrorMessage(R.string.failure_goplay_empty_html)
        is be.tapped.goplay.ApiResponse.Failure.HTML.MissingAttributeValue -> ErrorMessage(
            R.string.failure_goplay_missing_html_attribute,
            listOf(failure.attribute)
        )
        is be.tapped.goplay.ApiResponse.Failure.HTML.NoSelection -> ErrorMessage(
            R.string.failure_goplay_no_selection,
            listOf(failure.cssQuery)
        )
        is be.tapped.goplay.ApiResponse.Failure.HTML.NoChildAtPosition -> ErrorMessage(
            R.string.failure_goplay_no_child_at_position,
            listOf(
                failure.position,
                failure.amountOfChildren
            )
        )
        is be.tapped.goplay.ApiResponse.Failure.Authentication.AWS -> ErrorMessage(
            R.string.failure_authentication_aws,
            listOf(
                failure.statusCode,
                failure.statusText
            )
        )
        be.tapped.goplay.ApiResponse.Failure.Authentication.Login -> ErrorMessage(R.string.failure_authentication_login)
        be.tapped.goplay.ApiResponse.Failure.Authentication.Refresh -> ErrorMessage(R.string.failure_authentication_refresh)
        be.tapped.goplay.ApiResponse.Failure.Authentication.Profile -> ErrorMessage(R.string.failure_authentication_profile)
        be.tapped.goplay.ApiResponse.Failure.Content.NoEpisodeFound -> ErrorMessage(
            R.string.failure_content_no_episode_found,
        )
        is be.tapped.goplay.ApiResponse.Failure.Stream.NoStreamFound -> ErrorMessage(
            R.string.failure_content_no_stream_found,
            listOf(failure.videoUuid.id)
        )
        is be.tapped.goplay.ApiResponse.Failure.Epg.NoEpgDataFor -> ErrorMessage(R.string.failure_epg, listOf(failure.calendar))
        be.tapped.goplay.ApiResponse.Failure.Content.ProgramNoLongerAvailable -> ErrorMessage(R.string.failure_content_program_not_available)
    }
}
