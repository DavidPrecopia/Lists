package com.precopia.domain.exception

data class AuthInvalidCredentialsException(override val message: String?, override val cause: Throwable?) : Exception()
data class AuthTooManyRequestsException(override val message: String?, override val cause: Throwable?) : Exception()