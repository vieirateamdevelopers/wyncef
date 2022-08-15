package br.com.vieirateam.wyncef.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.entity.Agency
import br.com.vieirateam.wyncef.entity.Category
import br.com.vieirateam.wyncef.entity.Device
import br.com.vieirateam.wyncef.util.ListBottomSheetUtil
import br.com.vieirateam.wyncef.util.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_base.linear_layout
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentIntegrator.parseActivityResult
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.android.synthetic.main.bottom_sheet_base.view.*
import java.io.Serializable
import java.util.Locale

abstract class BaseActivity : GenericActivity(R.layout.activity_base), ListBottomSheetUtil.Callback {

    private var mTitle = ""

    protected var card = 0
    protected var save = false
    protected lateinit var mView: View
    protected var mEditText: EditText? = null
    protected var baseItem: Serializable? = null
    protected lateinit var dialog: BottomSheetDialog
    protected lateinit var callbackListener : ListBottomSheetUtil

    override fun posCreate(savedInstanceState: Bundle?) {
        setSupportActionBar()
        setDisplayHomeAsUpEnabled()

        mView = linear_layout
        bundle = intent.getBundleExtra(ConstantsUtil.BUNDLE) as Bundle
        save = bundle.getBoolean(ConstantsUtil.SAVE)
        mTitle = bundle.getString(ConstantsUtil.TITLE) as String
        callbackListener = ListBottomSheetUtil(this)
        callbackListener.setCallbackListener(this)

        title = if (save) {
            "${getString(R.string.menu_save)} $mTitle"
        } else {
            "${getString(R.string.menu_edit)} $mTitle"
        }

        WyncefApplication.getViewModel()
        configureItem(null)
        configureFields()
        updateUI()
    }

    override fun onStop() {
        super.onStop()
        if (mEditText != null) {
            KeyboardUtil.hide(mEditText as View)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ConstantsUtil.ITEM, baseItem)
        outState.putInt(ConstantsUtil.CARD, card)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        baseItem = savedInstanceState.getSerializable(ConstantsUtil.ITEM)
        card = savedInstanceState.getInt(ConstantsUtil.CARD)
        configureItem(baseItem)
    }

    @SuppressLint("DefaultLocale")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        val resultScan = parseActivityResult(requestCode, resultCode, intent)
        if (resultScan != null) {
            val text = resultScan.contents.toString()
            if (text.isNotEmpty()) {
                checkItem(text.toUpperCase())
                updateUI()
            }
            return
        }

        intent?.let {
            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    ConstantsUtil.REQUEST_CODE_ITEM_1 -> resultItem1(bundle)
                    ConstantsUtil.REQUEST_CODE_ITEM_2 -> resultItem2(bundle)
                    ConstantsUtil.REQUEST_CODE_VOICE -> {
                        val resultVoice = it.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        if (resultVoice != null) {
                            val text = resultVoice[0].toUpperCase()
                            checkItem(text)
                            updateUI()
                        }
                    }
                }
                return
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (save) {
            menuInflater.inflate(R.menu.save, menu)
        } else {
            menuInflater.inflate(R.menu.share, menu)
            menuInflater.inflate(R.menu.edit, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu_save -> {
                saveItem()
                return true
            }
            R.id.menu_edit -> {
                saveItem()
                return true
            }
            R.id.menu_share -> {
                configureShare()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureBottomSheetButtons(view: View) {
        view.image_view_back_base.setOnClickListener {
            hideBottomSheet(dialog, view)
        }

        view.image_view_voice.setOnClickListener {
            voiceItem()
            hideBottomSheet(dialog, view)
        }

        view.image_view_scan.setOnClickListener {
            scanItem()
            hideBottomSheet(dialog, view)
        }

        view.button_cancel_base.setOnClickListener {
            hideBottomSheet(dialog, view)
        }

        view.button_save_base.setOnClickListener {
            checkItem(view)
            hideBottomSheet(dialog, view)
        }
    }

    private fun scanItem() {
        IntentIntegrator(this).apply {
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            setPrompt(getString(R.string.text_view_scan))
            captureActivity = ScanCodeActivity::class.java
            setBeepEnabled(false)
            setCameraId(0)
            initiateScan()
        }
    }

    private fun voiceItem() {
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        try {
            startActivityForResult(recognizerIntent, ConstantsUtil.REQUEST_CODE_VOICE)
        } catch (exception: ActivityNotFoundException) {
            showMessage(getString(R.string.alert_activity))
        }
    }

    @SuppressLint("DefaultLocale")
    private fun checkItem(view: View) {
        val text: String = if (card == 6) {
            view.spinner_base.selectedItem.toString()
        } else {
            view.text_iput_edit_text_base.text.toString().trim().toUpperCase()
        }
        if (text.isNotEmpty()) {
            checkItem(text)
        }
    }

    fun showMessage(message: String) {
        SnackbarUtil.show(mView, message, Snackbar.LENGTH_LONG)
    }

    protected fun showBottomSheet(card: Int) {
        this.card = card
        val bottomSheetDialog = BottomSheetUtil.show(this, R.layout.bottom_sheet_base, false)
        dialog = bottomSheetDialog.first
        val view = bottomSheetDialog.second
        mEditText = view.text_iput_edit_text_base
        view.linear_layout_base.minimumHeight = window.decorView.height
        configureBottomSheetButtons(view)
        configureBottomSheet(view)
    }

    protected fun hideBottomSheet(dialog: BottomSheetDialog, view: View) {
        BottomSheetUtil.hideBottomSheet(dialog, view)
    }

    abstract fun resultItem1(bundle: Bundle)

    abstract fun resultItem2(bundle: Bundle)

    abstract fun configureItem(item: Serializable?)

    abstract fun configureFields()

    abstract fun updateUI()

    abstract fun configureBottomSheet(view: View)

    abstract fun enabledBottomSheetFields(view: View)

    abstract fun checkItem(text : String)

    abstract fun saveItem()

    abstract fun getMessage(): String

    abstract fun configureShare()

    override fun registerItem(card: Int) {}

    override fun onAgencyClick(item: Agency, view: View) {}

    override fun onAgencyLongClick(item: Agency, view: View) {}

    override fun onCategoryClick(item: Category, view: View) {}

    override fun onCategoryLongClick(item: Category, view: View) {}

    override fun onDeviceClick(item: Device, view: View) {}

    override fun onDeviceLongClick(item: Device, view: View) {}

    override fun onIconClick(icon: Int, view: View) {}

    class ScanCodeActivity : CaptureActivity()
}