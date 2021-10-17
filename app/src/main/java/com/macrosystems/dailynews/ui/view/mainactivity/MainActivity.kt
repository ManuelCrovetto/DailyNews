package com.macrosystems.dailynews.ui.view.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.macrosystems.dailynews.R
import com.macrosystems.dailynews.core.connectionadvisor.ConnectionStatusLiveData
import com.macrosystems.dailynews.core.ex.lostConnectionSnackBar
import com.macrosystems.dailynews.core.ex.reconnectedSnackBar
import com.macrosystems.dailynews.databinding.ActivityMainBinding
import com.macrosystems.dailynews.ui.factory.NewsFeedFragmentFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var fragmentFactory: NewsFeedFragmentFactory

    @Inject
    lateinit var connectionStatusLiveData: ConnectionStatusLiveData
    private var isConnected = true

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        setTheme(R.style.Splashscreen)
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_DailyNews)

        supportFragmentManager.fragmentFactory = fragmentFactory
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar()
        initObservers()
        setOnDestinationListenerForPersonalizedBackButton()
    }

    override fun onRestart() {
        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onRestart()
    }
    override fun onResume() {
        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onResume()

    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        setupActionBarWithNavController(navController)
        supportActionBar?.setDisplayShowTitleEnabled(false)

    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp()

    private fun initObservers() {
        connectionStatusLiveData.observe(this) { connectionStatus ->
            with(binding){
                isConnected = if (!connectionStatus){
                    root.lostConnectionSnackBar()
                    false
                } else {
                    if (!isConnected)
                        root.reconnectedSnackBar()
                    true
                }
            }
        }
    }

    private fun setOnDestinationListenerForPersonalizedBackButton() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.newsFeedFragment){
                binding.toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
            }
        }
    }
}