package com.app.subastas.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.app.subastas.AppApplication
import com.app.subastas.databinding.SliderActivityBinding
import com.app.subastas.view.adapters.SliderAdapter
import com.app.subastas.view.fragments.FirstAddFragment
import com.app.subastas.view.fragments.SecondAddFragment
import com.app.subastas.view.fragments.ThirdAddFragment
import com.app.subastas.viewmodel.AuthViewModel
import com.app.subastas.viewmodel.ViewModelFactory

class SliderActivity: AppCompatActivity() {

    private lateinit var binding: SliderActivityBinding

    private val userApp by lazy {
        application as AppApplication
    }

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(userApp.authRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SliderActivityBinding.inflate(layoutInflater)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = this@SliderActivity
            }
        setContentView(binding.root)

        val viewPager: ViewPager2 = binding.sliderViewpager

        val fragments: ArrayList<Fragment> = arrayListOf(
            FirstAddFragment(),
            SecondAddFragment(),
            ThirdAddFragment()
        )

        val adapter = SliderAdapter(fragments, this)
        viewPager.adapter = adapter

        binding.indicator.setViewPager(viewPager)
        adapter.registerAdapterDataObserver(binding.indicator.adapterDataObserver)
        binding.indicator.createIndicators(3,0)
    }
}