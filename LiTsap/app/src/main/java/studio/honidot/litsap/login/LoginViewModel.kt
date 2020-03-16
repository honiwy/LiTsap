package studio.honidot.litsap.login

import android.icu.util.Calendar
import android.text.format.DateFormat
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
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger
import studio.honidot.litsap.util.Util.getString
import java.util.*

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

    fun findUser(firebaseUser: FirebaseUser, firstLogin: Boolean) {
        coroutineScope.launch {
            val result = repository.findUser(firebaseUser.uid)
            _user.value = when (result) {
                is Result.Success -> {
                    if (result.data != null) {
                        if (!firstLogin) {
                            loginSuccess()
                        }
                        result.data
                    } else {
                        val newUser = User(
                            userId = firebaseUser.uid,
                            loginVia = _loginVia.value ?: getString(R.string.login_via_unknown),
                            userName = firebaseUser.displayName
                                ?: getString(R.string.login_name_unknown),
                            iconId = 0
                        )
                        createUser(newUser, firstLogin)
                        null
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

    private fun createUser(user: User, firstLogin: Boolean) {
        coroutineScope.launch {
            _user.value = when (repository.createUser(user)) {
                is Result.Success -> {
                    if (!firstLogin) {
                        loginSuccess()
                    }
                    user
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

    private fun eraseTaskDone(taskId: String) {
        coroutineScope.launch {
            repository.eraseTaskDone(taskId)
        }
    }

    private fun loginSuccess() {
        coroutineScope.launch {
            val date =
                DateFormat.format(
                    LiTsapApplication.instance.getString(R.string.diary_select_date),
                    Date(Calendar.getInstance().timeInMillis)
                ).toString()

            if ((_loginVia.value == getString(R.string.facebook) || _user.value?.loginVia == getString(
                    R.string.facebook
                )) && UserManager.lastTimeFB != date
            ) {
                _user.value?.let {
                    it.ongoingTasks.forEach { taskId ->
                        eraseTaskDone(taskId)
                    }
                    repository.eraseTodayDoneCount(it.userId)
                }
                UserManager.lastTimeFB = date
            } else if ((_loginVia.value == getString(R.string.google) || _user.value?.loginVia == getString(
                    R.string.google
                )) && UserManager.lastTimeGoogle != date
            ) {
                _user.value?.let {
                    it.ongoingTasks.forEach { taskId ->
                        eraseTaskDone(taskId)
                    }
                    repository.eraseTodayDoneCount(it.userId)
                }
                UserManager.lastTimeGoogle = date
            }
            navigateToMain()
        }
    }


    private fun navigateToMain() {
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
        if (_user.value != null && _user.value?.loginVia == getString(R.string.facebook)) {
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
            _loginVia.value = getString(R.string.facebook) //active login
            _loginAttempt.value = true
        }
    }

    fun loginGoogle() {
        if (_user.value != null && _user.value?.loginVia == getString(R.string.google)) {
            loginSuccess()
        } else {
            _loginVia.value = getString(R.string.google) //active login
            _loginAttempt.value = true
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    findUser(
                        auth.currentUser!!,
                        false
                    ) //make sure whether user account is existed in Firebase if not then create a new one
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
                    findUser(auth.currentUser!!, false)
                } else {
                    // If sign in fails, display a message to the user.
                    Logger.w("Authentication failed. signInWithCredential:failure: ${task.exception}")
                }
            }
    }

}