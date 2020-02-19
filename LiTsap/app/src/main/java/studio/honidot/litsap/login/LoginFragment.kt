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
import studio.honidot.litsap.NavigationDirections
import studio.honidot.litsap.databinding.FragmentLoginBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.util.Logger


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
            Toast.makeText(context, "Available soon!", Toast.LENGTH_SHORT).show()
        }

        viewModel.loginFacebook.observe(this, Observer {
            it?.let {
                Logger.w("viewModel.loginFacebook.observe, it=$it")
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
            viewModel.doneloginFacebook()
            }
        })
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.d("fragment onActivityResult")
        viewModel.fbCallbackManager.onActivityResult(requestCode, resultCode, data)

    }

}