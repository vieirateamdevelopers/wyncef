package br.com.vieirateam.wyncef.adapter

import android.annotation.SuppressLint
import android.view.View
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.entity.Agency
import br.com.vieirateam.wyncef.util.DateFormatUtil
import kotlinx.android.synthetic.main.adapter_card_view.view.image_view
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_1
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_2
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_3

class AgencyAdapter(val onItemClick: (Agency, View) -> Unit, val onItemLongClick: (Agency, View) -> Unit) : BaseAdapter<Agency>() {

    @SuppressLint("SetTextI18n")
    override fun onItemsView(item: Agency, view: View) {
        view.image_view.setImageResource(R.drawable.ic_drawable_agency)
        view.text_view_1.text = item.cgc
        view.text_view_2.text = "${item.name}, ${item.state}"
        view.text_view_3.text = DateFormatUtil.format(item.date, "dd/MM/yyyy")
    }

    override fun onClick(item: Agency, view: View) {
        onItemClick(item, view)
    }

    override fun onLongClick(item: Agency, view: View) {
        onItemLongClick(item, view)
    }

    override fun setItems(baseItems: List<Agency>) {
        this.genericItems = baseItems
        notifyDataSetChanged()
    }

    override fun getItems(): List<Agency> = this.genericItems
}