package br.com.vieirateam.wyncef.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.vieirateam.wyncef.util.FabButtonUtil
import br.com.vieirateam.wyncef.util.ConstantsUtil
import br.com.vieirateam.wyncef.util.SnackbarUtil
import com.google.android.material.snackbar.Snackbar

abstract class GenericFragment(private val layoutID: Int) : Fragment() {

    lateinit var mView: View
    private var mSnackBar: Snackbar? = null

    protected var fabMenu = false
    protected var bundle = Bundle()
    protected lateinit var fabButtonUtil: FabButtonUtil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(layoutID, container, false)
        setHasOptionsMenu(true)
        return mView
    }

    override fun onDetach() {
        super.onDetach()
        fabButtonUtil.closeFabMenu()
        if (mSnackBar != null) mSnackBar?.dismiss()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fabButtonUtil = FabButtonUtil(activity as AppCompatActivity)
        fabMenu = arguments?.getBoolean(ConstantsUtil.FAB) as Boolean
        posActivityCreated(savedInstanceState)

        fabButtonUtil.frameLayout.setOnClickListener {
            fabButtonUtil.configureFabMenu()
        }
    }

    fun showMessage(message: String, duration: Int) {
        mSnackBar = SnackbarUtil.show(mView, message, duration)
    }

    abstract fun posActivityCreated(savedInstanceState: Bundle?)
}