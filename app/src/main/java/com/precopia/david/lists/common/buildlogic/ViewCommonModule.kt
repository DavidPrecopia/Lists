package com.precopia.david.lists.common.buildlogic

import android.app.Application
import android.content.SharedPreferences
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.precopia.david.lists.common.ListsApplication
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.IUtilThemeContract
import com.precopia.david.lists.util.SchedulerProvider
import com.precopia.david.lists.view.common.TouchHelperCallback
import com.precopia.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.disposables.CompositeDisposable

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
    fun utilTheme(appComponent: AppComponent): IUtilThemeContract {
        return appComponent.utilTheme()
    }

    @Provides
    fun sharedPrefs(appComponent: AppComponent): SharedPreferences {
        return appComponent.sharedPrefs()
    }

    @Provides
    fun appComponent(application: Application): AppComponent {
        return (application as ListsApplication).appComponent
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
     * The injection site needs to be annotated with `@JvmSuppressWildcards`.
     */
    @ViewScope
    @Provides
    fun getStringResFunction(application: Application): (Int) -> String {
        return { application.getString(it) }
    }

    /**
     * The injection site needs to be annotated with `@JvmSuppressWildcards`.
     */
    @ViewScope
    @Provides
    fun getStringResArgFunction(application: Application): (Int, String) -> String {
        return { res, arg -> application.getString(res, arg) }
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
