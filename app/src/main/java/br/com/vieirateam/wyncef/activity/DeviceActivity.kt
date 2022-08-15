package br.com.vieirateam.wyncef.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import br.com.vieirateam.wyncef.entity.Device
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.entity.Agency
import br.com.vieirateam.wyncef.entity.Category
import br.com.vieirateam.wyncef.util.*
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.bottom_sheet_base.view.*
import kotlinx.android.synthetic.main.bottom_sheet_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

class DeviceActivity : BaseActivity() {

    private lateinit var device: Device

    override fun resultItem1(bundle: Bundle) {
        device.agency = bundle.getSerializable(ConstantsUtil.ITEM) as Agency
        WyncefApplication.agencyViewModel.insert(device.agency)
        updateUI()
    }

    override fun resultItem2(bundle: Bundle) {
        device.category = bundle.getSerializable(ConstantsUtil.ITEM) as Category
        WyncefApplication.categoryViewModel.insert(device.category)
        updateUI()
    }

    override fun configureItem(item: Serializable?) {
        baseItem = item ?: bundle.getSerializable(ConstantsUtil.ITEM)
        device = baseItem as Device
        callbackListener.initializeList(1)
        callbackListener.initializeList(2)
    }

    @SuppressLint("SetTextI18n")
    override fun updateUI() {
        if(device.agency.name.isNotEmpty()){
            text_view_body_1.text = "${device.agency.cgc} - ${device.agency.name}, ${device.agency.state}"
        }
        if(device.category.name.isNotEmpty()){
            image_view_icon1.setImageResource(device.category.icon)
            text_view_body_2.text = "${device.category.initials} - ${device.category.name}"
        }
        if(device.serialNumber.isNotEmpty()){
            text_view_body_3.text = device.serialNumber
        }
        if(device.logicalName != null){
            text_view_body_4.text = "${device.agency.state}${device.agency.cgc}${device.category.initials}${device.logicalName}"
        }
        if(device.patrimony != null){
            text_view_body_5.text = device.patrimony
        }
        if(device.status.isNotEmpty()){
            text_view_body_6.text = device.status
        }
    }

    override fun configureFields() {
        card_view_1.setOnClickListener {
            if (callbackListener.agencies.isEmpty()) {
                registerItem(1)
            } else {
                val bottomSheet = callbackListener.showBottomSheetList(1)
                dialog = bottomSheet.first
                mEditText = bottomSheet.second.text_iput_edit_text_list
            }
        }

        card_view_2.setOnClickListener {
            if (callbackListener.categories.isEmpty()) {
                registerItem(2)
            } else {
                val bottomSheet = callbackListener.showBottomSheetList(2)
                dialog = bottomSheet.first
                mEditText = bottomSheet.second.text_iput_edit_text_list
            }
        }

        card_view_3.setOnClickListener {
            showBottomSheet(3)
        }

        card_view_4.setOnClickListener {
            val agencyName = text_view_body_1.text.trim().toString()
            val categoryName = text_view_body_2.text.trim().toString()
            if (agencyName == getString(R.string.text_view_touch) || categoryName == getString(R.string.text_view_touch)) {
                showMessage(getString(R.string.alert_device_logical_name))
            } else {
                showBottomSheet(4)
            }
        }

        card_view_5.setOnClickListener {
            showBottomSheet(5)
        }

        card_view_6.setOnClickListener {
            showBottomSheet(6)
        }

        image_view_icon1.visibility = View.VISIBLE
        text_view_title_1.setText(R.string.text_view_agency)
        text_view_title_2.setText(R.string.text_view_category)
        text_view_title_3.setText(R.string.text_view_serial_number)
        text_view_title_4.setText(R.string.text_view_logical_name)
        text_view_title_5.setText(R.string.text_view_patrimony)
        text_view_title_6.setText(R.string.text_view_status)
    }

