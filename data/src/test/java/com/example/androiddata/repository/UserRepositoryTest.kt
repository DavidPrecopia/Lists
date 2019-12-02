package com.example.androiddata.repository

import android.app.Application
import com.example.domain.constants.AuthProviders
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.*
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * I am not testing [IRepositoryContract.UserRepository.sendVerificationEmail] and such because
 * it would be meaningless to do so on the JVM. I have to manually invoke the success/failure
 * listeners because the Task that sends the email, ect., needs a network connection to do so.
 */
class UserRepositoryTest {

    private val firebaseAuth = mockk<FirebaseAuth>(relaxUnitFun = true)

    private val actionCodeSettings = mockk<ActionCodeSettings>()

    private val authUI = mockk<AuthUI>()

    private val application = mockk<Application>()


    private val userRepo = UserRepository(firebaseAuth, actionCodeSettings, authUI, application)


    private val user = mockk<FirebaseUser>(relaxUnitFun = true)

    private val emailAddress = "emailAddress"
    private val emptyString = ""


    @BeforeEach
    fun init() {
        clearAllMocks()
    }


    @Nested
    inner class SignedOut {
        /**
         * - User returned by FirebaseAuth is not null, thus the user is signed-in.
         * - Returns false.
         */
        @Test
        fun `signedOut - false`() {
            every { firebaseAuth.currentUser } returns user

            assertThat(userRepo.signedOut).isEqualTo(false)
        }

        /**
         * - User returned by FirebaseAuth is null, thus the user is signed-out.
         * - Returns true.
         */
        @Test
        fun `signedOut - true`() {
            every { firebaseAuth.currentUser } returns null

            assertThat(userRepo.signedOut).isEqualTo(true)
        }
    }


    @Nested
    inner class UserVerified {
        /**
         * - User is not null, thus is not signed-out, and does not have an email.
         * - Returns true.
         */
        @Test
        fun `userVerified - true - no email`() {
            every { firebaseAuth.currentUser } returns user
            every { user.email } returns emptyString

            assertThat(userRepo.userVerified).isEqualTo(true)
        }

        /**
         * - User is not null, thus is not signed-out, has an email, email is verified.
         * - Returns true.
         */
        @Test
        fun `userVerified - true - has email - email verified`() {
            every { firebaseAuth.currentUser } returns user
            every { user.email } returns emailAddress
            every { user.isEmailVerified } returns true

            assertThat(userRepo.userVerified).isEqualTo(true)
        }

        /**
         * - User is not null, thus is not signed-out, has an email, email is not verified.
         * - Returns false.
         */
        @Test
        fun `userVerified - true - has email - email not verified`() {
            every { firebaseAuth.currentUser } returns user
            every { user.email } returns emailAddress
            every { user.isEmailVerified } returns false

            assertThat(userRepo.userVerified).isEqualTo(false)
        }

        /**
         * - User is null.
         * - Returns false.
         */
        @Test
        fun `userVerified - user is null`() {
            every { firebaseAuth.currentUser } returns null

            assertThat(userRepo.userVerified).isEqualTo(false)
        }
    }


    @Nested
    inner class HasEmail {
        /**
         * - User is not null and has an email.
         * - Returns true
         */
        @Test
        fun `hasEmail - true`() {
            every { firebaseAuth.currentUser } returns user
            every { user.email } returns emailAddress

            assertThat(userRepo.hasEmail).isEqualTo(true)
        }

        /**
         * - User is not null and email is an empty String.
         * - Returns false.
         */
        @Test
        fun `hasEmail - false`() {
            every { firebaseAuth.currentUser } returns user
            every { user.email } returns emptyString

            assertThat(userRepo.hasEmail).isEqualTo(false)
        }

        /**
         * - User is null.
         * - Returns false.
         */
        @Test
        fun `hasEmail - user is null`() {
            every { firebaseAuth.currentUser } returns null

            assertThat(userRepo.hasEmail).isEqualTo(false)
        }
    }


    @Nested
    inner class EmailVerified {
        /**
         * - User is not null and email is verified.
         * - Returns true.
         */
        @Test
        fun `emailVerified - true`() {
            every { firebaseAuth.currentUser } returns user
            every { user.isEmailVerified } returns true

            assertThat(userRepo.emailVerified).isEqualTo(true)
        }

        /**
         * - User is not null and email is not verified.
         * - Returns false.
         */
        @Test
        fun `emailVerified - false`() {
            every { firebaseAuth.currentUser } returns user
            every { user.isEmailVerified } returns false

            assertThat(userRepo.emailVerified).isEqualTo(false)
        }

        /**
         * - User is null.
         * - Returns false.
         */
        @Test
        fun `emailVerified - user is null`() {
            every { firebaseAuth.currentUser } returns null

            assertThat(userRepo.emailVerified).isEqualTo(false)
        }
    }


    @Nested
    inner class AuthProvider {
        /**
         * - Provider is [GoogleAuthProvider.PROVIDER_ID].
         * - Returns [AuthProviders.GOOGLE].
         */
        @Test
        fun `authProvider - Google`() {
            every { firebaseAuth.currentUser } returns user
            every { user.providerData[1].providerId } returns GoogleAuthProvider.PROVIDER_ID

            assertThat(userRepo.authProvider).isEqualTo(AuthProviders.GOOGLE)
        }

        /**
         * - Provider is [EmailAuthProvider.PROVIDER_ID].
         * - Returns [AuthProviders.EMAIL].
         */
        @Test
        fun `authProvider - Email`() {
            every { firebaseAuth.currentUser } returns user
            every { user.providerData[1].providerId } returns EmailAuthProvider.PROVIDER_ID

            assertThat(userRepo.authProvider).isEqualTo(AuthProviders.EMAIL)
        }

        /**
         * - Provider is [PhoneAuthProvider.PROVIDER_ID].
         * - Returns [AuthProviders.PHONE].
         */
        @Test
        fun `authProvider - Phone`() {
            every { firebaseAuth.currentUser } returns user
            every { user.providerData[1].providerId } returns PhoneAuthProvider.PROVIDER_ID

            assertThat(userRepo.authProvider).isEqualTo(AuthProviders.PHONE)
        }

        /**
         * - Provider is unknown.
         * - Returns [AuthProviders.UNKNOWN].
         */
        @Test
        fun `authProvider - Unknown`() {
            val unknownProvider = "unknown"

            every { firebaseAuth.currentUser } returns user
            every { user.providerData[1].providerId } returns unknownProvider

            assertThat(userRepo.authProvider).isEqualTo(AuthProviders.UNKNOWN)
        }
    }
}