package com.lawmobile.presentation.ui.login.x2

import android.Manifest
import android.content.Intent
import android.content.RestrictionsManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.User
import com.lawmobile.domain.entities.customEvents.LoginRequestErrorEvent
import com.lawmobile.domain.enums.BackOfficeType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.attachFragmentWithAnimation
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.extensions.isPermissionGranted
import com.lawmobile.presentation.keystore.KeystoreHandler
import com.lawmobile.presentation.ui.live.x2.LiveX2Activity
import com.lawmobile.presentation.ui.login.LoginBaseActivity
import com.lawmobile.presentation.ui.login.shared.Instructions
import com.lawmobile.presentation.ui.login.shared.StartPairing
import com.lawmobile.presentation.ui.login.state.LoginState
import com.lawmobile.presentation.ui.login.x2.fragment.devicePassword.DevicePasswordFragment
import com.lawmobile.presentation.ui.login.x2.fragment.officerId.OfficerIdFragment
import com.lawmobile.presentation.ui.sso.SSOActivity
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse

class LoginX2Activity : LoginBaseActivity() {

    override val parentTag: String
        get() = this::class.java.simpleName

    private val viewModel: LoginX2ViewModel by viewModels()

    private lateinit var authRequest: AuthorizationRequest
    private val officerIdFragment = OfficerIdFragment.createInstance(::onContinueClick)
    private val devicePasswordFragment = DevicePasswordFragment.createInstance(::onEditOfficerId)

    override val instructions: Instructions get() = devicePasswordFragment
    override val startPairing: StartPairing get() = devicePasswordFragment

    var officerId: String
        get() = viewModel.officerId
        set(value) {
            viewModel.officerId = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseViewModel = viewModel
        viewModel.updateConfigProgress.observe(this@LoginX2Activity, ::handleConfigResult)
        restoreBottomSheetState()
        setCollectors()
        viewModel.setObservers()
    }

    private fun handleConfigResult(result: Result<String>?) {
        with(result) {
            this?.doIfSuccess { onReceivedConfigFromBle() }
            this?.doIfError {
                showErrorSnackBar(R.string.error_getting_config_bluetooth, ::retryBleConnectionToFetchConfigs)
                hideLoadingDialog()
                setCollectors()
                viewModel.setObservers()
            }
        }
    }

    private fun retryBleConnectionToFetchConfigs() {
        initBleConnectionToFetchConfigs()
    }

    private fun initBleConnectionToFetchConfigs() {
        showLoadingDialog(R.string.connection_bluetooth)
        viewModel.fetchConfigFromBle(this)
    }

    private fun onReceivedConfigFromBle() {
        hideLoadingDialog()
        if (CameraInfo.backOfficeType == BackOfficeType.NEXUS) runOnUiThread {
            showLoadingDialog()
            viewModel.getAuthorizationEndpoints()
        }
        else {
            state = LoginState.X2.DevicePassword
        }
    }

    private fun LoginX2ViewModel.setObservers() {
        authEndpointsResult.observe(this@LoginX2Activity, ::handleAuthEndpointsResult)
        authRequestResult.observe(this@LoginX2Activity, ::handleAuthRequestResult)
        devicePasswordResult.observe(this@LoginX2Activity, ::handleDevicePasswordResult)
        userFromCameraResult.observe(this@LoginX2Activity, ::handleUserResult)
    }

    override fun handleLoginState(loginState: LoginState) {
        super.handleLoginState(loginState)
        with(loginState) {
            onOfficerId {
                showOfficerIdFragment()
                verifyLocationPermission()
            }
            onDevicePassword {
                showDevicePasswordFragment()
                setInstructionsListener()
                setStartPairingListener()
            }
        }
    }

    private fun handleAuthEndpointsResult(result: Result<AuthorizationEndpoints>) {
        with(result) {
            doIfSuccess { viewModel.getAuthorizationRequest(it) }
            doIfError { showRequestError() }
        }
    }

    private fun handleAuthRequestResult(result: Result<AuthorizationRequest>) {
        with(result) {
            doIfSuccess { goToSsoLogin(it) }
            doIfError { showRequestError() }
        }
    }

