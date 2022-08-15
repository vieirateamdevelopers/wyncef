package br.com.vieirateam.wyncef.interfaces

import android.content.Intent

interface LoginFirebase {

    fun configureLogin()

    fun configureIntent(intent: Intent)

    fun configureAuth()

    fun updateUI()
}