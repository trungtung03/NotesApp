package com.example.notepad.ui

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.example.notepad.R
import com.example.notepad.base.base.BaseActivity1
import com.example.notepad.base.base.BaseFragment
import com.example.notepad.databinding.ActivityMainQltcBinding
import com.example.notepad.ui.add.AddFragment
import com.example.notepad.ui.history_revenue_expenditure.HistoryRevenueExpenditureFragment
import com.example.notepad.ui.home.HomeFragment
import com.example.notepad.ui.main.MainFragment


class MainQLTActivity : BaseActivity1<ActivityMainQltcBinding>() {

    private val mainFragment = MainFragment.newInstance()
    val homeFragment = HomeFragment.newInstance()
    val addFragment = AddFragment.newInstance()
    val history = HistoryRevenueExpenditureFragment.newInstance()
    override fun getLayout(): Int = R.layout.activity_main_qltc

    override fun getViewBinding(inflater: LayoutInflater): ActivityMainQltcBinding =
        ActivityMainQltcBinding.inflate(inflater)

    override fun init() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(MainFragment::class.java.name)
        if (fragment == null) {
            supportFragmentManager.beginTransaction().add(
                R.id.fragment_container_view_tag,
                mainFragment,
                MainFragment::class.java.name,
            ).commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.let {
                val fragment = it.fragments.get(it.fragments.size - 2) as BaseFragment
                fragment.onBackNavigation?.invoke()
            }
        }
        super.onBackPressed()
    }

    fun getListFragment(): ArrayList<Fragment> =
        arrayListOf(homeFragment, addFragment, history)
}