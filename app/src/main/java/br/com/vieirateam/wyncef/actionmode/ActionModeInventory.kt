package br.com.vieirateam.wyncef.actionmode

import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.entity.Inventory
import br.com.vieirateam.wyncef.fragment.InventoryFragment

class ActionModeInventory(private val fragment: InventoryFragment) : ActionModeBase<Inventory>(fragment.activity, 0) {

    private val items = fragment.adapter.baseItemsSelected

    override fun addSelectedItem(item: Inventory) {
        items.add(item)
    }

    override fun removeSelectedItem(item: Inventory) {
        items.remove(item)
    }

    override fun clearSelectedItems() {
        items.clear()
    }

    override fun actionDeleteClicked() {
        for (item in items) {
            WyncefApplication.inventoryViewModel.delete(item)
            WyncefApplication.itemViewModel.deleteItems(item.id)
        }
    }

    override fun notifyDataSetChanged() {
        fragment.adapter.notifyDataSetChanged()
    }

    override fun hideFloatingButton(hide: Boolean) {
        fragment.hideFloatingButton(hide)
    }

    override fun actionPrintClicked() {}

    override fun actionTagClicked() {}
}