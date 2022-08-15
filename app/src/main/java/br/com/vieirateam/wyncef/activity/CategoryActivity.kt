package br.com.vieirateam.wyncef.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import br.com.vieirateam.wyncef.entity.Category
import br.com.vieirateam.wyncef.util.ConstantsUtil
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.util.ListBottomSheetUtil
import br.com.vieirateam.wyncef.util.KeyboardUtil
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.bottom_sheet_base.view.*
import kotlinx.android.synthetic.main.bottom_sheet_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

class CategoryActivity : BaseActivity(), ListBottomSheetUtil.Callback {

    private lateinit var category: Category

    override fun configureItem(item: Serializable?) {
        baseItem = item ?: bundle.getSerializable(ConstantsUtil.ITEM)
        category = baseItem as Category
        callbackListener.initializeList(4)
    }

    override fun updateUI() {
        if(category.name.isNotEmpty()) {
            text_view_body_1.text = category.name
        }
        if(category.initials.isNotEmpty()) {
            text_view_body_2.text = category.initials
        }
        image_view_icon2.setImageResource(category.icon)
    }

    override fun configureFields() {
        card_view_1.setOnClickListener {
            showBottomSheet(1)
        }

        card_view_2.setOnClickListener {
            showBottomSheet(2)
        }

        card_view_3.setOnClickListener {
            val bottomSheet = callbackListener.showBottomSheetList(4)
            dialog = bottomSheet.first
            mEditText = bottomSheet.second.text_iput_edit_text_list
        }

        card_view_4.removeAllViews()
        card_view_5.removeAllViews()
        card_view_6.removeAllViews()
        image_view_icon2.visibility = View.VISIBLE
        text_view_title_1.setText(R.string.text_view_category)
        text_view_title_2.setText(R.string.text_view_initials)
        text_view_title_3.setText(R.string.text_view_icon)
        text_view_body_3.text = getString(R.string.text_view_touch_icon)
    }

    override fun configureBottomSheet(view: View) {
        when (card) {
            1 -> {
                view.text_view_base.setText(R.string.text_view_category)
                view.text_iput_edit_text_base.setHint(R.string.text_view_example_category)
                view.text_iput_edit_text_base.setText(category.name)
            }
            else -> {
                view.text_view_base.setText(R.string.text_view_initials)
                view.text_iput_edit_text_base.setHint(R.string.text_view_example_initials)
                view.text_iput_edit_text_base.filters =
                    arrayOf<InputFilter>(InputFilter.LengthFilter(2))
                view.text_iput_edit_text_base.setText(category.initials)
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

    override fun checkItem(text: String) {
        when(card) {
            1 -> {
                WyncefApplication.categoryViewModel.getScope().launch(Dispatchers.Main) {
                    if (WyncefApplication.categoryViewModel.selectCategory(text) == null) {
                        text_view_body_1.text = text
                        category.name = text
                    } else {
                        showMessage(getString(R.string.alert_category_name_exists))
                        text_view_body_1.text = getString(R.string.text_view_touch)
                        category.name = ""
                    }
                }
            }
            else -> {
                if (text.length > 1) {
                    val initials = "${text[0]}${text[1]}"
                    WyncefApplication.categoryViewModel.getScope().launch(Dispatchers.Main) {
                        if (WyncefApplication.categoryViewModel.selectCategory(initials) == null) {
                            text_view_body_2.text = initials
                            category.initials = initials
                        } else {
                            showMessage(getString(R.string.alert_category_initials_exists))
                            text_view_body_2.text = getString(R.string.text_view_touch)
                            category.initials = ""
                        }
                    }
                } else {
                    text_view_body_2.setText(R.string.text_view_touch)
                    category.initials = ""
                }
            }
        }
    }

    override fun saveItem() {
        val name = text_view_body_1.text.trim().toString()
        val initials = text_view_body_2.text.trim().toString()

        if (name.isEmpty() ||
            initials.isEmpty() ||
            name == getString(R.string.text_view_touch) ||
            initials == getString(R.string.text_view_touch)) {
            showMessage(getString(R.string.alert_field_empty))
        } else {
            category.name = name
            category.initials = initials
            bundle.putBoolean(ConstantsUtil.SAVE, save)
            bundle.putSerializable(ConstantsUtil.ITEM, category)
            baseIntent.putExtra(ConstantsUtil.BUNDLE, bundle)
            setResult(Activity.RESULT_OK, baseIntent)
            finish()
        }
    }

    override fun getMessage(): String {
        val name = "${getString(R.string.text_view_category)}: ${category.name}"
        val initials = "${getString(R.string.text_view_initials)}: ${category.initials}"
        return "$name\n$initials"
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

    override fun onIconClick(icon: Int, view: View) {
        category.icon = icon
        updateUI()
        hideBottomSheet(dialog, view)
    }

    override fun resultItem1(bundle: Bundle) {}

    override fun resultItem2(bundle: Bundle) {}
}