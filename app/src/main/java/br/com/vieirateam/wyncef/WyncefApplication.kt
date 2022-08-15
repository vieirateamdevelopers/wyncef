package br.com.vieirateam.wyncef

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import br.com.vieirateam.wyncef.viewmodel.*

class WyncefApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private var instance: WyncefApplication? = null
        lateinit var agencyViewModel: AgencyViewModel
        lateinit var categoryViewModel: CategoryViewModel
        lateinit var deviceViewModel: DeviceViewModel
        lateinit var inventoryViewModel: InventoryViewModel
        lateinit var itemViewModel: ItemViewModel
        lateinit var tagViewModel: TagViewModel

        fun getInstance(): WyncefApplication {
            requireNotNull(instance) { "AndroidManifest.xml error" }
            return instance as WyncefApplication
        }

        fun getViewModel() {
            val context = instance as Application
            agencyViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(context).create(AgencyViewModel::class.java)
            categoryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(context).create(CategoryViewModel::class.java)
            deviceViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(context).create(DeviceViewModel::class.java)
            inventoryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(context).create(InventoryViewModel::class.java)
            itemViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(context).create(ItemViewModel::class.java)
            tagViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(context).create(TagViewModel::class.java)
        }
    }
}