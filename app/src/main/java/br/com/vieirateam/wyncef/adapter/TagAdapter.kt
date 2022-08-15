package br.com.vieirateam.wyncef.adapter

import android.annotation.SuppressLint
import android.view.View
import br.com.vieirateam.wyncef.entity.Tag
import br.com.vieirateam.wyncef.R
import kotlinx.android.synthetic.main.adapter_card_view.view.*

class TagAdapter(val onItemClick: (Tag, View) -> Unit, val onItemLongClick: (Tag, View) -> Unit) : BaseAdapter<Tag>() {

    @SuppressLint("SetTextI18n")
    override fun onItemsView(item: Tag, view: View) {
        view.image_view.setImageResource(item.device.category.icon)
        view.text_view_1.text = "${item.device.agency.state}${item.device.agency.cgc}${item.device.category.initials}${item.device.logicalName}"

        if (item.make.isEmpty()) {
            view.text_view_2.text = view.context.getString(R.string.text_view_without_tag_make)
        } else {
            view.text_view_2.text = item.make
        }
        if (item.model.isEmpty()) {
            view.text_view_3.text = view.context.getString(R.string.text_view_without_tag_model)
        } else {
            view.text_view_3.text = item.model
        }
        view.image_view_tag.visibility = View.VISIBLE

        if (item.complete) {
            view.image_view_tag.setImageResource(R.drawable.ic_drawable_device_printer_on)
        } else {
            view.image_view_tag.setImageResource(R.drawable.ic_drawable_device_printer_off)
        }
    }

    override fun onClick(item: Tag, view: View) {
        onItemClick(item, view)
    }

    override fun onLongClick(item: Tag, view: View) {
        onItemLongClick(item, view)
    }

    override fun setItems(baseItems: List<Tag>) {
        this.genericItems = baseItems
        notifyDataSetChanged()
    }

    override fun getItems(): List<Tag> = this.genericItems
}