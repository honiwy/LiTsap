package studio.honidot.litsap.login

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.R
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.User
import studio.honidot.litsap.data.UserManager
import studio.honidot.litsap.network.LoadApiStatus
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger
import studio.honidot.litsap.util.Util.getString

class LoginViewModel(private val repository: LiTsapRepository) : ViewModel() {

    lateinit var fbCallbackManager: CallbackManager
    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error


    val auth = FirebaseAuth.getInstance()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }



    fun loginFB() {
        _status.value = LoadApiStatus.LOADING
        fbCallbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(fbCallbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                _status.value = LoadApiStatus.ERROR
            }

            override fun onError(exception: FacebookException) {
                Logger.w("[${this::class.simpleName}] exception=${exception.message}")
                Logger.w("Login FB Error")
                exception.message?.let {
                    _error.value = if (it.contains("ERR_INTERNET_DISCONNECTED")) {
                        getString(R.string.internet_not_connected)
                    } else {
                        it
                    }
                }
                _status.value = LoadApiStatus.ERROR
            }
        })
        _loginAttempt.value = true
    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    auth.currentUser?.let {
                        UserManager.userId = it.uid
                        createUser(User(it.uid,it.displayName?:"沒有名字",3,0, emptyList(), emptyList(),0))
                    }
                } else {
                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    Toast.makeText(baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }

                // ...
            }
    }

    private fun createUser(user : User) {
        coroutineScope.launch {
            val result = repository.createUser(user)
            when (result) {
                is Result.Success -> {
                    loginSuccess()
                }
                is Result.Fail -> {
                    null
                }
                is Result.Error -> {
                    null
                }
                else -> {
                    null
                }
            }
        }
    }

    private val _loginAttempt = MutableLiveData<Boolean>()

    val loginAttempt: LiveData<Boolean>
        get() = _loginAttempt

    fun afterLogin() {
        _loginAttempt.value = null
    }


    // Handle leave login
    private val _navigateToMain = MutableLiveData<Boolean>()

    val navigateToMain: LiveData<Boolean>
        get() = _navigateToMain

    private fun loginSuccess() {
        Toast.makeText(
            LiTsapApplication.appContext,
            LiTsapApplication.instance.getString(R.string.login_success),
            Toast.LENGTH_SHORT
        ).show()
        _navigateToMain.value = true
    }

    fun onSucceeded() {
        _navigateToMain.value = null
    }

}