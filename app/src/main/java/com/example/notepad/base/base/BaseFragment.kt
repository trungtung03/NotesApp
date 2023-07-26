package com.example.notepad.base.base


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notepad.activity.MainActivity
import com.example.notepad.ui.MainQLTActivity
import com.google.android.material.appbar.MaterialToolbar

abstract class BaseFragment : Fragment() {
    var onBackNavigation: (() -> Unit)? = null
    var hideFragmentToThis: (() -> Unit)? = null
   protected val mainActivity: MainQLTActivity by lazy {
        activity as MainQLTActivity
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideFragmentToThis?.invoke()
        init()
        initData()
        initAction()
        onBackNavigation = {
            init()
            initData()
            initAction()
        }
    }


    open fun setToolbar(view: MaterialToolbar) {

    }


    abstract fun init()
    abstract fun initData()
    abstract fun initAction()


    fun finish() {
        requireActivity().finish()
    }


    fun showLoading() {

    }

    fun hideLoading() {

    }

}