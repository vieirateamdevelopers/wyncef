package br.com.vieirateam.wyncef.adapter

import android.annotation.SuppressLint
import android.view.View
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.entity.Inventory
import br.com.vieirateam.wyncef.util.DateFormatUtil
import kotlinx.android.synthetic.main.adapter_card_view.view.image_view
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_1
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_2
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_3

class InventoryAdapter(val onItemClick: (Inventory, View) -> Unit, val onItemLongClick: (Inventory, View) -> Unit) : BaseAdapter<Inventory>() {

    @SuppressLint("SetTextI18n")
    override fun onItemsView(item: Inventory, view: View) {
        view.image_view.setImageResource(R.drawable.ic_drawable_inventory)
        view.text_view_1.text = item.agency.cgc
        view.text_view_2.text = "${item.agency.name}, ${item.agency.state}"
        view.text_view_3.text = DateFormatUtil.format(item.date, "dd/MM/yyyy")
    }

    override fun onClick(item: Inventory, view: View) {
        onItemClick(item, view)
    }

    override fun onLongClick(item: Inventory, view: View) {
        onItemLongClick(item, view)
    }

    override fun setItems(baseItems: List<Inventory>) {
        this.genericItems = baseItems
        notifyDataSetChanged()
    }

    override fun getItems(): List<Inventory> = this.genericItems
}