package com.example.david.lists.common.buildlogic

import android.app.Application
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.david.lists.R
import com.example.david.lists.common.ListsApplication
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.util.IUtilNightModeContract
import com.example.david.lists.util.SchedulerProvider
import com.example.david.lists.view.common.TouchHelperCallback
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class ViewCommonModule {
    @Provides
    fun repository(appComponent: AppComponent): IRepositoryContract.Repository {
        return appComponent.repo()
    }

    @Provides
    fun userRepository(appComponent: AppComponent): IRepositoryContract.UserRepository {
        return appComponent.userRepo()
    }

    @Provides
    fun utilNightMode(appComponent: AppComponent): IUtilNightModeContract {
        return appComponent.utilNightMode()
    }

    @Provides
    fun sharedPrefs(appComponent: AppComponent): SharedPreferences {
        return appComponent.sharedPrefs()
    }

    @Provides
    fun appComponent(application: Application): AppComponent {
        return (application as ListsApplication).appComponent
    }


    @Provides
    fun navController(fragment: Fragment): NavController {
        return fragment.findNavController()
    }

    @Provides
    fun appBarConfiguration(): AppBarConfiguration {
        return AppBarConfiguration
                // top-level destinations, thus they should not display up-navigation
                .Builder(R.id.authView, R.id.userListListView)
                .build()
    }


    @ViewScope
    @Provides
    fun disposable(): CompositeDisposable {
        return CompositeDisposable()
    }

    @ViewScope
    @Provides
    fun schedulerProvider(): ISchedulerProviderContract {
        return SchedulerProvider()
    }


    /**
     * RecyclerView
     */
    @Provides
    fun layoutManager(application: Application): LinearLayoutManager {
        return LinearLayoutManager(application.applicationContext)
    }

    @ViewScope
    @Provides
    fun itemTouchHelper(movementCallback: TouchHelperCallback.MovementCallback): ItemTouchHelper {
        return ItemTouchHelper(TouchHelperCallback(movementCallback))
    }

    @ViewScope
    @Provides
    fun itemDecoration(application: Application, layoutManager: LinearLayoutManager): RecyclerView.ItemDecoration {
        return DividerItemDecoration(application.applicationContext, layoutManager.orientation)
    }

    @ViewScope
    @Provides
    fun viewBinderHelper(): ViewBinderHelper {
        return ViewBinderHelper().apply {
            setOpenOnlyOne(true)
        }
    }
}
