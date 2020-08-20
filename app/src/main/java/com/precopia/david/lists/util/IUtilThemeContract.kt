package com.precopia.david.lists.util

interface IUtilThemeContract {
    fun isNightModeEnabled(): Boolean

    fun setDay()

    fun setDark()

    fun setFollowSystem()

    fun restore()


    enum class ThemeLabels(val label: String) {
        DAY("Day"),
        DARK("Dark"),
        FOLLOW_SYSTEM("System default")
    }
}
