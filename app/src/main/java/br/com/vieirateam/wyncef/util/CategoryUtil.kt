package br.com.vieirateam.wyncef.util

import android.annotation.SuppressLint
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.entity.Category

object CategoryUtil {

    @SuppressLint("UseSparseArrays")
    fun getCategories(): MutableList<Category> {
        val icons = mutableListOf<Category>()
        icons.add(Category(icon = R.drawable.ic_drawable_device_work_station, name = "Estação de Trabalho", initials = "ET"))
        icons.add(Category(icon = R.drawable.ic_drawable_device_financial_station, name = "Estação Financeira", initials = "EF"))
        icons.add(Category(icon = R.drawable.ic_drawable_device_financial_station, name = "Estação de Captura", initials = "EC"))
        icons.add(Category(icon = R.drawable.ic_drawable_device, name = "Notebook / Ultrabook", initials = "NB"))
        icons.add(Category(icon = R.drawable.ic_drawable_device_low_plataform, name = "Baixa Plataforma", initials = "SH"))
        icons.add(Category(icon = R.drawable.ic_drawable_device_router, name = "Roteador", initials = "RA"))
        icons.add(Category(icon = R.drawable.ic_drawable_device_switch, name = "Switch", initials = "SW"))
        icons.add(Category(icon = R.drawable.ic_drawable_device_recycler_safe, name = "Cofre Reciclador", initials = "CR"))
        icons.add(Category(icon = R.drawable.ic_drawable_device_printer, name = "Impressora", initials = "PR"))
        icons.add(Category(icon = R.drawable.ic_drawable_device_printer, name = "IFC", initials = "IC"))
        icons.add(Category(icon = R.drawable.ic_drawable_device_scanner, name = "Scanner", initials = "SC"))
        icons.add(Category(icon = R.drawable.ic_drawable_device_atm, name = "TAA", initials = "AT"))
        icons.add(Category(icon = R.drawable.ic_drawable_device_thin_client, name = "Thin Client", initials = "TH"))
        icons.add(Category(icon = R.drawable.ic_drawable_device_totem, name = "Totem", initials = "TO"))
        icons.add(Category(icon = R.drawable.ic_drawable_device_other, name = "Outros", initials = "OT"))
        return icons
    }
}