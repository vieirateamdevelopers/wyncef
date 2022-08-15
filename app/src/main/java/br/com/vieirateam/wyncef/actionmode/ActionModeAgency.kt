package br.com.vieirateam.wyncef.actionmode

import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.entity.Agency
import br.com.vieirateam.wyncef.fragment.AgencyFragment

class ActionModeAgency(private val fragment: AgencyFragment) : ActionModeBase<Agency>(fragment.activity, 0) {

    private val items = fragment.adapter.baseItemsSelected

    override fun addSelectedItem(item: Agency) {
        items.add(item)
    }

    override fun removeSelectedItem(item: Agency) {
        items.remove(item)
    }

    override fun clearSelectedItems() {
        items.clear()
    }

    override fun actionDeleteClicked() {
        for (item in items) {
            WyncefApplication.agencyViewModel.delete(item)
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