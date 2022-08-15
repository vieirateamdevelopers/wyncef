package br.com.vieirateam.wyncef.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import br.com.vieirateam.wyncef.actionmode.ActionModeItems
import br.com.vieirateam.wyncef.adapter.ItemsAdapter
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.activity.BaseActivity
import br.com.vieirateam.wyncef.activity.DeviceActivity
import br.com.vieirateam.wyncef.activity.MainActivity
import br.com.vieirateam.wyncef.entity.*
import br.com.vieirateam.wyncef.interfaces.FilterBottomSheet
import br.com.vieirateam.wyncef.util.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.Date

class ItemsFragment : BaseFragment<ItemsAdapter, Item>(), FilterBottomSheet {

    lateinit var inventory: Inventory
    lateinit var adapter: ItemsAdapter
    var bottomDialog: BottomSheetDialog? = null
    private lateinit var actionMode: ActionModeItems
    private lateinit var context: FragmentActivity

    override fun onActivityCreated() {
        context = this.requireActivity()
        context.title = getString(R.string.app_inventory_items)
        (context as MainActivity).setHomeAsUpIndicator(R.drawable.ic_drawable_back)
        (context as MainActivity).indicator = false
        actionMode = ActionModeItems(this)

        fabButtonUtil.floatingButtonScan.setOnClickListener {
            scanOnClickListener()
        }

        fabButtonUtil.floatingButtonImport.setOnClickListener {
            importOnClickListener()
        }

        fabButtonUtil.floatingButtonExport.setOnClickListener {
            exportOnClickListener()
        }

        if (inventory.importFile) showBottomSheet(true, getString(R.string.alert_dialog_import))
    }

