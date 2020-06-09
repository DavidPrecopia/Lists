package com.precopia.david.lists.view.reauthentication.google

import androidx.lifecycle.Observer
import com.precopia.david.lists.InstantExecutorExtension
import com.precopia.david.lists.SchedulerProviderMockInit
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.google.IGoogleReAuthContract.LogicEvents
import com.precopia.david.lists.view.reauthentication.google.IGoogleReAuthContract.ViewEvents
import com.precopia.domain.repository.IRepositoryContract
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(value = [InstantExecutorExtension::class])
class GoogleReAuthLogicTest {

    private val viewModel = mockk<IGoogleReAuthContract.ViewModel>()

    private val userRepo = mockk<IRepositoryContract.UserRepository>()

    private val schedulerProvider = mockk<ISchedulerProviderContract>()


    private val logic = GoogleReAuthLogic(viewModel, userRepo, schedulerProvider)


    private val message = "message"


    @BeforeEach
    fun init() {
        clearAllMocks()
        SchedulerProviderMockInit.init(schedulerProvider)
    }


    /**
     * - [LogicEvents.OnStart].
     * - Successfully delete the user via the UserRepo.
     * - Display message from ViewModel.
     * - Open auth view.
     */
    @Test
    fun `onEvent - OnStart - succeeded`() {
        val listLiveDataOutput = mutableListOf<ViewEvents>()
        val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

        every { viewModel.msgAccountDeletionSucceed } returns message
        every { userRepo.deleteGoogleUser() } answers { Completable.complete() }

        logic.observe().observeForever(liveDataObserver)

        logic.onEvent(LogicEvents.OnStart)

        verify { userRepo.deleteGoogleUser() }
        assertThat(listLiveDataOutput.size).isEqualTo(2)
        assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(message))
        assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.OpenAuthView)

        logic.observe().removeObserver(liveDataObserver)
    }

    /**
     * - [LogicEvents.OnStart].
     * - Fail to delete the user via the UserRepo.
     * - Throw an Exception.
     * - Display failure message from ViewModel.
     * - Finish the view.
     */
    @Test
    fun `deleteAccountConfirmed - Google - failed`() {
        val listLiveDataOutput = mutableListOf<ViewEvents>()
        val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
        val exception = mockk<Exception>(relaxed = true)

        every { viewModel.msgAccountDeletionFailed } returns message
        every { userRepo.deleteGoogleUser() } answers { Completable.error(exception) }

        logic.observe().observeForever(liveDataObserver)

        logic.onEvent(LogicEvents.OnStart)

        verify { userRepo.deleteGoogleUser() }
        verify { exception.printStackTrace() }
        assertThat(listLiveDataOutput.size).isEqualTo(2)
        assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(message))
        assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.FinishView)

        logic.observe().removeObserver(liveDataObserver)
    }
}