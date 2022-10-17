package com.app.subastas.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.subastas.AppApplication
import com.app.subastas.databinding.ActivityMainBinding
import com.app.subastas.viewmodel.AuthViewModel
import com.app.subastas.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private val userApp by lazy {
        application as AppApplication
    }

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(userApp.authRepository)
    }

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel.userData1.observe(this) {
            mBinding = ActivityMainBinding.inflate(layoutInflater)
                .apply {
                    viewmodel = authViewModel
                    lifecycleOwner = this@MainActivity
                }

            val change = intent.getBooleanExtra("change", false)
            if(change) {
                authViewModel.goChangePassword()
            }

            setContentView(mBinding.root)
        }
    }
}