package br.com.vieirateam.wyncef.actionmode

import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.entity.Category
import br.com.vieirateam.wyncef.fragment.CategoryFragment

class ActionModeCategory(private val fragment: CategoryFragment) : ActionModeBase<Category>(fragment.activity, 0) {

    private val items = fragment.adapter.baseItemsSelected

    override fun addSelectedItem(item: Category) {
        items.add(item)
    }

    override fun removeSelectedItem(item: Category) {
        items.remove(item)
    }

    override fun clearSelectedItems() {
        items.clear()
    }

    override fun actionDeleteClicked() {
        for (item in items) {
            WyncefApplication.categoryViewModel.delete(item)
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