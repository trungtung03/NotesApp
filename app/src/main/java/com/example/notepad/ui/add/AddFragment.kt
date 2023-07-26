package com.example.notepad.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.notepad.databinding.FragmentAddBinding
import com.example.notepad.base.base.BaseFragmentWithBinding

class AddFragment : BaseFragmentWithBinding<FragmentAddBinding>() {

    companion object {
        fun newInstance() = AddFragment()
    }

    private lateinit var viewModel: AddViewModel
    override fun getViewBinding(inflater: LayoutInflater): FragmentAddBinding =
        FragmentAddBinding.inflate(inflater)


    override fun init() {
        binding.viewPager.adapter = AddViewPagerAdapter(
            childFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        binding.tableLayout.setupWithViewPager(binding.viewPager)
    }

    override fun initData() {

    }

    override fun initAction() {
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddViewModel::class.java)
    }

}