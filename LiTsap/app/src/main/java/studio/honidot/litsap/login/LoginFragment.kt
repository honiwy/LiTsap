package studio.honidot.litsap.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import studio.honidot.litsap.NavigationDirections
import studio.honidot.litsap.R
import studio.honidot.litsap.databinding.FragmentLoginBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.util.Logger


class LoginFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel> { getVmFactory() }
    // Configure Google Sign In

    val RC_SIGN_IN = 102

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        FirebaseAuth.getInstance().currentUser?.let {
            viewModel.findUser(it,true)
        }
        binding.buttonGoogleLogin.isSelected = true
        binding.buttonGoogleLogin.ellipsize = TextUtils.TruncateAt.MARQUEE
        binding.buttonFacebookLogin.isSelected = true
        binding.buttonFacebookLogin.ellipsize = TextUtils.TruncateAt.MARQUEE

        viewModel.loginAttempt.observe(this, Observer {
            it?.let {
                if (viewModel.loginVia.value == "Google") {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(context!!, gso)
                    val signInIntent = googleSignInClient.signInIntent
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                    viewModel.afterLogin()
                } else if (viewModel.loginVia.value == "Facebook") {
                    LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
                    viewModel.afterLogin()
                }
            }
        })

        viewModel.navigateToMain.observe(this, Observer {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navigateToTaskFragment(
                        FirebaseAuth.getInstance().currentUser!!.uid
                    )
                )
                viewModel.onSucceeded()
            }
        })
        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.w("override fun onActivityResult")
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                viewModel.firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Logger.w("Google sign in failed $e")
                // ...
            }
        }
        else{
            viewModel.fbCallbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

}