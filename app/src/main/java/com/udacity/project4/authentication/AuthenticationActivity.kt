package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAuthenticationBinding
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initialization binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)
        binding.authButton.setOnClickListener { launchSignIn() } //Set On Click On Login Button
        observeAuthState() //Observe Auth State

    }

    private fun observeAuthState() {
        viewModel.authenticationState.observe(this) { authState ->
            when (authState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {startActivity(Intent(this, RemindersActivity::class.java)) ; finish()}
                else -> Log.d(TAG, "Auth state doesn't require any UI change $authState")
            }
        }
    }


    private fun launchSignIn() {
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build())
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), SIGN_IN_RESULT_CODE
        )
    }

   

    companion object {
        const val SIGN_IN_RESULT_CODE = 1005
        private const val TAG = "AuthenticationActivity"
    }
}