    private fun handleDevicePasswordResult(result: Result<String>) {
        with(result) {
            doIfSuccess {
                Log.d(TAG, "SSO Completed with Success:$result")
                // TODO: Hard coded SSID can be removed once X2 start broadcasting first part of email of before @
                // val hotspotName = "X" + officerId.substringBefore("@")
                // TODO: Using the hardcoded SSID for X2.
                val hotspotName = "X$officerId"
                // WifiApPasswordMode should be 1
                val hotspotPassword = it.takeLast(16)
                viewModel.suggestWiFiNetwork(hotspotName, hotspotPassword) { isConnected ->
                    state = if (isConnected) LoginState.PairingResult
                    else LoginState.X2.DevicePassword
                }
                waitToEnableContinue()
            }
            doIfError {
                Log.d(TAG, "SSO Completed with Error:$it")
                showRequestError()
            }
        }
    }

    private fun waitToEnableContinue() {
        lifecycleScope.launch {
            delay(ENABLE_CONTINUE_DELAY)
            officerIdFragment.setButtonContinueEnable(true)
            hideLoadingDialog()
        }
    }

    private fun showRequestError() {
        hideLoadingDialog()
        val errorEvent = LoginRequestErrorEvent.event
        createNotificationDialog(errorEvent) {
            setButtonText(resources.getString(R.string.OK))
            onConfirmationClick = {
                state = LoginState.X2.DevicePassword
            }
        }
        officerIdFragment.setButtonContinueEnable(true)
    }

    override fun handleUserResult(result: Result<User>) {
        super.handleUserResult(result)
        result.doIfSuccess { startLiveViewActivity() }
    }

    private fun startLiveViewActivity() {
        CameraInfo.isOfficerLogged = true
        val intent = Intent(this, LiveX2Activity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showOfficerIdFragment() {
        officerIdFragment.officerId = officerId
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = officerIdFragment,
            tag = OfficerIdFragment.TAG,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun showDevicePasswordFragment() {
        devicePasswordFragment.officerId = officerId
        supportFragmentManager.attachFragmentWithAnimation(
            containerId = R.id.fragmentContainer,
            fragment = devicePasswordFragment,
            tag = DevicePasswordFragment.TAG,
            animationIn = android.R.anim.fade_in,
            animationOut = android.R.anim.fade_out
        )
    }

    private fun onEditOfficerId(officerId: String) {
        this.officerId = officerId
        state = LoginState.X2.OfficerId
    }

    private fun fetchConfigsFromBle() {
        if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            val configs = KeystoreHandler.getConfigFromKeystore(this)
            if (configs == null) {
                runOnUiThread {
                    initBleConnectionToFetchConfigs()
                }
            } else {
                Log.d(TAG, "Found Configs in KeyStore...$configs")
                viewModel.saveConfigLocally(configs)
                onReceivedConfigFromBle()
            }
        }
    }

    private fun onContinueClick(isEmail: Boolean, officerId: String) {
        this.officerId = officerId
        CameraInfo.officerId = officerId
        fetchConfigsFromBle()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RestrictionsManager.RESULT_ERROR_INTERNAL) {
            state = LoginState.X2.DevicePassword
        }

        if (!viewModel.isUserAuthorized() && data != null) {
            val response = buildAuthorizationResponse(data)
            val exception = getAuthorizationException(data)

            when {
                response.authorizationCode != null -> {
                    showLoadingDialog()
                    viewModel.authenticateToGetToken(response, ::onTokenResponse)
                }
                exception != null -> {
                    showRequestError()
                }
            }
        } else {
            officerIdFragment.setButtonContinueEnable(true)
        }
    }

    private fun getAuthorizationException(data: Intent?) = AuthorizationException.fromIntent(data)

    private fun buildAuthorizationResponse(data: Intent) =
        AuthorizationResponse.Builder(authRequest).fromUri(data.data!!).build()

    private fun onTokenResponse(response: Result<TokenResponse>) {
        with(response) {
            doIfSuccess {
                Log.d(TAG, "onTokenResponse:Success")
                viewModel.saveToken(it.accessToken.toString())
                viewModel.getDevicePassword(CameraInfo.userId)
            }
            doIfError {
                Log.d(TAG, "onTokenResponse:Error:$it")
                showRequestError()
            }
        }
    }

    private fun goToSsoLogin(authRequest: AuthorizationRequest) {
        this.authRequest = authRequest
        val intent = Intent(baseContext, SSOActivity::class.java)
        startActivityForResult(intent, 100)
        hideLoadingDialog()
    }

    companion object {
        private const val ENABLE_CONTINUE_DELAY = 1000L
        private const val TAG = "LoginX2Activity"
    }
}
