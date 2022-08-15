package br.com.vieirateam.wyncef.util

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.wyncef.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabButtonUtil (context: AppCompatActivity) {

    var fabOpen = false
    var frameLayout: FrameLayout = context.findViewById(R.id.frame_layout_fab_menu)
    var floatingButton: FloatingActionButton = context.findViewById(R.id.floating_button)
    var floatingButtonScan : FloatingActionButton = context.findViewById(R.id.floating_button_scan)
    var floatingButtonImport : FloatingActionButton = context.findViewById(R.id.floating_button_import)
    var floatingButtonExport : FloatingActionButton = context.findViewById(R.id.floating_button_export)
    var floatingButtonDevice : FloatingActionButton = context.findViewById(R.id.floating_button_device)
    var floatingButtonFeedback: FloatingActionButton = context.findViewById(R.id.floating_button_feedback)
    private var textViewButtonScan: TextView = context.findViewById(R.id.text_view_button_scan)
    private var textViewButtonImport: TextView = context.findViewById(R.id.text_view_button_import)
    private var textViewButtonExport: TextView = context.findViewById(R.id.text_view_button_export)
    private var textViewButtonDevice: TextView = context.findViewById(R.id.text_view_button_device)

    init {
        floatingButton.show()
        floatingButtonFeedback.hide()
    }

    fun configureFabMenu() {
        if(fabOpen) {
            closeFabMenu()
        } else {
            showFabMenu()
        }
    }

    private fun showFabMenu(){
        fabOpen = true
        hideFabMenu(false)
        floatingButton.rotation = 50F
        frameLayout.visibility = View.VISIBLE
        floatingButtonDevice.animate().translationY(-floatingButton.height.toFloat() * 1.15F)
        floatingButtonScan.animate().translationY(-floatingButton.height.toFloat() * 2.15F)
        floatingButtonExport.animate().translationY(-floatingButton.height.toFloat() * 3.15F)
        floatingButtonImport.animate().translationY(-floatingButton.height.toFloat() * 4.15F)
    }

    fun closeFabMenu(){
        fabOpen = false
        hideFabMenu(true)
        floatingButton.rotation = 0F
        frameLayout.visibility = View.INVISIBLE
        floatingButtonScan.animate().translationY(0F)
        floatingButtonImport.animate().translationY(0F)
        floatingButtonExport.animate().translationY(0F)
        floatingButtonDevice.animate().translationY(0F)
    }

    private fun hideFabMenu(hide: Boolean){
        if(hide){
            floatingButtonScan.hide()
            floatingButtonImport.hide()
            floatingButtonExport.hide()
            floatingButtonDevice.hide()
            textViewButtonScan.visibility = View.INVISIBLE
            textViewButtonImport.visibility = View.INVISIBLE
            textViewButtonExport.visibility = View.INVISIBLE
            textViewButtonDevice.visibility = View.INVISIBLE
        }else{
            floatingButtonScan.show()
            floatingButtonImport.show()
            floatingButtonExport.show()
            floatingButtonDevice.show()
            textViewButtonScan.visibility = View.VISIBLE
            textViewButtonImport.visibility = View.VISIBLE
            textViewButtonExport.visibility = View.VISIBLE
            textViewButtonDevice.visibility = View.VISIBLE
        }
    }
}