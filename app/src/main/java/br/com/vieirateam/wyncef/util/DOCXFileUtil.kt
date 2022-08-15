package br.com.vieirateam.wyncef.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.wyncef.entity.Tag
import br.com.vieirateam.wyncef.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.bottom_sheet_progress.view.linear_layout_progress
import kotlinx.android.synthetic.main.bottom_sheet_progress.view.text_view_progress
import java.io.FileWriter
import java.io.IOException

object DOCXFileUtil {

    fun print(context: Context, mView : View, tags : List<Tag>) {
        val fileWriter : FileWriter? = if (tags.size == 1) {
            FileWriter("/storage/emulated/0/etiqueta.txt")
        } else {
            FileWriter("/storage/emulated/0/etiquetas.txt")
        }

        try {
            showProgress(context, mView)
            for (tag in tags) {
                val logicalName = "${tag.device.agency.state}${tag.device.agency.cgc}${tag.device.category.initials}${tag.device.logicalName}"
                fileWriter?.append(context.getString(R.string.text_tag_print_logical_name, logicalName))
                fileWriter?.append("\n")
                fileWriter?.append(context.getString(R.string.text_tag_print_serial_number, tag.device.serialNumber))
                fileWriter?.append("\n")
                if (tag.device.patrimony == null) {
                    fileWriter?.append(context.getString(R.string.text_tag_print_patrimony, context.getString(R.string.text_view_patrimony_not_found)))
                } else {
                    fileWriter?.append(context.getString(R.string.text_tag_print_patrimony, tag.device.patrimony))
                }
                fileWriter?.append("\n")
                fileWriter?.append(context.getString(R.string.text_tag_print_make, tag.make))
                fileWriter?.append("\n")
                fileWriter?.append(context.getString(R.string.text_tag_print_model, tag.model))
                if (tag.IP != null) {
                    fileWriter?.append("\n")
                    fileWriter?.append(context.getString(R.string.text_tag_print_ip, tag.IP))
                }
                fileWriter?.append("\n\n")
            }
        } catch (exception : IOException) {
            showMessage(mView, context.getString(R.string.alert_complete_print_error))
        } finally {
            try {
                fileWriter?.flush()
                fileWriter?.close()
            } catch (exception: IOException) {
                showMessage(mView, context.getString(R.string.alert_complete_print_error))
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showProgress(context: Context, mView: View) {
        context as AppCompatActivity
        val bottomSheetDialog = BottomSheetUtil.show(context, R.layout.bottom_sheet_progress, true)
        val dialog = bottomSheetDialog.first
        val view = bottomSheetDialog.second
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.linear_layout_progress.minimumHeight = displayMetrics.heightPixels
        view.text_view_progress.text = context.getString(R.string.text_tag_print_progress)

        Handler().postDelayed({
            dialog.dismiss()
            showMessage(mView, context.getString(R.string.alert_complete_print_ok))
        }, ConstantsUtil.DELAY)
    }

    private fun showMessage(view: View, message: String) {
        SnackbarUtil.show(view, message, Snackbar.LENGTH_LONG)
    }
}