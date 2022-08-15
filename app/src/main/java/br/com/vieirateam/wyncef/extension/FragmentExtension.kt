package br.com.vieirateam.wyncef.extension

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.addFragment(@IdRes layoutId: Int, fragment: Fragment): Boolean {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    val attachedFragment = supportFragmentManager.findFragmentById(layoutId)

    if (attachedFragment != null) fragmentTransaction.remove(attachedFragment)
    fragmentTransaction.add(layoutId, fragment)

    try {
        fragmentTransaction.commit()
        return true
    } catch (exception: IllegalStateException) {
        finishAffinity()
    }
    return false
}