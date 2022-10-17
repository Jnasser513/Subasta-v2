package com.app.subastas.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.subastas.AppApplication
import com.app.subastas.data.entity.SliderState
import com.app.subastas.databinding.SplashActivityBinding
import com.app.subastas.view.util.Connectivity
import com.app.subastas.viewmodel.AuthViewModel
import com.app.subastas.viewmodel.ViewModelFactory

class SplashActivity: AppCompatActivity() {

    private lateinit var mBinding: SplashActivityBinding

    private val connectivity = Connectivity()

    private val userApp by lazy {
        application as AppApplication
    }

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(userApp.authRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        authViewModel.sliderState.observe(this) {
            initTimer(it)
        }
    }

    private fun initTimer(state: List<SliderState>) {
        Handler().postDelayed({
            checkConnectivity(state)
        }, 2000)
    }

    private fun checkConnectivity(state: List<SliderState>) {
        //if(connectivity.verifyConnection(this)) {
            //if(connectivity.isOnlineNet()) {
                authViewModel.userData1.observe(this) {
                    if(it.isEmpty()) {
                        if(state.isEmpty()) {
                            startActivity(Intent(Intent(this, SliderActivity::class.java)))
                        } else if (!state[0].state) {
                            startActivity(Intent(Intent(this, SliderActivity::class.java)))
                        } else {
                            startActivity(Intent(Intent(this, MainActivity::class.java)))
                        }
                    } else {
                        val intent = (Intent(this, HomeActivity::class.java))
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                    finish()
                }
            } /*else {
                startActivity(Intent(Intent(this, NoInternetActivity::class.java)))
            }
        } else {
            startActivity(Intent(Intent(this, NoInternetActivity::class.java)))
        }*/

}