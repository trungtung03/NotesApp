package com.example.notepad.base.base


import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.notepad.R


abstract class BaseActivity1<VB : ViewBinding> : AppCompatActivity() {
    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding(layoutInflater)
        setContentView(binding.root)
        init()
    }

    abstract fun getLayout(): Int
    abstract fun getViewBinding(inflater: LayoutInflater): VB
    abstract fun init()

    fun addFragment(toFragment: Fragment, fromFragment: Fragment) {
        supportFragmentManager.beginTransaction()

            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .add(R.id.fragment_container_view_tag, fromFragment).addToBackStack(fromFragment.tag)
            .hide(toFragment).commit()


    }


}