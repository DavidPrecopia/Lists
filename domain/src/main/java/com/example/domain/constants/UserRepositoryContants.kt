package com.example.domain.constants

const val SMS_TIME_OUT_SECONDS = 15L

const val PHONE_NUM_COUNTRY_CODE_USA = "+1"

enum class AuthProviders {
    GOOGLE,
    EMAIL,
    PHONE,
    UNKNOWN
}

sealed class PhoneNumValidationResults {
    // The number was validated, but the SMS was not sent.
    object Validated : PhoneNumValidationResults()

    data class SmsSent(val validationCode: String) : PhoneNumValidationResults()
}