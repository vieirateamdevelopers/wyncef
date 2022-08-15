package br.com.vieirateam.wyncef.adapter

import android.view.View
import br.com.vieirateam.wyncef.entity.Category
import br.com.vieirateam.wyncef.util.DateFormatUtil
import kotlinx.android.synthetic.main.adapter_card_view.view.image_view
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_1
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_2
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view_3

class CategoryAdapter(val onItemClick: (Category, View) -> Unit, val onItemLongClick: (Category, View) -> Unit) : BaseAdapter<Category>() {

    override fun onItemsView(item: Category, view: View) {
        view.image_view.setImageResource(item.icon)
        view.text_view_1.text = item.name
        view.text_view_2.text = item.initials
        view.text_view_3.text = DateFormatUtil.format(item.date, "dd/MM/yyyy")
    }

    override fun onClick(item: Category, view: View) {
        onItemClick(item, view)
    }

    override fun onLongClick(item: Category, view: View) {
        onItemLongClick(item, view)
    }

    override fun setItems(baseItems: List<Category>) {
        this.genericItems = baseItems
        notifyDataSetChanged()
    }

    override fun getItems(): List<Category> = this.genericItems
}