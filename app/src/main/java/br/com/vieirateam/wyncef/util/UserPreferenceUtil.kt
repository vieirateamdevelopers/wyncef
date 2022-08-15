package br.com.vieirateam.wyncef.util

import android.content.SharedPreferences
import br.com.vieirateam.wyncef.WyncefApplication

object UserPreferenceUtil {

    private fun getSharedPreferences(): SharedPreferences {
        val context = WyncefApplication.getInstance().applicationContext
        return context.getSharedPreferences(ConstantsUtil.PREF_ID, 0)
    }

    private fun setGridModePreference(value: Boolean) {
        val preferences = getSharedPreferences()
        val editor = preferences.edit()
        editor.putBoolean(ConstantsUtil.GRID_MODE, value)
        editor.apply()
    }

    private fun getGridModePreference(): Boolean {
        val preferences = getSharedPreferences()
        return preferences.getBoolean(ConstantsUtil.GRID_MODE, true)
    }

    private fun setDarkModePreference(value: Boolean) {
        val preferences = getSharedPreferences()
        val editor = preferences.edit()
        editor.putBoolean(ConstantsUtil.DARK_MODE, value)
        editor.apply()
    }

    private fun getDarkModeRunPreference(): Boolean {
        val preferences = getSharedPreferences()
        return preferences.getBoolean(ConstantsUtil.DARK_MODE, false)
    }

    private fun setFirstRunPreference(value: Boolean) {
        val preferences = getSharedPreferences()
        val editor = preferences.edit()
        editor.putBoolean(ConstantsUtil.FIRST_RUN, value)
        editor.apply()
    }

    private fun getFirstRunPreference(): Boolean {
        val preferences = getSharedPreferences()
        return preferences.getBoolean(ConstantsUtil.FIRST_RUN, true)
    }

    var firstRun
        get() = getFirstRunPreference()
        set(value) = setFirstRunPreference(value)

    var gridMode
        get() = getGridModePreference()
        set(value) = setGridModePreference(value)

    var darkMode
        get() = getDarkModeRunPreference()
        set(value) = setDarkModePreference(value)
}