    @SuppressLint("DefaultLocale")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        val resultScan = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent)
        if (resultScan != null) {
            val text = resultScan.contents.toString()
            if (text.isNotEmpty()) {
                resultScanCode(text.toUpperCase())
            }
            return
        }
        intent?.let {
            when (requestCode) {
                ConstantsUtil.REQUEST_CODE_READ -> {
                    configureImportFile(intent)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            ConstantsUtil.REQUEST_CODE_READ -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    configureIntent()
                } else {
                    showMessage(getString(R.string.alert_persmissions_read), Snackbar.LENGTH_LONG)
                }
            }
            ConstantsUtil.REQUEST_CODE_WRITE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    configureExportFile()
                } else {
                    showMessage(getString(R.string.alert_persmissions_write), Snackbar.LENGTH_LONG)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun addOnClickListener() {
        if (adapter.getItems().isEmpty()) {
            showMessage(getString(R.string.alert_import_not_exists))
        } else {
            val device = Device(agency = inventory.agency, category = Category(), status = "EM USO")
            startItemActivity(Item(inventory = inventory, device = device), true)
        }
    }

    private fun scanOnClickListener() {
        if (adapter.getItems().isEmpty()) {
            showMessage(getString(R.string.alert_import_not_exists))
        } else {
            IntentIntegrator(this.requireActivity()).apply {
                setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                setPrompt(getString(R.string.text_view_scan))
                captureActivity = BaseActivity.ScanCodeActivity::class.java
                setBeepEnabled(false)
                setCameraId(0)
                initiateScan()
            }
        }
    }

    private fun importOnClickListener() {
        if (adapter.getItems().isEmpty()) {
            showBottomSheet(true, getString(R.string.alert_dialog_import))
        } else {
            showMessage(getString(R.string.alert_import_exists))
        }
    }

    private fun exportOnClickListener() {
        if (adapter.getItems().isEmpty()) {
            showMessage(getString(R.string.alert_import_not_exists))
        } else {
            var result = false
            for (mItem in adapter.getItems()) {
                if (!mItem.verified) {
                    result = true
                    break
                }
            }
            if (result) {
                showMessage(getString(R.string.alert_items_verified))
            } else {
                showBottomSheet(false, getString(R.string.alert_dialog_export))
            }
        }
    }

    override fun resultItem(item: Serializable?) {
        WyncefApplication.itemViewModel.getScope().launch(Dispatchers.Main) {
            val device = item as Device
            val mItem = WyncefApplication.itemViewModel.selectItem(inventory.id, device.serialNumber)
            val order = getInventoryOrder()

            if (mItem == null) {
                val newItem = Item(inventory = inventory, device = device, verified = true, order = order)
                WyncefApplication.deviceViewModel.insert(device)
                WyncefApplication.itemViewModel.insert(newItem)
            } else {
                val oldDevice = WyncefApplication.deviceViewModel.selectDevice(device.serialNumber)
                val oldTag = WyncefApplication.tagViewModel.selectTag(device.serialNumber)

                if (oldDevice != null) {
                    oldDevice.agency = device.agency
                    oldDevice.category = device.category
                    oldDevice.serialNumber = device.serialNumber
                    oldDevice.logicalName = device.logicalName
                    oldDevice.patrimony = device.patrimony
                    oldDevice.status = device.status
                    oldDevice.date = device.date
                    mItem.device = oldDevice
                    mItem.verified = true
                    WyncefApplication.deviceViewModel.update(oldDevice)
                    WyncefApplication.itemViewModel.update(mItem)
                    if (oldTag != null) {
                        oldTag.device = oldDevice
                        WyncefApplication.tagViewModel.update(oldTag)
                    }
                }
            }
        }
    }

    override fun onItemClick(item: Item, view: View) {
        if (actionMode.selected) {
            actionMode.selectedItem(item, view)
        } else {
            startItemActivity(item, false)
        }
    }

    override fun onItemLongClick(item: Item, view: View) {
        if (!actionMode.selected) {
            hideFloatingButton(true)
            (view.context as AppCompatActivity).startSupportActionMode(actionMode)
            actionMode.selected = true
        }
        actionMode.selectedItem(item, view)
    }

    override fun setAdapter(): ItemsAdapter {
        adapter = ItemsAdapter(this::onItemClick, this::onItemLongClick)
        return adapter
    }

    override fun getItems() {
        filter.clear()
        inventory = arguments?.getSerializable(ConstantsUtil.ITEM) as Inventory
        WyncefApplication.itemViewModel.select(inventory.id)
        WyncefApplication.itemViewModel.items.observe(viewLifecycleOwner, Observer { items ->
            items?.let {
                adapter.setItems(it)
                filter.addAll(it)
                if (it.isEmpty()) showMessage(getString(R.string.alert_items_empty))
            }
        })
    }

    override fun onBindSearch(newText: String?) {
        filter.clear()
        WyncefApplication.itemViewModel.filter(inventory.id, newText)
        WyncefApplication.itemViewModel.items.observe(viewLifecycleOwner, Observer { items ->
            items?.let {
                adapter.setItems(it)
                filter.addAll(it)
            }
        })
    }

    override fun onBindFilter(startDate: Date, finalDate: Date) {
        filter.clear()
        WyncefApplication.itemViewModel.filter(inventory.id, startDate, finalDate)
        WyncefApplication.itemViewModel.items.observe(viewLifecycleOwner, Observer { items ->
            items?.let {
                adapter.setItems(it)
                filter.addAll(it)
            }
        })
    }

    override fun startItemActivity(item: Item, save: Boolean) {
        bundle.putSerializable(ConstantsUtil.ITEM, item.device)
        bundle.putBoolean(ConstantsUtil.SAVE, save)
        bundle.putString(ConstantsUtil.TITLE, getString(R.string.app_device))
        val intent = Intent(mView.context, DeviceActivity::class.java)
        intent.putExtra(ConstantsUtil.BUNDLE, bundle)
        startActivityForResult(intent, ConstantsUtil.REQUEST_CODE)
    }

    override fun configureFieldsFilter(dialog: BottomSheetDialog, view: View) {
        configureBottomSheetFilter(dialog, view)
    }

    override fun configureAscendingOrder(dialog: BottomSheetDialog) {
        filter.sortBy { it.device.serialNumber }
        adapter.setItems(filter)
        dialog.dismiss()
    }

    override fun configureDescendingOrder(dialog: BottomSheetDialog) {
        filter.sortByDescending { it.device.serialNumber }
        adapter.setItems(filter)
        dialog.dismiss()
    }

    override fun configureDateFilter(dialog: BottomSheetDialog) {
        showCalendar(dialog)
    }

    private fun resultScanCode(text: String) {
        WyncefApplication.deviceViewModel.getScope().launch {
            val resultDevice = WyncefApplication.deviceViewModel.selectDevice(text)
            val order = getInventoryOrder()

            if (resultDevice == null) {
                val device = Device(agency = inventory.agency, category = Category(), serialNumber = text, status = "EM USO")
                val newItem = Item(inventory = inventory, device = device, order = order)
                startItemActivity(newItem, true)
            } else {
                val item = WyncefApplication.itemViewModel.selectItem(inventory.id, resultDevice.serialNumber)
                if (item == null) {
                    val newItem = Item(inventory = inventory, device = resultDevice, order = order)
                    WyncefApplication.itemViewModel.insert(newItem)
                    startItemActivity(newItem, false)
                } else {
                    startItemActivity(item, false)
                }
            }
        }
    }

    private fun showBottomSheet(import: Boolean, message: String) {
        val bottomSheetDialog = BottomSheetUtil.show(context, R.layout.bottom_sheet_dialog, false)
        val dialog = bottomSheetDialog.first
        val view = bottomSheetDialog.second
        bottomDialog = dialog

        view.linear_layout_dialog.minimumHeight = context.window.decorView.height
        view.text_view_dialog_title.text = getString(R.string.text_view_choose)
        view.text_view_dialog_body.text = message

        view.button_positive.setOnClickListener {
            if (import) {
                importCSV()
            } else {
                exportCSV()
            }
            dialog.dismiss()
        }

        view.button_negative.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun importCSV() {
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            configureIntent()
        } else {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), ConstantsUtil.REQUEST_CODE_READ)
        }
    }

    private fun exportCSV() {
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            configureExportFile()
        } else {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), ConstantsUtil.REQUEST_CODE_WRITE)
        }
    }

    private fun configureIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/*"
        try {
            startActivityForResult(intent, ConstantsUtil.REQUEST_CODE_READ)
        } catch (exception: ActivityNotFoundException) {
            showMessage(getString(R.string.alert_activity), Snackbar.LENGTH_LONG)
        }
    }

    private fun configureImportFile(intent: Intent?) {
        intent?.let {
            val path = PathUtil.getPath(it.data as Uri)
            if (path == null) {
                showMessage(getString(R.string.alert_persmissions_read), Snackbar.LENGTH_LONG)
            } else {
                CSVFileUtil.import(this, path)
            }
        }
    }

    private fun configureExportFile() {
        CSVFileUtil.export(this)
    }

    private fun getInventoryOrder() : Long {
        return if(adapter.getItems().isEmpty()){
            1
        }else{
            val items = adapter.getItems().sortedBy { it.order }
            items.last().order + 1
        }
    }
}
