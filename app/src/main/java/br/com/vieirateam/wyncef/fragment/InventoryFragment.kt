package br.com.vieirateam.wyncef.fragment

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.actionmode.ActionModeInventory
import br.com.vieirateam.wyncef.activity.InventoryActivity
import br.com.vieirateam.wyncef.activity.MainActivity
import br.com.vieirateam.wyncef.adapter.InventoryAdapter
import br.com.vieirateam.wyncef.entity.Agency
import br.com.vieirateam.wyncef.entity.Inventory
import br.com.vieirateam.wyncef.interfaces.FilterBottomSheet
import br.com.vieirateam.wyncef.util.ConstantsUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.Date

class InventoryFragment : BaseFragment<InventoryAdapter, Inventory>(), FilterBottomSheet {

    lateinit var adapter: InventoryAdapter
    private lateinit var actionMode: ActionModeInventory

    override fun onActivityCreated() {
        actionMode = ActionModeInventory(this)
    }

    override fun addOnClickListener() {
        startItemActivity(Inventory(agency = Agency()))
    }

    override fun resultItem(item: Serializable?) {
        item as Inventory
        WyncefApplication.inventoryViewModel.insert(item)
        configureStartItemsFragment(null)
    }

    override fun onItemClick(item: Inventory, view: View) {
        if (actionMode.selected) {
            actionMode.selectedItem(item, view)
        } else {
            configureStartItemsFragment(item)
        }
    }

    override fun onItemLongClick(item: Inventory, view: View) {
        if (!actionMode.selected) {
            hideFloatingButton(true)
            (view.context as AppCompatActivity).startSupportActionMode(actionMode)
            actionMode.selected = true
        }
        actionMode.selectedItem(item, view)
    }

    override fun setAdapter(): InventoryAdapter {
        adapter = InventoryAdapter(this::onItemClick, this::onItemLongClick)
        return adapter
    }

    override fun onBindSearch(newText: String?) {
        filter.clear()
        WyncefApplication.inventoryViewModel.filter(newText)
        WyncefApplication.inventoryViewModel.inventories.observe(viewLifecycleOwner, Observer { inventories ->
            inventories?.let {
                adapter.setItems(it)
                filter.addAll(it)
            }
        })
    }

    override fun onBindFilter(startDate: Date, finalDate: Date) {
        filter.clear()
        WyncefApplication.inventoryViewModel.filter(startDate, finalDate)
        WyncefApplication.inventoryViewModel.inventories.observe(viewLifecycleOwner, Observer { inventories ->
            inventories?.let {
                adapter.setItems(it)
                filter.addAll(it)
            }
        })
    }

    private fun startItemActivity(item: Inventory) {
        bundle.putSerializable(ConstantsUtil.ITEM, item)
        bundle.putBoolean(ConstantsUtil.SAVE, true)
        bundle.putString(ConstantsUtil.TITLE, getString(R.string.app_inventory))
        val intent = Intent(mView.context, InventoryActivity::class.java)
        intent.putExtra(ConstantsUtil.BUNDLE, bundle)
        startActivityForResult(intent, ConstantsUtil.REQUEST_CODE)
    }

    override fun getItems() {
        filter.clear()
        WyncefApplication.inventoryViewModel.select()
        WyncefApplication.inventoryViewModel.inventories.observe(viewLifecycleOwner, Observer { inventories ->
            inventories?.let {
                adapter.setItems(it)
                filter.addAll(it)
                if (it.isEmpty()) showMessage(getString(R.string.alert_inventory_empty))
            }
        })
    }

    override fun configureFieldsFilter(dialog: BottomSheetDialog, view: View) {
        configureBottomSheetFilter(dialog, view)
    }

    override fun configureAscendingOrder(dialog: BottomSheetDialog) {
        filter.sortBy { inventory -> inventory.agency.name }
        adapter.setItems(filter)
        dialog.dismiss()
    }

    override fun configureDescendingOrder(dialog: BottomSheetDialog) {
        filter.sortByDescending { inventory -> inventory.agency.name }
        adapter.setItems(filter)
        dialog.dismiss()
    }

    override fun configureDateFilter(dialog: BottomSheetDialog) {
        showCalendar(dialog)
    }

    private fun configureStartItemsFragment(inventory: Inventory?) {
        if (inventory == null) {
            WyncefApplication.inventoryViewModel.getScope().launch(Dispatchers.Main) {
                val item = WyncefApplication.inventoryViewModel.selectLastInsert()
                if (item != null) {
                    bundle.putSerializable(ConstantsUtil.ITEM, item)
                    bundle.putBoolean(ConstantsUtil.FAB, true)
                    val fragment = ItemsFragment()
                    fragment.arguments = bundle
                    (activity as MainActivity).startFragment(fragment)
                }
            }
        } else {
            bundle.putSerializable(ConstantsUtil.ITEM, inventory)
            bundle.putBoolean(ConstantsUtil.FAB, true)
            val fragment = ItemsFragment()
            fragment.arguments = bundle
            (activity as MainActivity).startFragment(fragment)
        }
    }

    override fun startItemActivity(item: Inventory, save: Boolean) {}
}