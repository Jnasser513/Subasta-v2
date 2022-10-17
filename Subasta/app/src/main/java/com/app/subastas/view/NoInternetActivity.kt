package com.app.subastas.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.subastas.AppApplication
import com.app.subastas.data.entity.SliderState
import com.app.subastas.databinding.NoInternetFragmentBinding
import com.app.subastas.view.util.Connectivity
import com.app.subastas.viewmodel.AuthViewModel
import com.app.subastas.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoInternetActivity : AppCompatActivity() {

    private lateinit var mBinding: NoInternetFragmentBinding

    private val connectivity = Connectivity()

    private val userApp by lazy {
        application as AppApplication
    }

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(userApp.authRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = NoInternetFragmentBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.actionUpdate.setOnClickListener {
            authViewModel.sliderState.observe(this) {
                checkConnectivity(it)
                finish()
            }
        }

        mBinding.refresh.setOnRefreshListener {
            authViewModel.sliderState.observe(this) {
                checkConnectivity(it)
                finish()
            }
        }
    }

    private fun checkConnectivity(state: List<SliderState>) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (connectivity.verifyConnection(this@NoInternetActivity)) {
                if (connectivity.isOnlineNet()) {
                    withContext(Dispatchers.Main) {
                        authViewModel.userData1.observe(this@NoInternetActivity) {
                            if (it.isEmpty()) {
                                if (state.isEmpty()) {
                                    startActivity(
                                        Intent(
                                            Intent(
                                                this@NoInternetActivity,
                                                SliderActivity::class.java
                                            )
                                        )
                                    )
                                } else if (!state[0].state) {
                                    startActivity(
                                        Intent(
                                            Intent(
                                                this@NoInternetActivity,
                                                SliderActivity::class.java
                                            )
                                        )
                                    )
                                } else {
                                    startActivity(
                                        Intent(
                                            Intent(
                                                this@NoInternetActivity,
                                                MainActivity::class.java
                                            )
                                        )
                                    )
                                }
                            } else {
                                val intent =
                                    (Intent(this@NoInternetActivity, HomeActivity::class.java))
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                            }
                            finish()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        startActivity(
                            Intent(
                                Intent(
                                    this@NoInternetActivity,
                                    NoInternetActivity::class.java
                                )
                            )
                        )
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    startActivity(
                        Intent(
                            Intent(
                                this@NoInternetActivity,
                                NoInternetActivity::class.java
                            )
                        )
                    )
                }
            }
        }
    }

}