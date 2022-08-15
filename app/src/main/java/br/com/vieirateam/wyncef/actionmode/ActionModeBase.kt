package br.com.vieirateam.wyncef.actionmode

import android.graphics.Color
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.FragmentActivity
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.util.BottomSheetUtil
import br.com.vieirateam.wyncef.util.ConstantsUtil
import kotlinx.android.synthetic.main.adapter_card_view.view.floating_button_mini
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.button_negative
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.button_positive
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.linear_layout_dialog
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.text_view_dialog_body
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.text_view_dialog_title

abstract class ActionModeBase<T>(fragmentActivity: FragmentActivity?, private val menuID: Int) : ActionMode.Callback {

    var selected = false
    private var count = 0
    private var baseItems = mutableListOf<T>()
    private lateinit var actionMode: ActionMode
    private var context = fragmentActivity as FragmentActivity

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false

    override fun onActionItemClicked(mode: ActionMode, menuItem: MenuItem) : Boolean {
        return when (menuItem.itemId) {
            R.id.menu_delete -> {
                return showBottomSheet(mode)
            }
            R.id.menu_print -> {
                actionPrintClicked()
                clearSelectedItems()
                baseItems.clear()
                mode.finish()
                true
            }
            R.id.menu_tag -> {
                actionTagClicked()
                clearSelectedItems()
                baseItems.clear()
                mode.finish()
                true
            }
            else -> false
        }
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        actionMode = mode
        val inflater = actionMode.menuInflater
        inflater.inflate(R.menu.delete, menu)

        when (menuID) {
            1 -> inflater.inflate(R.menu.print, menu)
            2 -> inflater.inflate(R.menu.tag, menu)
        }
        actionMode.title = ConstantsUtil.MODE_TITLE
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        count = 0
        selected = false
        clearSelectedItems()
        baseItems.clear()
        actionMode.finish()
        mode.finish()
        hideFloatingButton(false)
        notifyDataSetChanged()
    }

    fun selectedItem(item: T, view: View) {
        if (selected) {
            if (baseItems.contains(item)) {
                baseItems.remove(item)
                removeSelectedItem(item)
                setBackgroundColorView(true, view)
                count--
            } else {
                baseItems.add(item)
                addSelectedItem(item)
                setBackgroundColorView(false, view)
                count++
            }
        }
        if (count == 0) actionMode.finish()
        if (baseItems.size > 1) {
            setTitle("$count selecionados")
        } else {
            setTitle("$count selecionado")
        }
    }

    private fun setBackgroundColorView(remove: Boolean, view: View) {
        if (remove) {
            view.setBackgroundColor(Color.WHITE)
            view.floating_button_mini.hide()
        } else {
            view.setBackgroundColor(Color.LTGRAY)
            view.floating_button_mini.show()
        }
    }

    private fun setTitle(title: String) {
        actionMode.title = title
    }

    private fun showBottomSheet(mode: ActionMode): Boolean {
        var result = false
        val bottomSheetDialog = BottomSheetUtil.show(context, R.layout.bottom_sheet_dialog, false)
        val dialog = bottomSheetDialog.first
        val view = bottomSheetDialog.second
        view.linear_layout_dialog.minimumHeight = context.window.decorView.height

        if (baseItems.size > 1) {
            view.text_view_dialog_title.text = context.getString(R.string.alert_delete_item_title_more, count.toString())
        } else {
            view.text_view_dialog_title.text = context.getString(R.string.alert_delete_item_title_one)
        }
        view.text_view_dialog_body.text = context.getString(R.string.alert_delete_item_body)
        view.button_positive.text = context.getString(R.string.menu_delete)
        view.button_negative.text = context.getString(R.string.menu_cancel)

        view.button_positive.setOnClickListener {
            result = true
            actionDeleteClicked()
            clearSelectedItems()
            baseItems.clear()
            mode.finish()
            BottomSheetUtil.hideBottomSheet(dialog, view)
        }

        view.button_negative.setOnClickListener {
            BottomSheetUtil.hideBottomSheet(dialog, view)
        }
        return result
    }

    abstract fun addSelectedItem(item: T)

    abstract fun removeSelectedItem(item: T)

    abstract fun clearSelectedItems()

    abstract fun actionDeleteClicked()

    abstract fun actionPrintClicked()

    abstract fun actionTagClicked()

    abstract fun notifyDataSetChanged()

    abstract fun hideFloatingButton(hide: Boolean)
}