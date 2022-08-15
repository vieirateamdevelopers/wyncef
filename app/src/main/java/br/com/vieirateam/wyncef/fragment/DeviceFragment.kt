package br.com.vieirateam.wyncef.fragment

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.actionmode.ActionModeDevice
import br.com.vieirateam.wyncef.activity.DeviceActivity
import br.com.vieirateam.wyncef.adapter.DeviceAdapter
import br.com.vieirateam.wyncef.entity.Agency
import br.com.vieirateam.wyncef.entity.Category
import br.com.vieirateam.wyncef.entity.Device
import br.com.vieirateam.wyncef.interfaces.FilterBottomSheet
import br.com.vieirateam.wyncef.util.ConstantsUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.Date

class DeviceFragment : BaseFragment<DeviceAdapter, Device>(), FilterBottomSheet {

    lateinit var adapter: DeviceAdapter
    private lateinit var actionMode: ActionModeDevice

    override fun onActivityCreated() {
        actionMode = ActionModeDevice(this)
    }

    override fun addOnClickListener() {
        startItemActivity(Device(agency = Agency(), category = Category()), true)
    }

    override fun resultItem(item: Serializable?) {
        val device = item as Device
        if (save) {
            WyncefApplication.deviceViewModel.insert(device)
        } else {
            WyncefApplication.deviceViewModel.update(device)
            updateItem(device)
        }
    }

    override fun onItemClick(item: Device, view: View) {
        if (actionMode.selected) {
            actionMode.selectedItem(item, view)
        } else {
            startItemActivity(item, false)
        }
    }

    override fun onItemLongClick(item: Device, view: View) {
        if (!actionMode.selected) {
            hideFloatingButton(true)
            (view.context as AppCompatActivity).startSupportActionMode(actionMode)
            actionMode.selected = true
        }
        actionMode.selectedItem(item, view)
    }

    override fun setAdapter(): DeviceAdapter {
        adapter = DeviceAdapter(this::onItemClick, this::onItemLongClick)
        return adapter
    }

    override fun onBindSearch(newText: String?) {
        filter.clear()
        WyncefApplication.deviceViewModel.filter(newText)
        WyncefApplication.deviceViewModel.devices.observe(viewLifecycleOwner, Observer { devices ->
            devices?.let {
                adapter.setItems(it)
                filter.addAll(it)
            }
        })
    }

    override fun onBindFilter(startDate: Date, finalDate: Date) {
        filter.clear()
        WyncefApplication.deviceViewModel.filter(startDate, finalDate)
        WyncefApplication.deviceViewModel.devices.observe(viewLifecycleOwner, Observer { devices ->
            devices?.let {
                adapter.setItems(it)
                filter.addAll(it)
            }
        })
    }

    override fun startItemActivity(item: Device, save: Boolean) {
        bundle.putSerializable(ConstantsUtil.ITEM, item)
        bundle.putBoolean(ConstantsUtil.SAVE, save)
        bundle.putString(ConstantsUtil.TITLE, getString(R.string.app_device))
        val intent = Intent(mView.context, DeviceActivity::class.java)
        intent.putExtra(ConstantsUtil.BUNDLE, bundle)
        startActivityForResult(intent, ConstantsUtil.REQUEST_CODE)
    }

    override fun getItems() {
        filter.clear()
        WyncefApplication.deviceViewModel.select()
        WyncefApplication.deviceViewModel.devices.observe(viewLifecycleOwner, Observer { devices ->
            devices?.let {
                adapter.setItems(it)
                filter.addAll(it)
                if (it.isEmpty()) showMessage(getString(R.string.alert_device_empty))
            }
        })
    }

    override fun configureFieldsFilter(dialog: BottomSheetDialog, view: View) {
        configureBottomSheetFilter(dialog, view)
    }

    override fun configureAscendingOrder(dialog: BottomSheetDialog) {
        filter.sortBy { device -> device.serialNumber }
        adapter.setItems(filter)
        dialog.dismiss()
    }

    override fun configureDescendingOrder(dialog: BottomSheetDialog) {
        filter.sortByDescending { device -> device.serialNumber }
        adapter.setItems(filter)
        dialog.dismiss()
    }

    override fun configureDateFilter(dialog: BottomSheetDialog) {
        showCalendar(dialog)
    }

    private fun updateItem(device: Device) {
        WyncefApplication.itemViewModel.getScope().launch(Dispatchers.Main) {
            val item = WyncefApplication.itemViewModel.selectItem(device.serialNumber)
            val tag = WyncefApplication.tagViewModel.selectTag(device.serialNumber)
            if (item != null) {
                item.device = device
                WyncefApplication.itemViewModel.update(item)
            }
            if (tag != null) {
                tag.device = device
                WyncefApplication.tagViewModel.update(tag)
            }
        }
    }
}