package br.com.vieirateam.wyncef.util

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.adapter.AgencyAdapter
import br.com.vieirateam.wyncef.adapter.CategoryAdapter
import br.com.vieirateam.wyncef.adapter.DeviceAdapter
import br.com.vieirateam.wyncef.adapter.IconAdapter
import br.com.vieirateam.wyncef.entity.Agency
import br.com.vieirateam.wyncef.entity.Category
import br.com.vieirateam.wyncef.entity.Device
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_list.view.*

class ListBottomSheetUtil(private val context: FragmentActivity) {

    var agencies = mutableListOf<Agency>()
    var categories = mutableListOf<Category>()
    var devices = mutableListOf<Device>()

    private var icons = mutableListOf<Category>()
    private lateinit var callbackListener : Callback
    private lateinit var iconAdapter: IconAdapter
    private lateinit var agencyAdapter: AgencyAdapter
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var bottomDialog: BottomSheetDialog

    fun setCallbackListener(callbackListener: Callback) {
        this.callbackListener = callbackListener
    }

    fun initializeList(card: Int) {
        when (card) {
            1 -> getAgencies()
            2 -> getCategories()
            3 -> getDevices()
            else -> icons = CategoryUtil.getCategories()
        }
    }

    fun showBottomSheetList(card: Int): Pair<BottomSheetDialog, View> {
        val bottomSheetDialog = BottomSheetUtil.show(context, R.layout.bottom_sheet_list, false)
        bottomDialog = bottomSheetDialog.first
        val view = bottomSheetDialog.second
        view.linear_layout_list.minimumHeight = context.window.decorView.height
        configureBottomSheetListButtons(view, card)
        return Pair(bottomDialog, view)
    }

