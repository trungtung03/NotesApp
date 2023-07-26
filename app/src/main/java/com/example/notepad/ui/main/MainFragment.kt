package com.example.notepad.ui.main

import android.view.LayoutInflater
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.notepad.R
import com.example.notepad.base.base.BaseFragmentWithBinding
import com.example.notepad.databinding.FragmentMainBinding

class MainFragment : BaseFragmentWithBinding<FragmentMainBinding>() {
    private val viewPager: MainViewPager by lazy {
        MainViewPager(
            childFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
    }


    override fun getViewBinding(inflater: LayoutInflater): FragmentMainBinding =
        FragmentMainBinding.inflate(inflater)

    override fun init() {
        viewPager.setData(mainActivity.getListFragment())
        binding.mainViewPager.adapter = viewPager
        binding.mainViewPager.offscreenPageLimit = 4
    }

    override fun initData() {

    }

    override fun initAction() {
        binding.mainViewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                binding.bottomNavigationView.selectedItemId = when (position) {
                    0 -> R.id.home
                    1 -> R.id.add
                    2 -> R.id.note
                    else -> R.id.home
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        binding.bottomNavigationView.setOnItemSelectedListener() {
            binding.mainViewPager.currentItem = when (it.itemId) {
                R.id.home -> 0
                R.id.add -> 1
                R.id.note -> 2
                else -> 0
            }
            true
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()
    }
}