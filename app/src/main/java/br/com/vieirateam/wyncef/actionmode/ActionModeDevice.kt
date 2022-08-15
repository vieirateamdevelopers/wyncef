package br.com.vieirateam.wyncef.actionmode

import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.entity.Device
import br.com.vieirateam.wyncef.entity.Tag
import br.com.vieirateam.wyncef.fragment.DeviceFragment

class ActionModeDevice(private val fragment: DeviceFragment) : ActionModeBase<Device>(fragment.activity, 2) {

    private val items = fragment.adapter.baseItemsSelected

    override fun addSelectedItem(item: Device) {
        items.add(item)
    }

    override fun removeSelectedItem(item: Device) {
        items.remove(item)
    }

    override fun clearSelectedItems() {
        items.clear()
    }

    override fun actionDeleteClicked() {
        for (item in items) {
            WyncefApplication.deviceViewModel.delete(item)
        }
    }

    override fun actionTagClicked() {
        for (item in items) {
            if (item.logicalName != null) {
                val tag = Tag(device = item)
                WyncefApplication.tagViewModel.insert(tag)
            }
        }
        fragment.showMessage(fragment.mView.context.getString(R.string.alert_complete_print_ok))
    }

    override fun notifyDataSetChanged() {
        fragment.adapter.notifyDataSetChanged()
    }

    override fun hideFloatingButton(hide: Boolean) {
        fragment.hideFloatingButton(hide)
    }

    override fun actionPrintClicked() {}
}