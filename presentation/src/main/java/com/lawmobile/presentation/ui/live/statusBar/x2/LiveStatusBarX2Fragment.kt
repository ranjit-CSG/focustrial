package com.lawmobile.presentation.ui.live.statusBar.x2

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.customEvents.LowStorageEvent
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentLiveStatusBarX2Binding
import com.lawmobile.presentation.extensions.createNotificationDialog
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.startAnimationIfEnabled
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.live.statusBar.LiveStatusBarBaseFragment
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBarColors
import com.safefleet.mobile.safefleet_ui.widgets.linearProgressBar.SafeFleetLinearProgressBarRanges
import kotlin.math.roundToInt

class LiveStatusBarX2Fragment : LiveStatusBarBaseFragment() {

    private val binding: FragmentLiveStatusBarX2Binding get() = _binding!!
    private var _binding: FragmentLiveStatusBarX2Binding? = null

    private lateinit var storageBarRanges: SafeFleetLinearProgressBarRanges
    private lateinit var storageBarColors: SafeFleetLinearProgressBarColors

    private var wasLowStorageAlertShowed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isInPortraitMode()) {
            setSharedObservers()
            setObservers()
        }
        _binding = FragmentLiveStatusBarX2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureProgressBars()
        setSharedViews()
        setListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getCameraStatus(isViewLoaded)
    }

    private fun setObservers() {
        sharedViewModel.storageLiveData.observe(viewLifecycleOwner, ::setStorageLevels)
        sharedViewModel.batteryLevelLiveData.observe(viewLifecycleOwner, ::setBatteryLevel)
    }

    private fun configureProgressBars() {
        batteryBarRanges = SafeFleetLinearProgressBarRanges(
            highRange = Range(36, 100),
            mediumRange = Range(6, 35),
            lowRange = Range(0, 5)
        )
        batteryBarColors = SafeFleetLinearProgressBarColors(
            highRangeColor = R.color.lightGreen,
            mediumRangeColor = R.color.colorOrange,
            lowRangeColor = R.color.red
        )
        storageBarRanges = SafeFleetLinearProgressBarRanges(
            highRange = Range(85, 100),
            mediumRange = Range(15, 84),
            lowRange = Range(0, 14)
        )
        storageBarColors = SafeFleetLinearProgressBarColors(
            highRangeColor = R.color.lightGreen,
            mediumRangeColor = R.color.lightGreen,
            lowRangeColor = R.color.red
        )
        highRangeColor = batteryBarColors.highRangeColor
        binding.progressBatteryLevel.setBehavior(batteryBarRanges, batteryBarColors)
        binding.progressStorageLevel.setBehavior(storageBarRanges, storageBarColors)
    }

    private fun setSharedViews() {
        parentLayout = binding.layoutStatusBar
        textViewBattery = binding.textViewBatteryPercent
        progressBarBattery = binding.progressBatteryLevel
        imageViewBattery = binding.imageViewBattery
    }

    private fun setListeners() {
        with(BaseActivity) {
            onBatteryLevelChanged = ::manageBatteryLevel
            onStorageLevelChanged = ::manageStorageLevel
            onLowBattery = ::manageLowBattery
            onLowStorage = ::manageLowStorage
        }
    }

    private fun manageLowStorage() {
        activity?.runOnUiThread {
            binding.imageViewStorage.startAnimationIfEnabled(blinkAnimation)
        }
    }

    private fun manageLowBattery(value: Int?) {
        activity?.runOnUiThread {
            wasNotificationArriveForLowBattery = true
            value?.let {
                if (currentMinutesAfterNotifications == 0) currentMinutesAfterNotifications = value
                manageBatteryLevel(getPercentLeftAfterNotification(it))
                currentMinutesAfterNotifications = value
            }
            imageViewBattery.startAnimationIfEnabled(blinkAnimation)
        }
    }

    private fun setStorageLevels(result: Event<Result<List<Double>>>) {
        result.getContentIfNotHandled()?.run {
            doIfSuccess {
                manageStorageLevel(getAvailableStoragePercent(it))
            }
            doIfError {
                binding.textViewStorageLevels.text = getString(R.string.not_available)
                progressBarBattery.setProgress(0)
                parentLayout.showErrorSnackBar(getString(R.string.storage_level_error))
            }
        }
        onInformationLoaded()
        EspressoIdlingResource.decrement()
    }

    private fun onInformationLoaded() {
        CameraInfo.onReadyToGetNotifications?.invoke()
    }

    private fun manageStorageLevel(availablePercent: Double) {
        activity?.runOnUiThread {
            setColorInStorageLevel(availablePercent)
            setTextStorageLevel(availablePercent)
            checkPercentToShowNotification(availablePercent)
        }
    }

    private fun checkPercentToShowNotification(availablePercent: Double) {
        if (availablePercent.roundToInt() <= LOW_STORAGE_LEVEL) {
            if (!wasLowStorageAlertShowed) {
                activity?.runOnUiThread {
                    context?.createNotificationDialog(LowStorageEvent.event)
                }
                wasLowStorageAlertShowed = true
            }
        } else wasLowStorageAlertShowed = false
    }

    private fun getAvailableStoragePercent(information: List<Double>): Double {
        return (information[FREE_STORAGE_POSITION] * TOTAL_PERCENTAGE) / information[TOTAL_STORAGE_POSITION]
    }

    private fun setColorInStorageLevel(availablePercent: Double) {
        binding.progressStorageLevel.setProgress(availablePercent.toInt())
        binding.imageViewStorage.apply {
            if (availablePercent.toInt() in storageBarRanges.lowRange) {
                backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red)
            } else {
                backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.greenSuccess)
                clearAnimation()
            }
        }
    }

    private fun setTextStorageLevel(availablePercent: Double) {
        val availablePercentage = if (availablePercent > 99.6) 100 else availablePercent.toInt()
        val usedStorageString = getString(R.string.storage_level_x2, availablePercentage)

        val textToStorage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(usedStorageString, 0)
        } else usedStorageString

        binding.textViewStorageLevels.text = textToStorage
    }

    override fun setBatteryLevel(result: Event<Result<Int>>) {
        result.getContentIfNotHandled()?.run {
            doIfSuccess {
                manageBatteryLevel(it)
            }
            doIfError {
                showBatteryLevelNotAvailable()
            }
            sharedViewModel.getStorageLevels()
        }
    }

    override fun manageBatteryLevel(batteryPercent: Int) {
        activity?.runOnUiThread {
            if (batteryPercent >= 0) {
                progressBarBattery.setProgress(batteryPercent)
                setColorInBattery(batteryPercent)
                setTextInProgressBattery(batteryPercent)
                currentPercentInBattery = batteryPercent
            } else showBatteryLevelNotAvailable()
        }
    }

    override fun setTextInProgressBattery(batteryPercent: Int) {
        if (wasNotificationArriveForLowBattery) {
            setJustTimeInBatteryText(batteryPercent)
            currentMinutesAfterNotifications = getMinutesLeftAfterNotification(batteryPercent)
            return
        }
        setJustPercentInBatteryText(batteryPercent)
    }

    private fun setJustPercentInBatteryText(batteryPercent: Int) {
        val textBatteryPercent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(getString(R.string.battery_percent_x2_just_percent, batteryPercent), 0)
        else getString(R.string.battery_percent_x2_just_percent, batteryPercent)
        textViewBattery.text = textBatteryPercent
    }

    private fun setJustTimeInBatteryText(batteryPercent: Int) {
        val textBatteryPercent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(getStringMinutesBatteryLevel(batteryPercent), 0)
        else getStringMinutesBatteryLevel(batteryPercent)
        textViewBattery.text = textBatteryPercent
    }

    private fun getStringMinutesBatteryLevel(batteryPercent: Int) = getString(
        R.string.battery_percent_x2_just_minutes,
        getMinutesLeftAfterNotification(batteryPercent).toString()
    )

    private fun getMinutesLeftAfterNotification(batteryPercent: Int): Int {
        return if (currentPercentInBattery == 0) 0
        else ((batteryPercent * currentMinutesAfterNotifications) / currentPercentInBattery)
    }

    private fun getPercentLeftAfterNotification(minutes: Int): Int {
        return if (currentMinutesAfterNotifications == 0) 0
        else ((minutes * currentPercentInBattery) / currentMinutesAfterNotifications)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        val TAG = LiveStatusBarX2Fragment::class.java.simpleName
        private var wasNotificationArriveForLowBattery = false
        private var currentMinutesAfterNotifications = 0
        private var currentPercentInBattery = 100
        const val LOW_STORAGE_LEVEL = 5
    }
}
