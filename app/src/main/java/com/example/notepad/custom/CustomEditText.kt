package com.example.notepad.custom

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText


class CustomEditText : AppCompatEditText {
    var onTouchListener: (() -> Unit)? = null

    constructor(context: Context?) : super(context!!) {
        initAction()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initAction()
    }


    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        initAction()
    }

    private fun initAction() {
        rootView.setOnTouchListener {v,event->
            onTouchListener?.invoke()
            requestFocus()
            val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this@CustomEditText, InputMethodManager.SHOW_IMPLICIT)
        }
    }



}