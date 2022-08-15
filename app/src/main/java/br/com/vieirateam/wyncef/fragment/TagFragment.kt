package br.com.vieirateam.wyncef.fragment

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.actionmode.ActionModeTag
import br.com.vieirateam.wyncef.activity.TagActivity
import br.com.vieirateam.wyncef.adapter.TagAdapter
import br.com.vieirateam.wyncef.entity.Agency
import br.com.vieirateam.wyncef.entity.Category
import br.com.vieirateam.wyncef.entity.Device
import br.com.vieirateam.wyncef.entity.Tag
import br.com.vieirateam.wyncef.interfaces.FilterBottomSheet
import br.com.vieirateam.wyncef.util.ConstantsUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.Serializable
import java.util.Date

class TagFragment : BaseFragment<TagAdapter, Tag>(), FilterBottomSheet {

    lateinit var adapter: TagAdapter
    private lateinit var actionMode: ActionModeTag

    override fun onActivityCreated() {
        actionMode = ActionModeTag(this)
    }

    override fun addOnClickListener() {
        val item = Tag(device = Device(agency = Agency(), category = Category(), status = "EM USO"))
        startItemActivity(item, true)
    }

    override fun resultItem(item: Serializable?) {
        item as Tag
        if (save) {
            WyncefApplication.tagViewModel.insert(item)
        } else {
            WyncefApplication.tagViewModel.update(item)
        }
    }

    override fun onItemClick(item: Tag, view: View) {
        if (actionMode.selected) {
            actionMode.selectedItem(item, view)
        } else {
            startItemActivity(item, false)
        }
    }

    override fun onItemLongClick(item: Tag, view: View) {
        if (!actionMode.selected) {
            hideFloatingButton(true)
            (view.context as AppCompatActivity).startSupportActionMode(actionMode)
            actionMode.selected = true
        }
        actionMode.selectedItem(item, view)
    }

    override fun setAdapter(): TagAdapter {
        adapter = TagAdapter(this::onItemClick, this::onItemLongClick)
        return adapter
    }

    override fun startItemActivity(item: Tag, save: Boolean) {
        bundle.putSerializable(ConstantsUtil.ITEM, item)
        bundle.putBoolean(ConstantsUtil.SAVE, save)
        bundle.putString(ConstantsUtil.TITLE, getString(R.string.app_tag))
        val intent = Intent(mView.context, TagActivity::class.java)
        intent.putExtra(ConstantsUtil.BUNDLE, bundle)
        startActivityForResult(intent, ConstantsUtil.REQUEST_CODE)
    }

    override fun getItems() {
        filter.clear()
        WyncefApplication.tagViewModel.select()
        WyncefApplication.tagViewModel.tags.observe(viewLifecycleOwner, Observer { tags ->
            tags?.let {
                adapter.setItems(it)
                filter.addAll(it)
                if (it.isEmpty()) showMessage(getString(R.string.alert_tag_empty))
            }
        })
    }

    override fun onBindSearch(newText: String?) {
        filter.clear()
        WyncefApplication.tagViewModel.filter(newText)
        WyncefApplication.tagViewModel.tags.observe(viewLifecycleOwner, Observer { tags ->
            tags?.let {
                adapter.setItems(it)
                filter.addAll(it)
            }
        })
    }

    override fun onBindFilter(startDate: Date, finalDate: Date) {
        filter.clear()
        WyncefApplication.agencyViewModel.filter(startDate, finalDate)
        WyncefApplication.tagViewModel.tags.observe(viewLifecycleOwner, Observer { tags ->
            tags?.let {
                adapter.setItems(it)
                filter.addAll(it)
            }
        })
    }

    override fun configureFieldsFilter(dialog: BottomSheetDialog, view: View) {
        configureBottomSheetFilter(dialog, view)
    }

    override fun configureAscendingOrder(dialog: BottomSheetDialog) {
        filter.sortBy { tag -> tag.make }
        adapter.setItems(filter)
        dialog.dismiss()
    }

    override fun configureDescendingOrder(dialog: BottomSheetDialog) {
        filter.sortByDescending { tag -> tag.make }
        adapter.setItems(filter)
        dialog.dismiss()
    }

    override fun configureDateFilter(dialog: BottomSheetDialog) {
        showCalendar(dialog)
    }
}