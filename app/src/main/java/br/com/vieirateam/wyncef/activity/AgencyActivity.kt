package br.com.vieirateam.wyncef.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import br.com.vieirateam.wyncef.entity.Agency
import br.com.vieirateam.wyncef.util.ConstantsUtil
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.util.KeyboardUtil
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.bottom_sheet_base.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

class AgencyActivity : BaseActivity() {

    private lateinit var agency: Agency

    override fun configureItem(item : Serializable?) {
        baseItem = item ?: bundle.getSerializable(ConstantsUtil.ITEM)
        agency = baseItem as Agency
    }

    override fun updateUI() {
        if(agency.name.isNotEmpty()) {
            text_view_body_1.text = agency.name
        }
        if(agency.cgc.isNotBlank()) {
            text_view_body_2.text = agency.cgc
        }
        if(agency.state.isNotEmpty()) {
            text_view_body_3.text = agency.state
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

        card_view_4.removeAllViews()
        card_view_5.removeAllViews()
        card_view_6.removeAllViews()
        text_view_title_1.setText(R.string.text_view_agency)
        text_view_title_2.setText(R.string.text_view_cgc)
        text_view_title_3.setText(R.string.text_view_state)
    }

    override fun configureBottomSheet(view: View) {
        when (card) {
            1 -> {
                view.text_view_base.setText(R.string.text_view_agency)
                view.text_iput_edit_text_base.setHint(R.string.text_view_example_agency)
                view.text_iput_edit_text_base.setText(agency.name)
            }
            2 -> {
                view.text_view_base.setText(R.string.text_view_cgc)
                view.text_iput_edit_text_base.setHint(R.string.text_view_example_cgc)
                view.text_iput_edit_text_base.inputType = InputType.TYPE_CLASS_NUMBER
                view.text_iput_edit_text_base.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(4))
                view.text_iput_edit_text_base.setText(agency.cgc)
            }
            else -> {
                view.text_view_base.setText(R.string.text_view_state)
                view.text_iput_edit_text_base.setHint(R.string.text_view_example_state)
                view.text_iput_edit_text_base.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(2))
                view.text_iput_edit_text_base.setText(agency.state)
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

    override fun checkItem(text : String) {
        when(card){
            1 -> {
                WyncefApplication.agencyViewModel.getScope().launch(Dispatchers.Main) {
                    if (WyncefApplication.agencyViewModel.selectAgency(text) == null) {
                        text_view_body_1.text = text
                        agency.name = text
                    } else {
                        showMessage(getString(R.string.alert_agency_name_exists))
                        text_view_body_1.text = getString(R.string.text_view_touch)
                        agency.name = ""
                    }
                }
            }
            2 -> {
                if (!text.contains(Regex("\\D")) && text.length > 3) {
                    val cgc = "${text[0]}${text[1]}${text[2]}${text[3]}"
                    WyncefApplication.agencyViewModel.getScope().launch(Dispatchers.Main) {
                        if (WyncefApplication.agencyViewModel.selectAgency(cgc) == null) {
                            text_view_body_2.text = cgc
                            agency.cgc = cgc
                        } else {
                            showMessage(getString(R.string.alert_agency_cgc_exists))
                            text_view_body_2.text = getString(R.string.text_view_touch)
                            agency.cgc = ""
                        }
                    }
                } else {
                    text_view_body_2.setText(R.string.text_view_touch)
                    agency.cgc = ""
                }
            } else -> {
                if (text.length > 1) {
                    val state = "${text[0]}${text[1]}"
                    text_view_body_3.text = state
                    agency.state = state
                } else {
                    text_view_body_3.setText(R.string.text_view_touch)
                    text_view_body_3.setText(R.string.text_view_touch)
                    agency.state = ""
                }
            }
        }
    }

    override fun saveItem() {
        val name = text_view_body_1.text.trim().toString()
        val cgc = text_view_body_2.text.trim().toString()
        val state = text_view_body_3.text.trim().toString()

        if (name.isEmpty() ||
            cgc.isEmpty() ||
            state.isEmpty() ||
            name == getString(R.string.text_view_touch) ||
            cgc == getString(R.string.text_view_touch) ||
            state == getString(R.string.text_view_touch)) {
            showMessage(getString(R.string.alert_field_empty))
        } else {
            agency.name = name
            agency.cgc = cgc
            agency.state = state
            bundle.putBoolean(ConstantsUtil.SAVE, save)
            bundle.putSerializable(ConstantsUtil.ITEM, agency)
            baseIntent.putExtra(ConstantsUtil.BUNDLE, bundle)
            setResult(Activity.RESULT_OK, baseIntent)
            finish()
        }
    }

    override fun getMessage(): String {
        val name = "${getString(R.string.text_view_agency)}: ${agency.name}"
        val state = "${getString(R.string.text_view_cgc)}: ${agency.cgc}"
        val cgc = "${getString(R.string.text_view_state)}: ${agency.state}"
        return "$name\n$state\n$cgc"
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

    override fun resultItem1(bundle: Bundle) {}

    override fun resultItem2(bundle: Bundle) {}
}