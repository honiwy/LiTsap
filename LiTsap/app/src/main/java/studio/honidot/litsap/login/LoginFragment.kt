package studio.honidot.litsap.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.facebook.login.LoginManager
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.NavigationDirections
import studio.honidot.litsap.R
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

        binding.buttonGoogleLogin.setOnClickListener {
            Toast.makeText(context, LiTsapApplication.instance.getString(R.string.google_login_info), Toast.LENGTH_SHORT).show()
        }

        viewModel.loginAttempt.observe(this, Observer {
            it?.let {
                LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
                viewModel.afterLogin()
            }
        })


        viewModel.navigateToMain.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToTaskFragment())
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