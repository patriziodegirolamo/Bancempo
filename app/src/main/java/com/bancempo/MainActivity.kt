package com.bancempo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bancempo.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        //finding navController
        val host : NavHostFragment? = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment?
        val navController = host?.findNavController()

        //connect the navigation drawer with the navigation controller
        navController?.let { NavigationUI.setupWithNavController(binding.navView, it) }

        if (navController != null) {
            NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        val host : NavHostFragment? = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment?
        val navController = host?.findNavController()

        return if(navController != null){
            NavigationUI.navigateUp(navController, drawer)
        }else false
    }

}