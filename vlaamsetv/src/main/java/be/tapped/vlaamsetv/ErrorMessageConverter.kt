package be.tapped.vlaamsetv

import androidx.annotation.StringRes
import be.tapped.vrtnu.profile.LoginFailure
import be.tapped.vrtnu.ApiResponse as VRTApiResponse

data class ErrorMessage(@StringRes val error: Int, val extras: List<Any?> = emptyList())

class ErrorMessageConverter {

    fun mapToHumanReadableError(failure: VRTApiResponse.Failure): ErrorMessage =
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
