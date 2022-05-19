package com.bancempo.activities

import com.bancempo.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bancempo.data.User
import com.bancempo.databinding.ActivityMainBinding
import com.bancempo.models.SharedViewModel
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: User
    private var userState: FirebaseUser? = null
    private val RC_SIGN_IN: Int = 1

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
                R.id.logoutItem -> {
                    logout()
                }




            }
            false
        }

        auth = Firebase.auth
        auth.addAuthStateListener { authState ->
            userState = authState.currentUser
            if (userState == null) {
                Log.d("AuthListener", "----------------null user")
            } else {
                Log.d("AuthListener", "--------------------OK user")
                /*model.getTrips().observe(this, Observer {
                    model.getTrips().removeObservers(this)
                }
                )
                initDrawerHeader(navView)*/
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser == null) {
            login()
        }
    }

    private fun login() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build()
        )

        val customLayout = AuthMethodPickerLayout.Builder(R.layout.sign_in)
            .setGoogleButtonId(R.id.sign_in_button)
            .setPhoneButtonId(R.id.sign_in_phone_button)
            .build()

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(customLayout)
                .setTheme(R.style.FirebaseUI_DefaultMaterialTheme)
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    private fun logout() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(0,0)
                finish()
                overridePendingTransition(0,0)
                Toast.makeText(this, "Logout successful!", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Log.d("Login result", "Successfully signed in")

            }

            Log.d("Login result", "Sign in success")
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Log.e("Login result", "Sign in failed")
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