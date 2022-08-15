package br.com.vieirateam.wyncef.interfaces

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_filter.view.*

interface FilterBottomSheet {

    fun configureBottomSheetFilter(dialog: BottomSheetDialog, view: View) {
        view.card_view_1.setOnClickListener {
            configureAscendingOrder(dialog)
        }

        view.card_view_2.setOnClickListener {
            configureDescendingOrder(dialog)
        }

        view.card_view_3.setOnClickListener {
            configureDateFilter(dialog)
        }
    }

    fun configureAscendingOrder(dialog: BottomSheetDialog)

    fun configureDescendingOrder(dialog: BottomSheetDialog)

    fun configureDateFilter(dialog: BottomSheetDialog)
}