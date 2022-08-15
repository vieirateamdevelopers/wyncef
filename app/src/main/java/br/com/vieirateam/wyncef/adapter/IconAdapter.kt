package br.com.vieirateam.wyncef.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.entity.Category
import kotlinx.android.synthetic.main.adapter_card_view_icon.view.image_view_icon2
import kotlinx.android.synthetic.main.adapter_card_view_icon.view.text_view_icon

class IconAdapter(val onItemClick: (Int, View) -> Unit) : GenericAdapter<Category>(R.layout.adapter_card_view_icon) {

    override fun onBindData(holder: RecyclerView.ViewHolder, item: Category) {
        val view = holder.itemView
        view.image_view_icon2.setImageResource(item.icon)
        view.text_view_icon.text = item.name

        view.setOnClickListener {
            onItemClick(item.icon, view)
        }
    }

    fun setItems(baseItems: List<Category>) {
        this.genericItems = baseItems
        notifyDataSetChanged()
    }
}