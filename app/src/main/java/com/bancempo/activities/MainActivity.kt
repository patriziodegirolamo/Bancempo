package com.bancempo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bancempo.R
import com.bancempo.models.SharedViewModel
import com.bancempo.Skill
import com.bancempo.data.User
import com.bancempo.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    private val sharedVM: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,
            R.layout.activity_main
        )
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val navView = findViewById<NavigationView>(R.id.nav_view)

        NavigationUI.setupWithNavController(binding.navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)

        navView.setNavigationItemSelectedListener {
            when (it.itemId){
                R.id.goToTimeSlotList -> {
                    if (navController.currentDestination?.id != R.id.timeSlotListFragment) {

                        navController.navigate(R.id.timeSlotListFragment)
                    }
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true


                }
                R.id.goToShowProfile -> {
                    if (navController.currentDestination?.id != R.id.showProfileFragment) {
                        navController.navigate(R.id.showProfileFragment)
                    }
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        //TODO FUNZIONE PER TORNARE INDIETRO DAL MENU, CAPIRE SE FUNZIONA DA UNDO OPPURE SE BISOGNA SALVARE
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.findNavController()

        return NavigationUI.navigateUp(navController, drawer)
    }

}