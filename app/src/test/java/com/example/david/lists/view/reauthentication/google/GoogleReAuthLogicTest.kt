package com.example.david.lists.view.reauthentication.google

import com.example.david.lists.view.reauthentication.google.IGoogleReAuthContract.ViewEvent
import com.example.domain.repository.IRepositoryContract
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GoogleReAuthLogicTest {

    private val view = mockk<IGoogleReAuthContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<IGoogleReAuthContract.ViewModel>()

    private val userRepo = mockk<IRepositoryContract.UserRepository>()


    private val logic = GoogleReAuthLogic(view, viewModel, userRepo)


    private val message = "message"


    @BeforeEach
    fun init() {
        clearAllMocks()
    }


    /**
     * - [ViewEvent.OnStart].
     * - Successfully delete the user via the UserRepo.
     * - Display message from ViewModel.
     * - Open auth view.
     */
    @Test
    fun `onEvent - OnStart - succeeded`() {
        val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
        val captureArgFailure = CapturingSlot<OnFailureListener>()

        every { viewModel.msgAccountDeletionSucceed } returns message
        every {
            userRepo.deleteGoogleUser(
                    successListener = capture(captureArgSuccess),
                    failureListener = capture(captureArgFailure)
            )
        } answers { Unit }

        logic.onEvent(ViewEvent.OnStart)

        captureArgSuccess.captured.onSuccess(null)

        verify { userRepo.deleteGoogleUser(captureArgSuccess.captured, captureArgFailure.captured) }
        verify { view.displayMessage(message) }
        verify { view.openAuthView() }
    }

    /**
     * - [ViewEvent.OnStart].
     * - Fail to delete the user via the UserRepo.
     * - Throw an Exception.
     * - Display failure message from ViewModel.
     * - Finish the view.
     */
    @Test
    fun `deleteAccountConfirmed - Google - failed`() {
        val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
        val captureArgFailure = CapturingSlot<OnFailureListener>()
        val exception = mockk<Exception>(relaxed = true)

        every { viewModel.msgAccountDeletionFailed } returns message
        every {
            userRepo.deleteGoogleUser(
                    successListener = capture(captureArgSuccess),
                    failureListener = capture(captureArgFailure)
            )
        } answers { Unit }

        logic.onEvent(ViewEvent.OnStart)

        captureArgFailure.captured.onFailure(exception)

        verify { userRepo.deleteGoogleUser(captureArgSuccess.captured, captureArgFailure.captured) }
        verify { exception.printStackTrace() }
        verify { view.displayMessage(message) }
        verify { view.finishView() }
    }
}