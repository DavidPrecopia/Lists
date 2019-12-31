package com.precopia.david.lists.widget.common.buildlogic

import android.content.Context
import android.content.SharedPreferences
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.widget.common.UtilWidgetKeys
import dagger.Module
import dagger.Provides
import javax.inject.Named

const val SHARED_PREFS = "widget_shared_prefs"
private const val STRING_RES_FUN = "string_res_fun"
private const val STRING_RES_ARG_FUN = "string_res_arg_fun"

@Module
class WidgetCommonModule {
    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun utilWidgetKeys(@Named(STRING_RES_FUN) getStringRes: (Int) -> String,
                       @Named(STRING_RES_ARG_FUN) getStringResArg: (Int, Int) -> String): UtilWidgetKeys {
        return UtilWidgetKeys(getStringRes, getStringResArg)
    }

    @ViewScope
    @Provides
    @Named(STRING_RES_FUN)
    fun getStringResFunction(context: Context): (Int) -> String {
        return { context.getString(it) }
    }

    @ViewScope
    @Provides
    @Named(STRING_RES_ARG_FUN)
    fun getStringResArgFunction(context: Context): (Int, Int) -> String {
        return { res, arg -> context.getString(res, arg) }
    }

    @ViewScope
    @Provides
    @Named(SHARED_PREFS)
    fun sharedPrefs(utilWidgetKeys: UtilWidgetKeys, context: Context): SharedPreferences {
        return context.getSharedPreferences(utilWidgetKeys.getSharedPrefName(), Context.MODE_PRIVATE)
    }
}