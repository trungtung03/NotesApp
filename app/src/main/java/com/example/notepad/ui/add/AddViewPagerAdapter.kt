package com.example.notepad.ui.add

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.notepad.ui.add.add_collect.AddCollectMoneyFragment
import com.example.notepad.ui.add.add_spending.AddSpendingFragment

class AddViewPagerAdapter(fragmentManager: FragmentManager, behavior: Int) :
    FragmentPagerAdapter(fragmentManager, behavior) {
    private var listFragment: ArrayList<Fragment> = arrayListOf(
        AddSpendingFragment.newInstance(),
        AddCollectMoneyFragment.newInstance(),
    )

    override fun getCount(): Int {
        return listFragment.size
    }

    override fun getItem(position: Int): Fragment {
        return listFragment[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
             0 -> "Chi"
            1-> "Thu"
            else -> super.getPageTitle(position)

        }
    }


}