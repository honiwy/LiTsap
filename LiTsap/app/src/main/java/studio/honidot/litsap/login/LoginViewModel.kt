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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
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

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    private val _loginVia = MutableLiveData<String>()

    val loginVia: LiveData<String>
        get() = _loginVia

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun findUser(firebaseUser: FirebaseUser) {
        coroutineScope.launch {
            val result = repository.findUser(firebaseUser.uid)
            _user.value = when (result) {
                is Result.Success -> {
                    if (result.data != null) {
                        result.data
                    } else {
                        Logger.w("Oops! You create new uer: loginVia ${_loginVia.value}")
                        val newUser = User(
                            firebaseUser.uid,
                            _loginVia.value ?: "Unknown",
                            firebaseUser.displayName ?: "無名氏",
                            3,
                            0,
                            emptyList(),
                            emptyList(),
                            0
                        )
                        createUser(newUser)
                        newUser
                    }
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

    private fun createUser(user: User) {
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

    private fun loginSuccess() {
        _navigateToMain.value = true
        Toast.makeText(
            LiTsapApplication.appContext,
            LiTsapApplication.instance.getString(R.string.login_success),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun onSucceeded() {
        _navigateToMain.value = null
    }

    lateinit var fbCallbackManager: CallbackManager

    fun loginFacebook() {
        if (_user.value != null && _user.value!!.loginVia == "Facebook") {
            loginSuccess()
        } else {
            fbCallbackManager = CallbackManager.Factory.create()//build callback
            LoginManager.getInstance().registerCallback(fbCallbackManager, object :
                FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    handleFacebookAccessToken(loginResult.accessToken)
                }//if login success

                override fun onCancel() {
                }

                override fun onError(exception: FacebookException) {
                    Logger.w("[${this::class.simpleName}] exception=${exception.message}")
                }
            })//register call back
            _loginVia.value = "Facebook" //active login
            _loginAttempt.value = true
        }
    }

    fun loginGoogle() {
        if (_user.value != null && _user.value!!.loginVia == "Google") {
            loginSuccess()
        } else {
            _loginVia.value = "Google" //active login
            _loginAttempt.value = true
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    findUser(auth.currentUser!!) //make sure whether user account is existed in Firebase if not then create a new one
                    loginSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    Logger.w("Authentication failed. signInWithCredential:failure: ${task.exception}")
                }
            }
    }

    private val auth = FirebaseAuth.getInstance()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
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

    fun firebaseAuthWithGoogle(token: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(token.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    findUser(auth.currentUser!!)
                    loginSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    Logger.w("Authentication failed. signInWithCredential:failure: ${task.exception}")
                }
            }
    }

}