package br.com.vieirateam.wyncef.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.extension.addFragment
import br.com.vieirateam.wyncef.extension.configureDarkMode
import br.com.vieirateam.wyncef.fragment.*
import br.com.vieirateam.wyncef.util.FabButtonUtil
import br.com.vieirateam.wyncef.util.ConstantsUtil
import br.com.vieirateam.wyncef.util.UserPreferenceUtil
import kotlinx.android.synthetic.main.activity_main.drawer_layout
import kotlinx.android.synthetic.main.activity_main.navigation_view
import kotlinx.android.synthetic.main.adapter_toolbar.toolbar

class MainActivity : GenericActivity(R.layout.activity_main),
    NavigationView.OnNavigationItemSelectedListener {

    var indicator = true
    private var commit = false
    private var fragment: Fragment = Fragment()
    private lateinit var mSwitch: SwitchCompat
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var fabButtonUtil: FabButtonUtil

    override fun posCreate(savedInstanceState: Bundle?) {
        fabButtonUtil = FabButtonUtil(this)
        setSupportActionBar()
        configureToolbar()

        val savedTitle = savedInstanceState?.getCharSequence(ConstantsUtil.TITLE)
        if (savedTitle != null) {
            title = savedTitle
        } else {
            title = getString(R.string.app_name)
            firstFragment()
        }
        configureSwitchButton()
    }

    private fun configureToolbar() {
        toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, 0, 0)
        toggle.isDrawerIndicatorEnabled = false
        toggle.setToolbarNavigationClickListener {
            if (indicator) {
                drawer_layout.openDrawer(GravityCompat.START)
            } else {
                if(fabButtonUtil.fabOpen) {
                    fabButtonUtil.configureFabMenu()
                } else {
                    setFirstFragment()
                }
            }
        }
        setHomeAsUpIndicator(R.drawable.ic_drawable_menu)
        toggle.syncState()
        navigation_view.setNavigationItemSelectedListener(this)
    }

    private fun setFirstFragment() {
        indicator = true
        setHomeAsUpIndicator(R.drawable.ic_drawable_menu)
        firstFragment()
    }

    fun setHomeAsUpIndicator(indicator: Int) {
        toggle.setHomeAsUpIndicator(indicator)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if(indicator){
                super.onBackPressed()
            }else{
                if(fabButtonUtil.fabOpen){
                    fabButtonUtil.configureFabMenu()
                }else{
                    setFirstFragment()
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (!commit) firstFragment()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(ConstantsUtil.TITLE, title)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_inventory -> {
                firstFragment()
            }
            R.id.nav_agency -> {
                title = getString(R.string.nav_agency)
                fragment = AgencyFragment()
                bundle.putBoolean(ConstantsUtil.FAB, false)
                fragment.arguments = bundle
                startFragment(fragment)
            }
            R.id.nav_category -> {
                title = getString(R.string.nav_category)
                fragment = CategoryFragment()
                bundle.putBoolean(ConstantsUtil.FAB, false)
                fragment.arguments = bundle
                startFragment(fragment)
            }
            R.id.nav_device -> {
                title = getString(R.string.nav_device)
                fragment = DeviceFragment()
                bundle.putBoolean(ConstantsUtil.FAB, false)
                fragment.arguments = bundle
                startFragment(fragment)
            }
            R.id.nav_tag -> {
                title = getString(R.string.nav_tag)
                fragment = TagFragment()
                bundle.putBoolean(ConstantsUtil.FAB, false)
                fragment.arguments = bundle
                startFragment(fragment)
            }
            R.id.nav_dark -> {
                mSwitch.isChecked = !mSwitch.isChecked
                UserPreferenceUtil.darkMode = !UserPreferenceUtil.darkMode
                configureDarkMode(UserPreferenceUtil.darkMode)
                recreateActivity()
            }
            R.id.nav_about -> {
                title = getString(R.string.nav_about)
                fragment = AboutFragment()
                bundle.putBoolean(ConstantsUtil.FAB, false)
                fragment.arguments = bundle
                startFragment(fragment)
            }
            R.id.nav_exit -> {
                finishAffinity()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun startFragment(fragment: Fragment) {
        commit = addFragment(R.id.frame_layout, fragment)
    }

    private fun firstFragment() {
        title = getString(R.string.nav_inventory)
        fragment = InventoryFragment()
        bundle.putBoolean(ConstantsUtil.FAB, false)
        fragment.arguments = bundle
        startFragment(fragment)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configureSwitchButton() {
        val menu = navigation_view.menu.findItem(R.id.nav_dark).actionView
        mSwitch = menu.findViewById(R.id.switch_dark_mode)
        mSwitch.isChecked = UserPreferenceUtil.darkMode

        mSwitch.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_MOVE) {
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }

        mSwitch.setOnClickListener {
            UserPreferenceUtil.darkMode = !UserPreferenceUtil.darkMode
            configureDarkMode(UserPreferenceUtil.darkMode)
            recreateActivity()
        }
    }

    private fun recreateActivity() {
        this.recreate()
    }
}
