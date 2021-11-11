package com.safefleet.lawmobile.tests.x2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.tests.EspressoBaseTest
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CameraStatusTest : EspressoBaseTest() {
    private val liveViewScreen = LiveViewScreen()

    @get:Rule
    var baristaRule = BaristaRule.create(LoginActivity::class.java)

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        baristaRule.launchActivity()
        LoginScreen().loginWithoutSSO()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1780
     */
    @Test
    fun verifyBatteryIndicator() {
        mockUtils.setBatteryProgressCamera(100)
        liveViewScreen.refreshCameraStatus()
        liveViewScreen.isBatteryIndicatorTextDisplayed("100")
        liveViewScreen.isBatteryStatusDisplayed()

        mockUtils.setBatteryProgressCamera(34)
        liveViewScreen.refreshCameraStatus()
        liveViewScreen.isBatteryIndicatorTextDisplayed("34")
        liveViewScreen.isBatteryStatusDisplayed()

        mockUtils.setBatteryProgressCamera(5)
        liveViewScreen.refreshCameraStatus()
        liveViewScreen.isBatteryIndicatorTextDisplayed("5")
        liveViewScreen.isBatteryStatusDisplayed()

        mockUtils.setBatteryProgressCamera(0)
        liveViewScreen.refreshCameraStatus()
        liveViewScreen.isBatteryIndicatorTextDisplayed("0")
        liveViewScreen.isBatteryStatusDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1984
     */
    @Test
    fun verifyStorageIndicator() {
        mockUtils.setStorageProgressCamera(60000000, 60000000)
        liveViewScreen.refreshCameraStatus()
        liveViewScreen.isMemoryStorageIndicatorTextDisplayed("100")
        liveViewScreen.isMemoryStorageStatusDisplayed()

        mockUtils.setStorageProgressCamera(60000000, 10000000)
        liveViewScreen.refreshCameraStatus()
        liveViewScreen.isMemoryStorageIndicatorTextDisplayed("16")
        liveViewScreen.isMemoryStorageStatusDisplayed()

        mockUtils.setStorageProgressCamera(60000000, 0)
        liveViewScreen.refreshCameraStatus()
        liveViewScreen.isMemoryStorageIndicatorTextDisplayed("0")
        liveViewScreen.isMemoryStorageStatusDisplayed()
    }
}
