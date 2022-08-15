package br.com.vieirateam.wyncef.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

object BottomSheetUtil {

    fun show(context: FragmentActivity, layoutID: Int, stateExpanded: Boolean): Pair<BottomSheetDialog, View> {
        val dialog = BottomSheetDialog(context)
        val viewGroup = (context.window.decorView.rootView) as ViewGroup
        val view = LayoutInflater.from(context).inflate(layoutID, viewGroup, false)
        dialog.setContentView(view)
        val bottomSheet = BottomSheetBehavior.from((view.parent) as View)
        dialog.setOnShowListener { bottomSheet.setPeekHeight(view.height) }
        dialog.show()

        if (stateExpanded) {
            dialog.setCancelable(false)
        } else {
            bottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheetView: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) hideBottomSheet(dialog, view)
                }

                override fun onSlide(bottomSheetView: View, slideOffset: Float) {
                    if (slideOffset < -0.50f) hideBottomSheet(dialog, view)
                }
            })
        }
        return Pair(dialog, view)
    }

    fun hideBottomSheet(dialog: BottomSheetDialog, view: View) {
        KeyboardUtil.hide(view)
        dialog.dismiss()
    }
}