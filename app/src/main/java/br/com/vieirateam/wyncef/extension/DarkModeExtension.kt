package br.com.vieirateam.wyncef.extension

import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.wyncef.R

fun AppCompatActivity.configureDarkMode(darkMode: Boolean): Boolean {
    if (darkMode) {
        setTheme(R.style.DarkTheme)
    } else {
        setTheme(R.style.AppTheme)
    }
    return true
}