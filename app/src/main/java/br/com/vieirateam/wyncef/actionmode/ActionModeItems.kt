package br.com.vieirateam.wyncef.actionmode

import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.entity.Item
import br.com.vieirateam.wyncef.entity.Tag
import br.com.vieirateam.wyncef.fragment.ItemsFragment
import br.com.vieirateam.wyncef.R

class ActionModeItems(private val fragment: ItemsFragment) : ActionModeBase<Item>(fragment.activity,2) {

    private val items = fragment.adapter.baseItemsSelected

    override fun addSelectedItem(item: Item) {
        items.add(item)
    }

    override fun removeSelectedItem(item: Item) {
        items.remove(item)
    }

    override fun clearSelectedItems() {
        items.clear()
    }

    override fun actionDeleteClicked() {
        for (item in items) {
            WyncefApplication.itemViewModel.delete(item)
        }
    }

    override fun actionTagClicked() {
        for (item in items) {
            if (item.device.logicalName != null) {
                val tag = Tag(device = item.device)
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