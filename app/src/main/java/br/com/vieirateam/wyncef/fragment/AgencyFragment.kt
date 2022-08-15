package br.com.vieirateam.wyncef.fragment

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import br.com.vieirateam.wyncef.actionmode.ActionModeAgency
import br.com.vieirateam.wyncef.adapter.AgencyAdapter
import br.com.vieirateam.wyncef.entity.Agency
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.activity.AgencyActivity
import br.com.vieirateam.wyncef.interfaces.FilterBottomSheet
import br.com.vieirateam.wyncef.util.ConstantsUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.Serializable
import java.util.Date

class AgencyFragment : BaseFragment<AgencyAdapter, Agency>(), FilterBottomSheet {

    lateinit var adapter: AgencyAdapter
    private lateinit var actionMode: ActionModeAgency

    override fun onActivityCreated() {
        actionMode = ActionModeAgency(this)
    }

    override fun addOnClickListener() {
        startItemActivity(Agency(), true)
    }

    override fun resultItem(item: Serializable?) {
        item as Agency
        if (save) {
            WyncefApplication.agencyViewModel.insert(item)
        } else {
            WyncefApplication.agencyViewModel.update(item)
        }
    }

    override fun onItemClick(item: Agency, view: View) {
        if (actionMode.selected) {
            actionMode.selectedItem(item, view)
        } else {
            startItemActivity(item, false)
        }
    }

    override fun onItemLongClick(item: Agency, view: View) {
        if (!actionMode.selected) {
            hideFloatingButton(true)
            (view.context as AppCompatActivity).startSupportActionMode(actionMode)
            actionMode.selected = true
        }
        actionMode.selectedItem(item, view)
    }

    override fun setAdapter(): AgencyAdapter {
        adapter = AgencyAdapter(this::onItemClick, this::onItemLongClick)
        return adapter
    }

    override fun onBindSearch(newText: String?) {
        filter.clear()
        WyncefApplication.agencyViewModel.filter(newText)
        WyncefApplication.agencyViewModel.agencies.observe(viewLifecycleOwner, Observer { agencies ->
            agencies?.let {
                adapter.setItems(it)
                filter.addAll(it)
            }
        })
    }

    override fun onBindFilter(startDate: Date, finalDate: Date) {
        filter.clear()
        WyncefApplication.agencyViewModel.filter(startDate, finalDate)
        WyncefApplication.agencyViewModel.agencies.observe(viewLifecycleOwner, Observer { agencies ->
            agencies?.let {
                adapter.setItems(it)
                filter.addAll(it)
            }
        })
    }

    override fun startItemActivity(item: Agency, save: Boolean) {
        bundle.putSerializable(ConstantsUtil.ITEM, item)
        bundle.putBoolean(ConstantsUtil.SAVE, save)
        bundle.putString(ConstantsUtil.TITLE, getString(R.string.app_agency))
        val intent = Intent(mView.context, AgencyActivity::class.java)
        intent.putExtra(ConstantsUtil.BUNDLE, bundle)
        startActivityForResult(intent, ConstantsUtil.REQUEST_CODE)
    }

    override fun getItems() {
        filter.clear()
        WyncefApplication.agencyViewModel.select()
        WyncefApplication.agencyViewModel.agencies.observe(viewLifecycleOwner, Observer { agencies ->
            agencies?.let {
                adapter.setItems(it)
                filter.addAll(it)
                if (it.isEmpty()) showMessage(getString(R.string.alert_agency_empty))
            }
        })
    }

    override fun configureFieldsFilter(dialog: BottomSheetDialog, view: View) {
        configureBottomSheetFilter(dialog, view)
    }

    override fun configureAscendingOrder(dialog: BottomSheetDialog) {
        filter.sortBy { agency -> agency.name }
        adapter.setItems(filter)
        dialog.dismiss()
    }

    override fun configureDescendingOrder(dialog: BottomSheetDialog) {
        filter.sortByDescending { agency -> agency.name }
        adapter.setItems(filter)
        dialog.dismiss()
    }

    override fun configureDateFilter(dialog: BottomSheetDialog) {
        showCalendar(dialog)
    }
}