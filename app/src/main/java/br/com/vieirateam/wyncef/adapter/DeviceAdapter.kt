package br.com.vieirateam.wyncef.adapter

import android.annotation.SuppressLint
import android.view.View
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.entity.Device
import kotlinx.android.synthetic.main.adapter_card_view.view.image_view
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_1
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_2
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_3

class DeviceAdapter(val onItemClick: (Device, View) -> Unit, val onItemLongClick: (Device, View) -> Unit) : BaseAdapter<Device>() {

    @SuppressLint("SetTextI18n")
    override fun onItemsView(item: Device, view: View) {
        view.image_view.setImageResource(item.category.icon)
        view.text_view_1.text = "${item.category.initials} - ${item.category.name}"
        view.text_view_2.text = item.serialNumber

        if (item.logicalName == null) {
            view.text_view_3.text = view.context.getString(R.string.text_view_without_logical_name)
        } else {
            view.text_view_3.text = "${item.agency.state}${item.agency.cgc}${item.category.initials}${item.logicalName}"
        }
    }

    override fun onClick(item: Device, view: View) {
        onItemClick(item, view)
    }

    override fun onLongClick(item: Device, view: View) {
        onItemLongClick(item, view)
    }

    override fun setItems(baseItems: List<Device>) {
        this.genericItems = baseItems
        notifyDataSetChanged()
    }

    override fun getItems(): List<Device> = this.genericItems
}