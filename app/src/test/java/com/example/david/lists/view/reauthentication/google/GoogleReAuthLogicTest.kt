package com.example.david.lists.view.reauthentication.google

import com.example.david.lists.SchedulerProviderMockInit
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.view.reauthentication.google.IGoogleReAuthContract.ViewEvent
import com.example.domain.repository.IRepositoryContract
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GoogleReAuthLogicTest {

    private val view = mockk<IGoogleReAuthContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<IGoogleReAuthContract.ViewModel>()

    private val userRepo = mockk<IRepositoryContract.UserRepository>()

    private val schedulerProvider = mockk<ISchedulerProviderContract>()


    private val logic = GoogleReAuthLogic(view, viewModel, userRepo, schedulerProvider)


    private val message = "message"


    @BeforeEach
    fun init() {
        clearAllMocks()
        SchedulerProviderMockInit.init(schedulerProvider)
    }


    /**
     * - [ViewEvent.OnStart].
     * - Successfully delete the user via the UserRepo.
     * - Display message from ViewModel.
     * - Open auth view.
     */
    @Test
    fun `onEvent - OnStart - succeeded`() {
        every { viewModel.msgAccountDeletionSucceed } returns message
        every { userRepo.deleteGoogleUser() } answers { Completable.complete() }

        logic.onEvent(ViewEvent.OnStart)

        verify { userRepo.deleteGoogleUser() }
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
        val exception = mockk<Exception>(relaxed = true)

        every { viewModel.msgAccountDeletionFailed } returns message
        every { userRepo.deleteGoogleUser() } answers { Completable.error(exception) }

        logic.onEvent(ViewEvent.OnStart)

        verify { userRepo.deleteGoogleUser() }
        verify { exception.printStackTrace() }
        verify { view.displayMessage(message) }
        verify { view.finishView() }
    }
}