package com.bancempo.activities

import android.content.ClipData
import com.bancempo.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 1

    private val sharedVM: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val navView = findViewById<NavigationView>(R.id.nav_view)

        NavigationUI.setupWithNavController(binding.navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)

        sharedVM.currentUser.observe(this){
            println("messaggio: currentUser -> ${it?.email}")
        }

        sharedVM.authUser.observe(this){
            println("messaggio: authuse -> ${it?.email}")
        }

        sharedVM.services.observe(this){
            println("messaggio: servs -> ${it.size}")
        }

        sharedVM.myAdvs.observe(this){
            println("messaggio: myadvs -> ${it.size}")
        }

        sharedVM.advs.observe(this){
            println("messaggio: advs -> ${it.size}")
        }

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
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
                    //sharedVM.cleanAfterLogout()
                }


            }
            false
        }

        sharedVM.authUser.observe(this) { firebaseUser ->
            if (firebaseUser == null) {
                Log.d("AuthListener", "----------------null user")
            } else {
                Log.d("AuthListener", "--------------------OK user")
                sharedVM.createUserIfDoesNotExists()
            }
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val authUser = sharedVM.authUser.value
        if (authUser == null) {
            login()
        }
        else {
            findViewById<NavigationView>(R.id.nav_view).menu.findItem(R.id.sign_in_button).isVisible = false
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
                overridePendingTransition(0, 0)
                finish()
                overridePendingTransition(0, 0)
                Toast.makeText(this, "Logout successful!", Toast.LENGTH_SHORT).show()
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Log.d("messaggio: Login result", "Successfully signed in")
                findViewById<NavigationView>(R.id.nav_view).menu.findItem(R.id.sign_in_button).isVisible = false
                sharedVM.afterLogin()
            }

        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Log.e("messaggio: Login result", "Sign in failed")
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        //TODO FUNZIONE PER TORNARE INDIETRO DAL MENU, CAPIRE SE FUNZIONA DA UNDO OPPURE SE BISOGNA SALVARE
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.findNavController()

        return NavigationUI.navigateUp(navController, drawer)
    }


}