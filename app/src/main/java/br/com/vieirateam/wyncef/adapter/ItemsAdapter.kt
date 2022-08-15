package br.com.vieirateam.wyncef.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.entity.Item
import kotlinx.android.synthetic.main.adapter_card_view.view.image_view
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_1
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_2
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_3

class ItemsAdapter(val onItemClick: (Item, View) -> Unit, val onItemLongClick: (Item, View) -> Unit) : BaseAdapter<Item>() {

    @SuppressLint("SetTextI18n")
    override fun onItemsView(item: Item, view: View) {
        view.image_view.setImageResource(item.device.category.icon)
        view.text_view_1.text = item.device.serialNumber

        if (item.device.logicalName == null) {
            view.text_view_2.text = view.context.getString(R.string.text_view_without_logical_name)
        } else {
            view.text_view_2.text = "${item.device.agency.state}${item.device.agency.cgc}${item.device.category.initials}${item.device.logicalName}"
        }

        if (item.verified) {
            view.text_view_3.text = view.context.getString(R.string.text_view_items_verified_true)
            view.text_view_3.setTextColor(Color.GREEN)
        } else {
            view.text_view_3.text = view.context.getString(R.string.text_view_items_verified_false)
            view.text_view_3.setTextColor(Color.RED)
        }
    }

    override fun onClick(item: Item, view: View) {
        onItemClick(item, view)
    }

    override fun onLongClick(item: Item, view: View) {
        onItemLongClick(item, view)
    }

    override fun setItems(baseItems: List<Item>) {
        this.genericItems = baseItems
        notifyDataSetChanged()
    }

    override fun getItems(): List<Item> = this.genericItems

}