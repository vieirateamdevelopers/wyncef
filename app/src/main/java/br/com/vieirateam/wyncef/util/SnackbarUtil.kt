package br.com.vieirateam.wyncef.util

import android.view.View
import android.widget.TextView
import br.com.vieirateam.wyncef.R
import com.google.android.material.snackbar.Snackbar

object SnackbarUtil {

    fun show(view: View, message: String, duration: Int): Snackbar? {
        val snackBar = Snackbar.make(view, message, duration)
        if (duration == Snackbar.LENGTH_LONG) snackBar.duration = 3000
        snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).textSize = 20F

        if (UserPreferenceUtil.darkMode) {
            snackBar.view.setBackgroundResource(R.color.colorPrimaryNight)
        } else {
            snackBar.view.setBackgroundResource(R.color.colorPrimaryDay)
        }
        snackBar.show()
        return snackBar
    }
}