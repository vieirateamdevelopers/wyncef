package br.com.vieirateam.wyncef.fragment

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.adapter.BaseAdapter
import br.com.vieirateam.wyncef.util.BottomSheetUtil
import br.com.vieirateam.wyncef.util.ConstantsUtil
import br.com.vieirateam.wyncef.util.UserPreferenceUtil
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.adapter_recycler_view.recycler_view
import kotlinx.android.synthetic.main.bottom_sheet_filter.view.*
import java.io.Serializable
import java.util.Calendar
import java.util.Date

abstract class BaseFragment<T, V> : GenericFragment(R.layout.adapter_recycler_view),
    SearchView.OnQueryTextListener, DatePickerDialog.OnDateSetListener {

    protected var save = false
    protected var filter = mutableListOf<V>()
    private lateinit var configuration: Configuration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configuration = resources.configuration
        configureRecyclerView()

        recycler_view.apply {
            itemAnimator = DefaultItemAnimator()
            scheduleLayoutAnimation()
            setHasFixedSize(true)
            adapter = setAdapter() as BaseAdapter<*>
        }
    }

    override fun posActivityCreated(savedInstanceState: Bundle?) {
        WyncefApplication.getViewModel()
        getItems()
        onActivityCreated()

        fabButtonUtil.floatingButton.setOnClickListener {
            if(fabMenu){
                fabButtonUtil.configureFabMenu()
            }else{
                addOnClickListener()
            }
        }

        fabButtonUtil.floatingButtonDevice.setOnClickListener {
            addOnClickListener()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == ConstantsUtil.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            intent?.let {
                bundle = it.getBundleExtra(ConstantsUtil.BUNDLE) as Bundle
                save = bundle.getBoolean(ConstantsUtil.SAVE)
                val item = bundle.getSerializable(ConstantsUtil.ITEM)
                resultItem(item)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search, menu)
        inflater.inflate(R.menu.grid, menu)
        inflater.inflate(R.menu.filter, menu)

        menu.findItem(R.id.menu_grid)?.isVisible = !(configuration.orientation == Configuration.ORIENTATION_PORTRAIT && resources.getInteger(R.integer.span_count_portrait) == 1)
        val item: MenuItem? = menu.findItem(R.id.menu_grid)
        configureGridMode(item)

        val search = menu.findItem(R.id.menu_search)?.actionView as SearchView
        search.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> {
                closeFabMenu()
                return true
            }
            R.id.menu_grid -> {
                closeFabMenu()
                UserPreferenceUtil.gridMode = !UserPreferenceUtil.gridMode
                configureGridMode(item)
                configureRecyclerView()
                return true
            }
            R.id.menu_filter -> {
                closeFabMenu()
                configureFilter()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        onBindSearch(newText)
        return true
    }

    override fun onDateSet(view: DatePickerDialog?, yearStart: Int, monthStart: Int, dayStart: Int, yearEnd: Int, monthEnd: Int, dayEnd: Int) {
        val startDate = Calendar.getInstance()
        startDate.set(Calendar.YEAR, yearStart)
        startDate.set(Calendar.MONTH, monthStart)
        startDate.set(Calendar.DAY_OF_MONTH, dayStart)

        val finalDate = Calendar.getInstance()
        finalDate.set(Calendar.YEAR, yearEnd)
        finalDate.set(Calendar.MONTH, monthEnd)
        finalDate.set(Calendar.DAY_OF_MONTH, dayEnd)

        onBindFilter(startDate.time, finalDate.time)
    }

    private fun configureRecyclerView() {
        val spanCount = if (UserPreferenceUtil.gridMode) {
            1
        } else {
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                resources.getInteger(R.integer.span_count_portrait)
            } else {
                resources.getInteger(R.integer.span_count_landscape)
            }
        }
        recycler_view.layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
    }


    private fun configureGridMode(item: MenuItem?) {
        if (UserPreferenceUtil.gridMode) {
            item?.setIcon(R.drawable.ic_drawable_list)
        } else {
            item?.setIcon(R.drawable.ic_drawable_grid)
        }
    }

    private fun configureFilter() {
        val bottomSheetDialog = BottomSheetUtil.show((activity as AppCompatActivity), R.layout.bottom_sheet_filter, false)
        val dialog = bottomSheetDialog.first
        val view = bottomSheetDialog.second
        view.linear_layout_filter.minimumHeight = (activity as AppCompatActivity).window.decorView.height

        view.image_view_back_filter.setOnClickListener {
            hideBottomSheet(dialog, view)
        }

        configureFieldsFilter(dialog, view)
    }

    protected fun showCalendar(dialog: BottomSheetDialog) {
        dialog.dismiss()
        val calendar = Calendar.getInstance()
        DatePickerDialog.newInstance(
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            maxDate = calendar
            if (UserPreferenceUtil.darkMode) {
                isThemeDark = true
            }
            show(this@BaseFragment.requireActivity().fragmentManager, ConstantsUtil.TAG)
        }
    }

    protected fun hideBottomSheet(dialog: BottomSheetDialog, view: View) {
        BottomSheetUtil.hideBottomSheet(dialog, view)
    }

    fun showMessage(message: String) {
        showMessage(message, Snackbar.LENGTH_LONG)
    }

    private fun closeFabMenu(){
        if (fabButtonUtil.fabOpen) fabButtonUtil.configureFabMenu()
    }

    fun hideFloatingButton(hide: Boolean) {
        if (hide) {
            fabButtonUtil.floatingButton.hide()
        } else {
            fabButtonUtil.floatingButton.show()
        }
    }

    abstract fun onActivityCreated()

    abstract fun resultItem(item: Serializable?)

    abstract fun onItemClick(item: V, view: View)

    abstract fun onItemLongClick(item: V, view: View)

    abstract fun setAdapter(): T

    abstract fun addOnClickListener()

    abstract fun startItemActivity(item: V, save: Boolean)

    abstract fun getItems()

    abstract fun configureFieldsFilter(dialog: BottomSheetDialog, view: View)

    abstract fun onBindSearch(newText: String?)

    abstract fun onBindFilter(startDate: Date, finalDate: Date)
}