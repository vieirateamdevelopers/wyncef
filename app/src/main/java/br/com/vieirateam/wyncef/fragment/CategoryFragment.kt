package br.com.vieirateam.wyncef.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import br.com.vieirateam.wyncef.R
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.actionmode.ActionModeCategory
import br.com.vieirateam.wyncef.activity.CategoryActivity
import br.com.vieirateam.wyncef.adapter.CategoryAdapter
import br.com.vieirateam.wyncef.entity.Category
import br.com.vieirateam.wyncef.interfaces.FilterBottomSheet
import br.com.vieirateam.wyncef.util.ConstantsUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.Serializable
import java.util.Date

class CategoryFragment : BaseFragment<CategoryAdapter, Category>(), FilterBottomSheet {

    lateinit var adapter: CategoryAdapter
    private lateinit var actionMode: ActionModeCategory

    override fun onActivityCreated() {
        actionMode = ActionModeCategory(this)
    }

    override fun addOnClickListener() {
        startItemActivity(Category(), true)
    }

    override fun resultItem(item: Serializable?) {
        item as Category
        if (save) {
            WyncefApplication.categoryViewModel.insert(item)
        } else {
            WyncefApplication.categoryViewModel.update(item)
        }
    }

    override fun onItemClick(item: Category, view: View) {
        if (actionMode.selected) {
            actionMode.selectedItem(item, view)
        } else {
            startItemActivity(item, false)
        }
    }

    override fun onItemLongClick(item: Category, view: View) {
        if (!actionMode.selected) {
            hideFloatingButton(true)
            (view.context as AppCompatActivity).startSupportActionMode(actionMode)
            actionMode.selected = true
        }
        actionMode.selectedItem(item, view)
    }

    override fun setAdapter(): CategoryAdapter {
        adapter = CategoryAdapter(this::onItemClick, this::onItemLongClick)
        return adapter
    }

    override fun onBindSearch(newText: String?) {
        filter.clear()
        WyncefApplication.categoryViewModel.filter(newText)
        WyncefApplication.categoryViewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            categories?.let {
                adapter.setItems(it)
                filter.addAll(it)
            }
        })
    }

    override fun onBindFilter(startDate: Date, finalDate: Date) {
        filter.clear()
        WyncefApplication.categoryViewModel.filter(startDate, finalDate)
        WyncefApplication.categoryViewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            categories?.let {
                adapter.setItems(it)
                filter.addAll(it)
            }
        })
    }

    override fun startItemActivity(item: Category, save: Boolean) {
        bundle.putSerializable(ConstantsUtil.ITEM, item)
        bundle.putBoolean(ConstantsUtil.SAVE, save)
        bundle.putString(ConstantsUtil.TITLE, getString(R.string.app_category))
        val intent = Intent(mView.context, CategoryActivity::class.java)
        intent.putExtra(ConstantsUtil.BUNDLE, bundle)
        startActivityForResult(intent, ConstantsUtil.REQUEST_CODE)
    }

    override fun getItems() {
        filter.clear()
        WyncefApplication.categoryViewModel.select()
        WyncefApplication.categoryViewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            categories?.let {
                adapter.setItems(it)
                filter.addAll(it)
                if (it.isEmpty()) showMessage(getString(R.string.alert_category_empty))
            }
        })
    }

    override fun configureFieldsFilter(dialog: BottomSheetDialog, view: View) {
        configureBottomSheetFilter(dialog, view)
    }

    override fun configureAscendingOrder(dialog: BottomSheetDialog) {
        filter.sortBy { category -> category.name }
        adapter.setItems(filter)
        dialog.dismiss()
    }

    override fun configureDescendingOrder(dialog: BottomSheetDialog) {
        filter.sortByDescending { category -> category.name }
        adapter.setItems(filter)
        dialog.dismiss()
    }

    override fun configureDateFilter(dialog: BottomSheetDialog) {
        showCalendar(dialog)
    }
}