package com.example.notepad.ui.history_revenue_expenditure

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.notepad.ui.history_revenue_expenditure.collect_money.CollectMoneyFragment
import com.example.notepad.ui.history_revenue_expenditure.spending_money.SpendingMoneyFragment

class HistoryRevenueExpenditureAdapter(fragmentManager: FragmentManager, behavior: Int) :
    FragmentStatePagerAdapter(fragmentManager, behavior) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CollectMoneyFragment.newInstance()
            1 -> SpendingMoneyFragment.newInstance()
            else -> CollectMoneyFragment.newInstance()
        }
    }
}