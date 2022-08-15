package br.com.vieirateam.wyncef.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.entity.Agency
import br.com.vieirateam.wyncef.entity.Inventory
import br.com.vieirateam.wyncef.util.*
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.bottom_sheet_list.view.*
import java.io.Serializable

class InventoryActivity : BaseActivity() {

    private lateinit var inventory: Inventory

    override fun resultItem1(bundle: Bundle) {
        inventory.agency = bundle.getSerializable(ConstantsUtil.ITEM) as Agency
        WyncefApplication.agencyViewModel.insert(inventory.agency)
        updateUI()
    }

    override fun configureItem(item: Serializable?) {
        baseItem = item ?: bundle.getSerializable(ConstantsUtil.ITEM)
        inventory = baseItem as Inventory
        callbackListener.initializeList(1)
    }

    @SuppressLint("SetTextI18n")
    override fun updateUI() {
        if(inventory.agency.name.isNotEmpty()) {
            text_view_body_1.text = "${inventory.agency.cgc} - ${inventory.agency.name}, ${inventory.agency.state}"
        }
        text_view_body_2.text = DateFormatUtil.format(inventory.date, "dd/MM/yyyy")
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
        card_view_3.removeAllViews()
        card_view_4.removeAllViews()
        card_view_5.removeAllViews()
        card_view_6.removeAllViews()
        text_view_title_1.setText(R.string.text_view_agency)
        text_view_title_2.setText(R.string.text_view_date)
    }

    override fun onAgencyClick(item: Agency, view: View) {
        inventory.agency = item
        updateUI()
        hideBottomSheet(dialog, view)
    }

    override fun registerItem(card: Int) {
        bundle.putBoolean(ConstantsUtil.SAVE, true)
        bundle.putString(ConstantsUtil.TITLE, getString(R.string.app_agency))
        bundle.putSerializable(ConstantsUtil.ITEM, Agency())
        val intent = Intent(this, AgencyActivity::class.java)
        intent.putExtra(ConstantsUtil.BUNDLE, bundle)
        startActivityForResult(intent, ConstantsUtil.REQUEST_CODE_ITEM_1)
    }

    override fun saveItem() {
        val agencyName = text_view_body_1.text.trim().toString()
        if (agencyName.isEmpty() || agencyName == getString(R.string.text_view_touch)) {
            showMessage(getString(R.string.alert_field_empty))
        } else {
            bundle.putBoolean(ConstantsUtil.SAVE, save)
            bundle.putSerializable(ConstantsUtil.ITEM, inventory)
            baseIntent.putExtra(ConstantsUtil.BUNDLE, bundle)
            setResult(Activity.RESULT_OK, baseIntent)
            finish()
        }
    }

    override fun getMessage(): String {
        val agencyName = "${getString(R.string.text_view_agency)}: ${inventory.agency.cgc} - ${inventory.agency.name}, ${inventory.agency.state}"
        val date = "${getString(R.string.text_view_date)}: ${DateFormatUtil.format(inventory.date, "dd/MM/yyyy")}"
        return "$agencyName\n$date"
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

    override fun resultItem2(bundle: Bundle) {}

    override fun configureBottomSheet(view: View) {}

    override fun enabledBottomSheetFields(view: View) {}

    override fun checkItem(text: String) {}
}