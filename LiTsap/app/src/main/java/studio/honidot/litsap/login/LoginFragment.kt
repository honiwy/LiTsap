package studio.honidot.litsap.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import studio.honidot.litsap.NavigationDirections
import studio.honidot.litsap.databinding.FragmentLoginBinding
import studio.honidot.litsap.extension.getVmFactory


class LoginFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel> { getVmFactory() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.findUser(FirebaseAuth.getInstance().currentUser!!)

        viewModel.loginAttempt.observe(this, Observer {
            it?.let {
                LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
                viewModel.afterLogin()
            }
        })

        viewModel.navigateToMain.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToTaskFragment(FirebaseAuth.getInstance().currentUser!!.uid))
            viewModel.onSucceeded()
            }
        })
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.fbCallbackManager.onActivityResult(requestCode, resultCode, data)

    }

}