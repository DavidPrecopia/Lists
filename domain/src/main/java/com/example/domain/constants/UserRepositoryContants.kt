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
    /**
     * This is when the user is instantly verified, thus an SMS code is not sent.
     * In this case, I cannot continue because, unlike [SmsSent], this method does not give me the Verification ID.
     */
    object Validated : PhoneNumValidationResults()

    data class SmsSent(val validationCode: String) : PhoneNumValidationResults()
}