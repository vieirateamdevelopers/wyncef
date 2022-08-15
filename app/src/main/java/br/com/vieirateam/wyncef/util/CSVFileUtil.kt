package br.com.vieirateam.wyncef.util

import android.annotation.SuppressLint
import android.os.Handler
import android.util.DisplayMetrics
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.entity.Category
import br.com.vieirateam.wyncef.entity.Device
import br.com.vieirateam.wyncef.entity.Item
import br.com.vieirateam.wyncef.fragment.ItemsFragment
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.android.synthetic.main.bottom_sheet_progress.view.linear_layout_progress
import kotlinx.android.synthetic.main.bottom_sheet_progress.view.text_view_progress
import java.lang.NumberFormatException

object CSVFileUtil {

    @SuppressLint("DefaultLocale")
    fun import(fragment: ItemsFragment, path: String) {
        val agency = fragment.inventory.agency
        val inventory = fragment.inventory
        var bufferedReader: BufferedReader? = null
        var line: String?
        var item: Item
        inventory.path = path
        inventory.importFile = false
        WyncefApplication.inventoryViewModel.update(inventory)

        try {
            bufferedReader = BufferedReader(InputStreamReader(FileInputStream(inventory.path.toString()), Charset.forName("UTF-8")))
            bufferedReader.readLine()
            bufferedReader.readLine()
            line = bufferedReader.readLine()

            while (line != null) {
                val row = line.split(";")
                if (row.isNotEmpty()) {
                    val inventoryOrder = row[0].toLong()
                    val categoryName = row[1].toUpperCase().trim()
                    val serialNumber = row[2].toUpperCase().trim()

                    if (categoryName.isNotEmpty() && serialNumber.isNotEmpty()) {
                        showProgress(fragment, fragment.getString(R.string.alert_progress_import), fragment.getString(R.string.alert_complete_import))
                        WyncefApplication.categoryViewModel.getScope().launch(Dispatchers.Main) {
                            val category = WyncefApplication.categoryViewModel.selectCategory(categoryName)
                            val device = WyncefApplication.deviceViewModel.selectDevice(serialNumber)
                            if (category == null) {
                                item = if (device == null) {
                                    val newDevice = Device(agency = agency, category = Category(), serialNumber = serialNumber, status = "EM USO")
                                    WyncefApplication.deviceViewModel.insert(newDevice)
                                    Item(inventory = inventory, device = newDevice, order = inventoryOrder)
                                } else {
                                    Item(inventory = inventory, device = device, order = inventoryOrder)
                                }
                            } else {
                                item = if (device == null) {
                                    val newDevice = Device(agency = agency, category = category, serialNumber = serialNumber, status = "EM USO")
                                    WyncefApplication.deviceViewModel.insert(newDevice)
                                    Item(inventory = inventory, device = newDevice, order = inventoryOrder)
                                } else {
                                    Item(inventory = inventory, device = device, order = inventoryOrder)
                                }
                            }
                            WyncefApplication.itemViewModel.insert(item)
                        }
                    }
                }
                line = bufferedReader.readLine()
            }
            if (fragment.bottomDialog != null) {
                val dialog = fragment.bottomDialog
                dialog?.dismiss()
            }
        } catch (exception : NumberFormatException) {
            showMessage(fragment, fragment.getString(R.string.alert_persmissions_read))
        } catch (exception: IOException) {
            showMessage(fragment, fragment.getString(R.string.alert_persmissions_read))
        } finally {
            try {
                bufferedReader?.close()
            } catch (exception: IOException) {
                showMessage(fragment, fragment.getString(R.string.alert_persmissions_read))
            }
        }
    }

    fun export(fragment: ItemsFragment) {
        var fileWriter: FileWriter? = null
        val agency = fragment.inventory.agency
        val inventory = fragment.inventory
        val items = fragment.adapter.getItems().sortedBy { it.order }
        val site = "${agency.cgc}-${agency.name}, ${agency.state}"
        val arrayStatus = fragment.resources.getStringArray(R.array.text_view_example_status)
        val header = "Site;Item;Série;Nome Lógico;Patrimônio;OBS - em uso / sem uso / não encontrado"

        try {
            showProgress(fragment, fragment.getString(R.string.alert_progress_export), fragment.getString(R.string.alert_complete_export))
            fileWriter = if (inventory.path == null) {
                FileWriter("/storage/emulated/0/wyncef.csv")
            } else {
                FileWriter(inventory.path.toString(), false)
            }
            fileWriter.append(header)
            fileWriter.append("\n")

            for (item in items) {
                fileWriter.append(site)
                fileWriter.append(";")
                fileWriter.append(item.device.category.name)
                fileWriter.append(";")
                fileWriter.append(item.device.serialNumber)
                fileWriter.append(";")
                if (item.device.status == arrayStatus[2]) {
                    fileWriter.append("-")
                    fileWriter.append(";")
                    fileWriter.append("-")
                    fileWriter.append(";")
                    fileWriter.append(item.device.status)
                } else {
                    if (item.device.logicalName == null) {
                        fileWriter.append("-")
                        fileWriter.append(";")
                    } else {
                        val logicalName = "${agency.state}${agency.cgc}${item.device.category.initials}${item.device.logicalName}"
                        fileWriter.append(logicalName)
                        fileWriter.append(";")
                    }
                    val patrimony = item.device.patrimony
                    if (patrimony == null) {
                        fileWriter.append("-")
                        fileWriter.append(";")
                    } else {
                        fileWriter.append(patrimony)
                        fileWriter.append(";")
                    }
                    fileWriter.append(item.device.status)
                }
                fileWriter.append("\n")
            }
            if (fragment.bottomDialog != null) {
                val dialog = fragment.bottomDialog
                dialog?.dismiss()
            }
        } catch (exception: IOException) {
            showMessage(fragment, fragment.getString(R.string.alert_persmissions_write))
        } finally {
            try {
                fileWriter?.flush()
                fileWriter?.close()
            } catch (exception: IOException) {
                showMessage(fragment, fragment.getString(R.string.alert_persmissions_write))
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showProgress(fragment: ItemsFragment, messageStart: String, messageEnd: String) {
        hideButtons(fragment, true)
        val activity = fragment.requireActivity()
        val bottomSheetDialog = BottomSheetUtil.show(activity, R.layout.bottom_sheet_progress, true)
        val dialog = bottomSheetDialog.first
        val view = bottomSheetDialog.second
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.linear_layout_progress.minimumHeight = displayMetrics.heightPixels
        view.text_view_progress.text = messageStart

        Handler().postDelayed({
            dialog.dismiss()
            showMessage(fragment, messageEnd)
        }, ConstantsUtil.DELAY)
    }

    private fun showMessage(fragment: ItemsFragment, message: String) {
        fragment.showMessage(message, Snackbar.LENGTH_LONG)
        hideButtons(fragment, false)
    }

    private fun hideButtons(fragment: ItemsFragment, hide: Boolean) {
        fragment.hideFloatingButton(hide)
    }
}