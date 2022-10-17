package com.app.subastas.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.app.subastas.AppApplication
import com.app.subastas.R
import com.app.subastas.databinding.HomeActivityBinding
import com.app.subastas.viewmodel.AuthViewModel
import com.app.subastas.viewmodel.ViewModelFactory

class HomeActivity: AppCompatActivity() {

    private lateinit var binding: HomeActivityBinding

    private val userApp by lazy {
        application as AppApplication
    }

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(userApp.authRepository)
    }

    private var type: Boolean? = null
    private var push: String? = null
    private var idLote: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeActivityBinding.inflate(layoutInflater)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = this@HomeActivity
            }

        type = intent?.getBooleanExtra("key", false)
        if(type == true) {
            authViewModel.goInscriptionsFromAuction()
        }


        /*push = intent.getStringExtra("push")
        idLote = intent.getIntExtra("idLote", 0)
        if(push?.contains("push") == true) {
            authViewModel.userData1.observe(this) {
                authViewModel.getLotDetail(it[0].accessToken!!, idLote.toString())
            }
            authViewModel.lotDetail.observe(this) { lote ->
                val bundle = Bundle()
                bundle.putString("idLote", idLote.toString())
                bundle.putLong("fechaInicio", lote.start_subasta!!.toLong())
                bundle.putLong("fechaFin", lote.finish_subasta!!.toLong())
                bundle.putLong("serverTime", lote.server_time!!.toLong())
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.home_nav) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.auctionInProgressFragment, bundle)
            }
        }*/

        setContentView(binding.root)
    }

}