package br.com.vieirateam.wyncef.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.wyncef.BuildConfig
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.util.BottomSheetUtil
import br.com.vieirateam.wyncef.util.KeyboardUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.bottom_sheet_base.view.*
import kotlinx.android.synthetic.main.fragment_about.card_view_4
import kotlinx.android.synthetic.main.fragment_about.card_view_5
import kotlinx.android.synthetic.main.fragment_about.text_view_version

class AboutFragment : GenericFragment(R.layout.fragment_about) {

    private lateinit var intent: Intent

    override fun posActivityCreated(savedInstanceState: Bundle?) {
        configureFields()
        fabButtonUtil.floatingButton.hide()
        fabButtonUtil.floatingButtonFeedback.show()
        fabButtonUtil.floatingButtonFeedback.setOnClickListener {
            configureBottomSheet()
        }
    }

    private fun configureFields() {

        val version = getString(R.string.text_view_version, BuildConfig.VERSION_NAME)
        text_view_version.text = version

        card_view_4.setOnClickListener {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.link_site))
            startIntentActivity(intent)
        }

        card_view_5.setOnClickListener {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.link_github))
            startIntentActivity(intent)
        }
    }

    private fun configureBottomSheet() {
        val bottomSheetDialog = BottomSheetUtil.show(this.requireActivity(), R.layout.bottom_sheet_base, false)
        val dialog = bottomSheetDialog.first
        val view = bottomSheetDialog.second
        view.linear_layout_base.minimumHeight = (activity as AppCompatActivity).window.decorView.height

        view.text_view_base.setText(R.string.text_view_feedback)
        view.image_view_voice.visibility = View.INVISIBLE
        view.image_view_scan.visibility = View.INVISIBLE
        view.button_save_base.setText(R.string.menu_send)
        view.text_iput_edit_text_base.hint = getString(R.string.text_view_feedback_message)
        KeyboardUtil.show(view.text_iput_edit_text_base)

        view.image_view_back_base.setOnClickListener {
            BottomSheetUtil.hideBottomSheet(dialog, view)
        }

        view.button_cancel_base.setOnClickListener {
            BottomSheetUtil.hideBottomSheet(dialog, view)
        }

        view.button_save_base.setOnClickListener {
            val text = view.text_iput_edit_text_base.text?.trim().toString()
            if (text.isNotEmpty()) {
                intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:")
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.link_email)))
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.text_view_feedback))
                intent.putExtra(Intent.EXTRA_TEXT, text)
                startIntentActivity(intent)
            }
            BottomSheetUtil.hideBottomSheet(dialog, view)
        }
    }

    private fun startIntentActivity(intent: Intent) {
        try {
            startActivity(intent)
        } catch (exception: ActivityNotFoundException) {
            showMessage()
        }
    }

    private fun showMessage() {
        showMessage(getString(R.string.alert_activity), Snackbar.LENGTH_LONG)
    }
}