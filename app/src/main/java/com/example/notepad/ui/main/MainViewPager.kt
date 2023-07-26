package com.example.notepad.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MainViewPager(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {
    private var listFragment: ArrayList<Fragment> = arrayListOf(

    )

    override fun getCount(): Int {
        return listFragment.size
    }

    override fun getItem(position: Int): Fragment {
        return listFragment[position]
    }

    fun setData(listFragment: ArrayList<Fragment>) {
        this.listFragment = listFragment
        notifyDataSetChanged()
    }
}
