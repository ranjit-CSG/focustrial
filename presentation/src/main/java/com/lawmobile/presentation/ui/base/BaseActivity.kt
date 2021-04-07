package com.lawmobile.presentation.ui.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.entities.CameraInfo.isOfficerLogged
import com.lawmobile.domain.enums.EventType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.NeutralAlertInformation
import com.lawmobile.presentation.extensions.checkIfSessionIsExpired
import com.lawmobile.presentation.extensions.createAlertMobileDataActive
import com.lawmobile.presentation.extensions.createAlertProgress
import com.lawmobile.presentation.extensions.createAlertSessionExpired
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.security.RootedHelper
import com.lawmobile.presentation.ui.live.statusBar.x2.LiveStatusBarX2Fragment
import com.lawmobile.presentation.ui.login.LoginActivity
import com.lawmobile.presentation.utils.CameraEventsManager
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.lawmobile.presentation.utils.MobileDataStatus
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Timestamp
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    private val viewModel: BaseViewModel by viewModels()

    @Inject
    lateinit var mobileDataStatus: MobileDataStatus

    @Inject
    lateinit var cameraEventsManager: CameraEventsManager

    private var isLiveVideoOrPlaybackActive: Boolean = false
    private lateinit var mobileDataDialog: AlertDialog
    var isMobileDataAlertShowing = MutableLiveData<Boolean>()
    private var loadingDialog: AlertDialog? = null
    var onNotificationBatteryWarning: (() -> Unit)? = null
    var onNotificationStorageWarning: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)

        verifyDeviceIsNotRooted()
        setEventsManager()
        setBaseObservers()
        createMobileDataDialog()
        updateLastInteraction()
        startReadingEvents()
    }

    private fun setEventsManager() {
        viewModel.setNotificationManager(cameraEventsManager)
    }

    private fun verifyDeviceIsNotRooted() {
        if (RootedHelper.isDeviceRooted(this)) {
            finish()
            return
        }
    }

    private fun startReadingEvents() {
        if (isOfficerLogged) {
            reviewNotificationInCamera()
        }
    }

    private fun setBaseObservers() {
        mobileDataStatus.observe(this, Observer(::showMobileDataDialog))
        viewModel.logEventsLiveData().observe(this, ::handleEvents)
    }

    private fun handleEvents(result: Result<List<CameraEvent>>) {
        with(result) {
            doIfSuccess {
                reviewIfNotificationsContainsBatteryOrStorageWarning(it)
                createNotificationDialog(it.last()) {}
            }
            doIfError {
                println(it.message)
            }
        }
    }

    private fun reviewIfNotificationsContainsBatteryOrStorageWarning(names: List<CameraEvent>) {
        names.forEach { event ->
            if (event.name == LiveStatusBarX2Fragment.NAME_BATTERY_WARNING) {
                LiveStatusBarX2Fragment.blinkBatteryLevel = true
                onNotificationBatteryWarning?.invoke()
            }
            if (event.name == LiveStatusBarX2Fragment.NAME_STORAGE_WARNING) {
                LiveStatusBarX2Fragment.blinkStorageLevel = true
                onNotificationStorageWarning?.invoke()
            }
        }
    }

    private fun createMobileDataDialog() {
        val alertInformation = NeutralAlertInformation(
            R.string.mobile_data_status_title,
            R.string.mobile_data_status_message
        )
        mobileDataDialog = this.createAlertMobileDataActive(alertInformation)
    }

    private fun showMobileDataDialog(active: Boolean) {
        isMobileDataAlertShowing.postValue(active)
        if (active) mobileDataDialog.show()
        else mobileDataDialog.dismiss()
    }

    private fun reviewNotificationInCamera() {
        CameraHelper.getInstance().reviewNotificationInCamera {
            if (it.eventType == EventType.NOTIFICATION) runOnUiThread {
                createNotificationDialog(it) {}
            }
        }
    }

    override fun onStop() {
        super.onStop()
        updateLastInteraction()
    }

    override fun onRestart() {
        super.onRestart()
        if (checkIfSessionIsExpired() && this !is LoginActivity) this.createAlertSessionExpired()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        if (!isLiveVideoOrPlaybackActive && !isRecordingVideo && checkIfSessionIsExpired() && this !is LoginActivity) {
            return
        }
        updateLastInteraction()
    }

    fun updateLiveOrPlaybackActive(isActive: Boolean) {
        if (isActive) updateLastInteraction()
        isLiveVideoOrPlaybackActive = isActive
    }

    fun updateLastInteraction() {
        lastInteraction = Timestamp(System.currentTimeMillis())
    }

    fun showLoadingDialog() {
        EspressoIdlingResource.increment()
        loadingDialog = this.createAlertProgress()
        loadingDialog?.show()
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
        EspressoIdlingResource.decrement()
    }

    fun isInPortraitMode() =
        resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    companion object {
        const val PERMISSION_FOR_LOCATION = 100
        const val MAX_TIME_SESSION = 300000
        lateinit var lastInteraction: Timestamp
        var isRecordingVideo: Boolean = false
    }
}