    @SuppressLint("SetTextI18n")
    override fun onAgencyClick(item: Agency, view: View) {
        device.agency = item
        updateUI()
        hideBottomSheet(dialog, view)
    }

    @SuppressLint("SetTextI18n")
    override fun onCategoryClick(item: Category, view: View) {
        device.category = item
        updateUI()
        hideBottomSheet(dialog, view)
    }

    override fun registerItem(card: Int) {
        bundle.putBoolean(ConstantsUtil.SAVE, true)
        val requestCode : Int
        baseIntent = if (card == 1) {
            bundle.putString(ConstantsUtil.TITLE, getString(R.string.app_agency))
            bundle.putSerializable(ConstantsUtil.ITEM, Agency())
            requestCode = ConstantsUtil.REQUEST_CODE_ITEM_1
            Intent(this, AgencyActivity::class.java)
        } else {
            bundle.putString(ConstantsUtil.TITLE, getString(R.string.app_category))
            bundle.putSerializable(ConstantsUtil.ITEM, Category())
            requestCode = ConstantsUtil.REQUEST_CODE_ITEM_2
            Intent(this, CategoryActivity::class.java)
        }
        baseIntent.putExtra(ConstantsUtil.BUNDLE, bundle)
        startActivityForResult(baseIntent, requestCode)
    }