    private fun configureBottomSheetListButtons(view: View, card: Int) {
        var search = false
        val recyclerView = view.recycler_view_bottom_list

        when (card) {
            1 -> view.text_view_list.text = context.getString(R.string.nav_agency)
            2 -> view.text_view_list.text = context.getString(R.string.nav_category)
            3 -> view.text_view_list.text = context.getString(R.string.nav_device)
            else -> {
                view.text_view_list.text = context.getString(R.string.nav_icon)
                view.image_view_add_list.isEnabled = false
            }
        }

        recyclerView.apply {
            layoutManager = when {
                card == 4 -> StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                UserPreferenceUtil.gridMode -> StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                else -> StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }
            itemAnimator = DefaultItemAnimator()
            scheduleLayoutAnimation()
            setHasFixedSize(true)
            adapter = when (card) {
                1 -> setAgencyAdapter()
                2 -> setCategoryAdapter()
                3 -> setDeviceAdapter()
                else -> setIconAdapter()
            }
        }

        view.image_view_add_list.setOnClickListener {
            when (card) {
                1 -> callbackListener.registerItem(1)
                2 -> callbackListener.registerItem(2)
                3 -> callbackListener.registerItem(3)
            }
            hideBottomSheet(bottomDialog, view)
        }

        view.image_view_search_list.setOnClickListener {
            if (search) {
                view.image_view_search_list.setImageResource(R.drawable.ic_drawable_search)
                view.text_iput_edit_text_list.visibility = View.INVISIBLE
                view.text_view_list.visibility = View.VISIBLE
                KeyboardUtil.hide(view.text_iput_edit_text_list)
                search = false

                when (card) {
                    1 -> agencyAdapter.setItems(agencies)
                    2 -> categoryAdapter.setItems(categories)
                    3 -> deviceAdapter.setItems(devices)
                    else -> iconAdapter.setItems(icons)
                }
            } else {
                view.image_view_search_list.setImageResource(R.drawable.ic_drawable_cancel)
                view.text_iput_edit_text_list.visibility = View.VISIBLE
                view.text_view_list.visibility = View.INVISIBLE
                KeyboardUtil.show(view.text_iput_edit_text_list)
                search = true
            }
        }

        view.image_view_back_list.setOnClickListener {
            if (search) {
                view.image_view_search_list.setImageResource(R.drawable.ic_drawable_search)
                view.text_iput_edit_text_list.visibility = View.INVISIBLE
                view.text_view_list.visibility = View.VISIBLE
                KeyboardUtil.hide(view.text_iput_edit_text_list)
                search = false

                when (card) {
                    1 -> agencyAdapter.setItems(agencies)
                    2 -> categoryAdapter.setItems(categories)
                    3 -> deviceAdapter.setItems(devices)
                    else -> iconAdapter.setItems(icons)
                }
            } else {
                hideBottomSheet(bottomDialog, view)
            }
        }

        view.text_iput_edit_text_list.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(editable: Editable?) {
                when (card) {
                    1 -> onAgencyBindSearch(editable.toString())
                    2 -> onCategoryBindSearch(editable.toString())
                    3 -> onDeviceBindSearch(editable.toString())
                    else -> onIconBindSearch(editable.toString())
                }
            }

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun getAgencies() {
        agencies.clear()
        WyncefApplication.agencyViewModel.select()
        WyncefApplication.agencyViewModel.agencies.observe(context, Observer { item ->
            item?.let {
                agencies.addAll(it)
            }
        })
    }

    private fun getCategories() {
        categories.clear()
        WyncefApplication.categoryViewModel.select()
        WyncefApplication.categoryViewModel.categories.observe(context, Observer { item ->
            item?.let {
                categories.addAll(it)
            }
        })
    }

    private fun getDevices() {
        devices.clear()
        WyncefApplication.deviceViewModel.select()
        WyncefApplication.deviceViewModel.devices.observe(context, Observer { item ->
            item?.let {
                devices.addAll(it)
            }
        })
    }

    private fun setIconAdapter(): IconAdapter {
        iconAdapter = IconAdapter(this.callbackListener::onIconClick)
        iconAdapter.setItems(icons)
        return iconAdapter
    }

    private fun setAgencyAdapter(): AgencyAdapter {
        agencyAdapter = AgencyAdapter(this.callbackListener::onAgencyClick, this.callbackListener::onAgencyLongClick)
        agencyAdapter.setItems(agencies)
        return agencyAdapter
    }

    private fun setCategoryAdapter(): CategoryAdapter {
        categoryAdapter = CategoryAdapter(this.callbackListener::onCategoryClick, this.callbackListener::onCategoryLongClick)
        categoryAdapter.setItems(categories)
        return categoryAdapter
    }

    private fun setDeviceAdapter() : DeviceAdapter {
        deviceAdapter = DeviceAdapter(this.callbackListener::onDeviceClick, this.callbackListener::onDeviceLongClick)
        deviceAdapter.setItems(devices)
        return deviceAdapter
    }

    @SuppressLint("DefaultLocale")
    private fun onIconBindSearch(newText: String?) {
        val query = newText?.toLowerCase().toString()
        val items = mutableListOf<Category>()

        for (item in icons) {
            val name = item.name.toLowerCase()
            if (name.contains(query)) items.add(item)
        }
        iconAdapter.setItems(items)
    }

    @SuppressLint("DefaultLocale")
    private fun onAgencyBindSearch(newText: String?) {
        val query = newText?.toLowerCase().toString()
        val items = mutableListOf<Agency>()

        for (item in agencies) {
            val name = item.name.toLowerCase()
            val cgc = item.cgc.toLowerCase()
            if (name.contains(query) || cgc.contains(query)) items.add(item)
        }
        agencyAdapter.setItems(items)
    }

    @SuppressLint("DefaultLocale")
    private fun onCategoryBindSearch(newText: String?) {
        val query = newText?.toLowerCase().toString()
        val items = mutableListOf<Category>()

        for (item in categories) {
            val name = item.name.toLowerCase()
            val cgc = item.initials.toLowerCase()
            if (name.contains(query) || cgc.contains(query)) items.add(item)
        }
        categoryAdapter.setItems(items)
    }

    @SuppressLint("DefaultLocale")
    private fun onDeviceBindSearch(newText: String?) {
        val query = newText?.toLowerCase().toString()
        val items = mutableListOf<Device>()

        for (item in devices) {
            val agencyName = item.agency.name.toLowerCase()
            val agencyCGC = item.agency.cgc.toLowerCase()
            val categoryName = item.category.name.toLowerCase()
            val categoryInitials = item.category.initials.toLowerCase()
            val serialNumber = item.serialNumber.toLowerCase()
            val logicalName = item.logicalName.toString().toLowerCase()
            val patrimony = item.patrimony.toString().toLowerCase()

            if( agencyName.contains(query) ||
                agencyCGC.contains(query) ||
                categoryName.contains(query) ||
                categoryInitials.contains(query) ||
                serialNumber.contains(query) ||
                logicalName.contains(query) ||
                patrimony.contains(query)) items.add(item)
        }
        deviceAdapter.setItems(items)
    }

    private fun hideBottomSheet(dialog: BottomSheetDialog, view: View) {
        BottomSheetUtil.hideBottomSheet(dialog, view)
    }

    interface Callback {

        fun registerItem(card: Int)

        fun onIconClick(icon: Int, view: View)

        fun onAgencyClick(item: Agency, view: View)

        fun onAgencyLongClick(item: Agency, view: View)

        fun onCategoryClick(item: Category, view: View)

        fun onCategoryLongClick(item: Category, view: View)

        fun onDeviceClick(item: Device, view: View)

        fun onDeviceLongClick(item: Device, view: View)

    }
}