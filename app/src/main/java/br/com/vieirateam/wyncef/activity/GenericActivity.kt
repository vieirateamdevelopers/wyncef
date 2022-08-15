package br.com.vieirateam.wyncef.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.wyncef.extension.configureDarkMode
import br.com.vieirateam.wyncef.util.UserPreferenceUtil
import kotlinx.android.synthetic.main.adapter_toolbar.toolbar

abstract class GenericActivity(private val layoutID: Int?) : AppCompatActivity() {

    protected var bundle = Bundle()
    protected var baseIntent = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        configureDarkMode(UserPreferenceUtil.darkMode)
        super.onCreate(savedInstanceState)
        if (layoutID != null) setContentView(layoutID)
        posCreate(savedInstanceState)
    }

    protected fun setSupportActionBar() {
        setSupportActionBar(toolbar)
    }

    protected fun setDisplayHomeAsUpEnabled() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    abstract fun posCreate(savedInstanceState: Bundle?)
}