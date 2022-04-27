package com.bancempo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bancempo.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.findNavController()
        val navView = findViewById<NavigationView>(R.id.nav_view)

        NavigationUI.setupWithNavController(binding.navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)

        navView.setNavigationItemSelectedListener {
            when (it.itemId){
                R.id.goToTimeSlotList -> {
                    navController.navigate(R.id.action_timeSlotListFragment_self)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.goToShowProfile -> {
                    navController.navigate(R.id.action_timeSlotListFragment_to_showProfileFragment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.findNavController()

        return NavigationUI.navigateUp(navController, drawer)
    }

}