package br.com.vieirateam.wyncef.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import br.com.vieirateam.wyncef.entity.Tag
import br.com.vieirateam.wyncef.util.ConstantsUtil
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.entity.Agency
import br.com.vieirateam.wyncef.entity.Category
import br.com.vieirateam.wyncef.entity.Device
import br.com.vieirateam.wyncef.extension.validateIPAddress
import br.com.vieirateam.wyncef.util.DOCXFileUtil
import br.com.vieirateam.wyncef.util.KeyboardUtil
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.bottom_sheet_base.view.*
import kotlinx.android.synthetic.main.bottom_sheet_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

class TagActivity : BaseActivity() {

    private var saveDevice = false
    private lateinit var tag: Tag
    private lateinit var device: Device

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (!save) menuInflater.inflate(R.menu.print, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_print) {
            if (checkItem()) {
                val items = mutableListOf<Tag>()
                items.add(tag)
                DOCXFileUtil.print(this, mView, items)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    override fun resultItem1(bundle: Bundle) {
        tag.device = bundle.getSerializable(ConstantsUtil.ITEM) as Device
        saveDevice = bundle.getBoolean(ConstantsUtil.SAVE)

        text_view_body_4.text = "${tag.device.agency.state}${tag.device.agency.cgc}${tag.device.category.initials}${tag.device.logicalName}"
        image_view_icon3.setImageResource(tag.device.category.icon)

        if (saveDevice) {
            WyncefApplication.deviceViewModel.insert(tag.device)
        } else {
            WyncefApplication.deviceViewModel.getScope().launch(Dispatchers.Main) {
                val oldDevice = WyncefApplication.deviceViewModel.selectDevice(tag.device.serialNumber)
                if (oldDevice != null) {
                    oldDevice.agency = tag.device.agency
                    oldDevice.category = tag.device.category
                    oldDevice.serialNumber = tag.device.serialNumber
                    oldDevice.logicalName = tag.device.logicalName
                    oldDevice.patrimony = tag.device.patrimony
                    oldDevice.status = tag.device.status
                    oldDevice.date = tag.device.date
                    tag.device = oldDevice
                    WyncefApplication.deviceViewModel.update(oldDevice)
                    updateItem(tag.device)
                }
            }
        }
    }

    override fun configureItem(item: Serializable?) {
        baseItem = item ?: bundle.getSerializable(ConstantsUtil.ITEM)
        tag = baseItem as Tag
        callbackListener.initializeList(3)
    }

    @SuppressLint("SetTextI18n")
    override fun updateUI() {
        if(tag.make.isNotEmpty()) {
            text_view_body_1.text = tag.make
        }
        if(tag.model.isNotEmpty()) {
            text_view_body_2.text = tag.model
        }
        if (tag.IP != null) {
            if(tag.IP.toString().isNotEmpty()) {
                text_view_body_3.text = tag.IP
            }
        }
        if(tag.device.agency.name.isNotEmpty()) {
            text_view_body_4.text = "${tag.device.agency.state}${tag.device.agency.cgc}${tag.device.category.initials}${tag.device.logicalName}"
            image_view_icon3.setImageResource(tag.device.category.icon)
        }
    }

    override fun configureFields() {
        card_view_1.setOnClickListener {
            showBottomSheet(1)
        }

        card_view_2.setOnClickListener {
            showBottomSheet(2)
        }

        card_view_3.setOnClickListener {
            showBottomSheet(3)
        }

        card_view_4.setOnClickListener {
            if ( callbackListener.devices.isEmpty() ) {
                registerItem(3)
            } else {
                val bottomSheet = callbackListener.showBottomSheetList(3)
                dialog = bottomSheet.first
                mEditText = bottomSheet.second.text_iput_edit_text_list
            }
        }

        card_view_5.removeAllViews()
        card_view_6.removeAllViews()
        text_view_title_1.setText(R.string.text_view_tag_make)
        text_view_title_2.setText(R.string.text_view_tag_model)
        text_view_title_3.setText(R.string.text_view_tag_ip)
        text_view_title_4.setText(R.string.app_device)
        image_view_icon3.visibility = View.VISIBLE
    }

    override fun configureBottomSheet(view: View) {
        when (card) {
            1 -> {
                view.text_view_base.setText(R.string.text_view_tag_make)
                view.text_iput_edit_text_base.setHint(R.string.text_view_example_tag_make)
                view.text_iput_edit_text_base.setText(tag.make)
            }
            2 -> {
                view.text_view_base.setText(R.string.text_view_tag_model)
                view.text_iput_edit_text_base.setHint(R.string.text_view_example_tag_model)
                view.text_iput_edit_text_base.setText(tag.model)
            }
            else -> {
                view.text_view_base.setText(R.string.text_view_tag_ip)
                view.text_iput_edit_text_base.setHint(R.string.text_view_example_tag_ip)
                view.text_iput_edit_text_base.setText(tag.IP)
            }
        }
        view.text_iput_edit_text_base.text?.length?.let { position ->
            view.text_iput_edit_text_base.setSelection(position)
        }
        KeyboardUtil.show(view.text_iput_edit_text_base)
        enabledBottomSheetFields(view)
    }

    override fun enabledBottomSheetFields(view: View) {
        view.image_view_scan.visibility = View.INVISIBLE
    }

    @SuppressLint("DefaultLocale")
    override fun checkItem(text: String) {
        when(card) {
            1 -> {
                text_view_body_1.text = text
                tag.make = text
            }
            2 -> {
                text_view_body_2.text = text
                tag.model = text
            }
            else -> {
                if (validateIPAddress(text)) {
                    text_view_body_3.text = text
                    tag.IP = text
                } else {
                    showMessage(getString(R.string.alert_tag_ip))
                    text_view_body_3.text = getString(R.string.text_view_touch)
                    tag.IP = null
                }
            }
        }
    }

    override fun saveItem() {
        if (checkItem()) {
            bundle.putBoolean(ConstantsUtil.SAVE, save)
            bundle.putSerializable(ConstantsUtil.ITEM, tag)
            baseIntent.putExtra(ConstantsUtil.BUNDLE, bundle)
            setResult(Activity.RESULT_OK, baseIntent)
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun getMessage(): String {
        val make = "${getString(R.string.text_view_tag_make)}: ${tag.make}"
        val model = "${getString(R.string.text_view_tag_model)}: ${tag.model}"
        val ip = if (tag.IP != null) {
            "${getString(R.string.text_view_tag_ip)}: ${tag.IP}"
        } else {
            "${getString(R.string.text_view_tag_ip)}: ${getString(R.string.text_view_without_tag_ip)}"
        }
        val device = "${getString(R.string.app_device)}: ${tag.device.agency.state}${tag.device.agency.cgc}${tag.device.category.initials}${tag.device.logicalName}"
        val serialNumber = "${getString(R.string.text_view_serial_number)}: ${tag.device.serialNumber}"
        return "$make\n$model\n$ip\n$device\n$serialNumber"
    }

    override fun configureShare() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, getMessage())
        intent.type = "text/plain"
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.menu_share)))
        } catch (exception: ActivityNotFoundException) {
            showMessage(getString(R.string.alert_activity))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onDeviceClick(item: Device, view: View) {
        if (item.logicalName == null) {
            device = item
            showMessage(getString(R.string.alert_tag_device))
            Handler().postDelayed({
                registerItem(0)
            }, 1000)
        } else {
            tag.device = item
            updateUI()
        }
        hideBottomSheet(dialog, view)
    }

    override fun registerItem(card: Int) {
        if (card == 3) {
            device = Device(agency = Agency(), category = Category(), status = "EM USO")
            saveDevice = true
        } else {
            saveDevice = false
        }
        bundle.putBoolean(ConstantsUtil.SAVE, saveDevice)
        bundle.putString(ConstantsUtil.TITLE, getString(R.string.app_device))
        bundle.putSerializable(ConstantsUtil.ITEM, device)
        val intent = Intent(this, DeviceActivity::class.java)
        intent.putExtra(ConstantsUtil.BUNDLE, bundle)
        startActivityForResult(intent, ConstantsUtil.REQUEST_CODE_ITEM_1)
    }

    private fun checkItem() : Boolean {
        val make = text_view_body_1.text.trim().toString()
        val model = text_view_body_2.text.trim().toString()
        val ip = text_view_body_3.text.trim().toString()
        val device = text_view_body_4.text.trim().toString()

        if (make.isEmpty() ||
            model.isEmpty() ||
            device.isEmpty() ||
            make == getString(R.string.text_view_touch) ||
            model == getString(R.string.text_view_touch) ||
            device == getString(R.string.text_view_touch)) {
            showMessage(getString(R.string.alert_field_empty))
            return false
        } else {
            if(ip.isEmpty() || ip == getString(R.string.text_view_touch)){
                tag.IP = null
            }else{
                tag.IP = ip
            }
            tag.make = make
            tag.model = model
            tag.complete = true
            return true
        }
    }

    private fun updateItem(device: Device) {
        WyncefApplication.itemViewModel.getScope().launch(Dispatchers.Main) {
            val item = WyncefApplication.itemViewModel.selectItem(device.serialNumber)
            if (item != null) {
                item.device = device
                WyncefApplication.itemViewModel.update(item)
            }
        }
    }

    override fun resultItem2(bundle: Bundle) {}
}