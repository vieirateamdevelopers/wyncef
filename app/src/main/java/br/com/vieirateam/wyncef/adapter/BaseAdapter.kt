package br.com.vieirateam.wyncef.adapter

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.vieirateam.wyncef.R
import kotlinx.android.synthetic.main.adapter_card_view.view.*

abstract class BaseAdapter<T> : GenericAdapter<T>(R.layout.adapter_card_view) {

    var baseItemsSelected = mutableListOf<T>()

    override fun onBindData(holder: RecyclerView.ViewHolder, item: T) {
        val view = holder.itemView

        if (baseItemsSelected.contains(item)) {
            view.setBackgroundColor(Color.LTGRAY)
            view.floating_button_mini.show()
        } else {
            view.setBackgroundColor(Color.WHITE)
            view.floating_button_mini.hide()
        }

        onItemsView(item, view)

        view.setOnClickListener {
            onClick(item, view)
        }

        view.setOnLongClickListener {
            onLongClick(item, view)
            return@setOnLongClickListener true
        }
    }

    abstract fun onItemsView(item: T, view: View)

    abstract fun onClick(item: T, view: View)

    abstract fun onLongClick(item: T, view: View)

    abstract fun setItems(baseItems: List<T>)

    abstract fun getItems(): List<T>
}