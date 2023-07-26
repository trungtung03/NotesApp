package com.example.notepad.ui.history_revenue_expenditure

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.notepad.R
import com.example.notepad.base.base.BaseFragmentWithBinding
import com.example.notepad.databinding.FragmentHistoryRevenueExpenditureBinding
import com.example.quanlychitieu.utils.click


class HistoryRevenueExpenditureFragment :
    BaseFragmentWithBinding<FragmentHistoryRevenueExpenditureBinding>() {

    companion object {
        fun newInstance() = HistoryRevenueExpenditureFragment()
    }

    private lateinit var viewModel: HistoryRevenueExpenditureViewModel
    override fun getViewBinding(inflater: LayoutInflater): FragmentHistoryRevenueExpenditureBinding =
        FragmentHistoryRevenueExpenditureBinding.inflate(inflater)


    override fun init() {
        binding.viewPager.adapter = HistoryRevenueExpenditureAdapter(
            childFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        binding.viewPager.offscreenPageLimit = 2
    }

    override fun initData() {

    }

    override fun initAction() {
        binding.viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.tab1.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    binding.tab2.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    binding.select.animate().x(0f).duration = 100
                } else if (position == 1) {
                    binding.tab1.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    binding.tab2.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    val size: Int = binding.tab2.width
                    binding.select.animate().x(size.toFloat()).duration = 100
                } else {

                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
        binding.tab1.click {
            binding.tab1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.tab2.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.select.animate().x(0f).duration = 100
            binding.viewPager.currentItem = 0
        }
        binding.tab2.click {
            binding.tab1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tab2.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            val size: Int = binding.tab2.width
            binding.select.animate().x(size.toFloat()).duration = 100
            binding.viewPager.currentItem = 1
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[HistoryRevenueExpenditureViewModel::class.java]
    }

}