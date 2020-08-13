package com.precopia.david.lists.util

interface IUtilNightModeContract {
    fun isNightModeEnabled(): Boolean

    fun setDay()

    fun setNight()

    fun setFollowSystem()

    fun restore()
}