    override fun configureBottomSheet(view: View) {
        when (card) {
            3 -> {
                view.text_view_base.setText(R.string.text_view_serial_number)
                view.text_iput_edit_text_base.setHint(R.string.text_view_example_serial_number)
                view.text_iput_edit_text_base.setText(device.serialNumber)
            }
            4 -> {
                view.image_view_scan.visibility = View.INVISIBLE
                view.text_view_base.setText(R.string.text_view_logical_name)
                view.text_iput_edit_text_base.setHint(R.string.text_view_example_logical_name)
                view.text_iput_edit_text_base.inputType = InputType.TYPE_CLASS_NUMBER
                view.text_iput_edit_text_base.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
                view.text_iput_edit_text_base.setText(device.logicalName)
            }
            5 -> {
                view.text_view_base.setText(R.string.text_view_patrimony)
                view.text_iput_edit_text_base.setHint(R.string.text_view_example_patrimony)
                view.text_iput_edit_text_base.inputType = InputType.TYPE_CLASS_NUMBER
                view.text_iput_edit_text_base.setText(device.patrimony)
            }
            6 -> {
                view.image_view_voice.visibility = View.INVISIBLE
                view.image_view_scan.visibility = View.INVISIBLE
                view.text_iput_edit_text_base.visibility = View.INVISIBLE
                view.spinner_base.visibility = View.VISIBLE
                view.text_view_base.setText(R.string.text_view_status)
                val stringArray = resources.getStringArray(R.array.text_view_example_status)
                val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stringArray)
                view.spinner_base.adapter = arrayAdapter
                when {
                    device.status == stringArray[0] -> view.spinner_base.setSelection(0)
                    device.status == stringArray[1] -> view.spinner_base.setSelection(1)
                    device.status == stringArray[2] -> view.spinner_base.setSelection(2)
                    else -> view.spinner_base.setSelection(0)
                }
            }
        }
        view.text_iput_edit_text_base.text?.length?.let { position ->
            view.text_iput_edit_text_base.setSelection(position)
        }
        if (card < 6) KeyboardUtil.show(view.text_iput_edit_text_base)
        enabledBottomSheetFields(view)
    }

    @SuppressLint("SetTextI18n")
    override fun checkItem(text: String) {
        when(card){
            3 -> {
                WyncefApplication.deviceViewModel.getScope().launch(Dispatchers.Main) {
                    if (WyncefApplication.deviceViewModel.selectDevice(text) == null) {
                        text_view_body_3.text = text
                        device.serialNumber = text
                    } else {
                        showMessage(getString(R.string.alert_device_serial_number_exists))
                        text_view_body_3.text = getString(R.string.text_view_touch)
                        device.serialNumber = ""
                    }
                }
            }
            4 -> {
                if (!text.contains(Regex("\\D")) && text.length > 2) {
                    val logicalName = "${text[0]}${text[1]}${text[2]}"
                    text_view_body_4.text = "${device.agency.state}${device.agency.cgc}${device.category.initials}$logicalName"
                    device.logicalName = logicalName
                } else {
                    text_view_body_4.text = getString(R.string.text_view_touch)
                    device.logicalName = null
                }
            }
            5 -> {
                if (!text.contains(Regex("\\D"))) {
                    WyncefApplication.deviceViewModel.getScope().launch(Dispatchers.Main) {
                        if (WyncefApplication.deviceViewModel.selectDevice(text) == null) {
                            text_view_body_5.text = text
                            device.patrimony = text
                        } else {
                            showMessage(getString(R.string.alert_device_patrimony_exists))
                            text_view_body_5.text = getString(R.string.text_view_touch)
                            device.patrimony = null
                        }
                    }
                } else {
                    text_view_body_5.text = getString(R.string.text_view_touch)
                    device.patrimony = null
                }
            }
            6 -> {
                text_view_body_6.text = text
                device.status = text
            }
        }
    }

    override fun saveItem() {
        val agencyName = text_view_body_1.text.trim().toString()
        val categoryName = text_view_body_2.text.trim().toString()
        val serialNumber = text_view_body_3.text.trim().toString()
        val patrimony = text_view_body_5.text.trim().toString()
        val status = text_view_body_6.text.trim().toString()

        if (agencyName.isEmpty() ||
            categoryName.isEmpty() ||
            serialNumber.isEmpty() ||
            agencyName == getString(R.string.text_view_touch) ||
            categoryName == getString(R.string.text_view_touch) ||
            serialNumber == getString(R.string.text_view_touch) ||
            status == getString(R.string.text_view_touch)) {
            showMessage(getString(R.string.alert_field_empty))
        } else {
            device.serialNumber = serialNumber
            device.status = status

            if (text_view_body_3.text.trim().toString() == getString(R.string.text_view_touch)) {
                device.logicalName = null
            }

            if (patrimony == getString(R.string.text_view_touch)) {
                device.patrimony = null
            } else {
                device.patrimony = patrimony
            }
            bundle.putBoolean(ConstantsUtil.SAVE, save)
            bundle.putSerializable(ConstantsUtil.ITEM, device)
            baseIntent.putExtra(ConstantsUtil.BUNDLE, bundle)
            setResult(Activity.RESULT_OK, baseIntent)
            finish()
        }
    }

    override fun getMessage(): String {
        val agencyName = "${getString(R.string.text_view_agency)}: ${device.agency.cgc} - ${device.agency.name}, ${device.agency.state}"
        val categoryName = "${getString(R.string.text_view_category)}: ${device.category.initials} - ${device.category.name}"
        val serialNumber = "${getString(R.string.text_view_serial_number)}: ${device.serialNumber}"
        var logicalName = "${getString(R.string.text_view_logical_name)}: ${getString(R.string.text_view_without_logical_name)}"
        var patrimony = "${getString(R.string.text_view_patrimony)}: ${getString(R.string.text_view_without_patrimony)}"
        val status = "${getString(R.string.text_view_status)}: ${device.status}"

        if (device.patrimony != null) {
            patrimony = "${getString(R.string.text_view_patrimony)}: ${device.patrimony}"
        }
        if (device.logicalName != null) {
            logicalName = "${getString(R.string.text_view_logical_name)}: ${device.agency.state}${device.agency.cgc}${device.category.initials}${device.logicalName}"
        }
        return "$agencyName\n$categoryName\n$serialNumber\n$logicalName\n$patrimony\n$status"
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

    override fun enabledBottomSheetFields(view: View) {}
}