package br.com.vieirateam.wyncef.actionmode

import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.entity.Tag
import br.com.vieirateam.wyncef.fragment.TagFragment
import br.com.vieirateam.wyncef.util.DOCXFileUtil

class ActionModeTag(private val fragment: TagFragment) : ActionModeBase<Tag>(fragment.activity, 1) {

    private val items = fragment.adapter.baseItemsSelected

    override fun addSelectedItem(item: Tag) {
        items.add(item)
    }

    override fun removeSelectedItem(item: Tag) {
        items.remove(item)
    }

    override fun clearSelectedItems() {
        items.clear()
    }

    override fun actionDeleteClicked() {
        for (item in items) {
            WyncefApplication.tagViewModel.delete(item)
        }
    }

    override fun actionPrintClicked() {
        val temp = mutableListOf<Tag>()
        for (item in items) {
            if (item.make.isNotEmpty() &&
                item.model.isNotEmpty()) {
                temp.add(item)
            }
        }
        if (temp.isNotEmpty()) {
            DOCXFileUtil.print(fragment.requireActivity(), fragment.mView, temp)
        }
    }

    override fun notifyDataSetChanged() {
        fragment.adapter.notifyDataSetChanged()
    }

    override fun hideFloatingButton(hide: Boolean) {
        fragment.hideFloatingButton(hide)
    }

    override fun actionTagClicked() {}
}