package com.lawmobile.presentation.utils

import com.lawmobile.domain.utils.ConnectionHelper
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CameraHelperTest {

    private val wifiHelper: WifiHelper = mockk()
    private val connectionHelper: ConnectionHelper = mockk()
    private val cameraHelper = CameraHelper(connectionHelper, wifiHelper)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun testSetInstance() {
        CameraHelper.setInstance(cameraHelper)
        Assert.assertEquals(CameraHelper.getInstance(), cameraHelper)
    }

    @Test
    fun testCheckWithAlertIfTheCameraIsConnectedSuccess() {
        every { wifiHelper.getGatewayAddress() } returns ""
        every { connectionHelper.isCameraConnected(any()) } returns true
        Assert.assertTrue(cameraHelper.checkWithAlertIfTheCameraIsConnected())
    }

    @Test
    fun testCheckWithAlertIfTheCameraIsConnectedFailed() {
        every { wifiHelper.getGatewayAddress() } returns ""
        every { connectionHelper.isCameraConnected(any()) } returns false
        Assert.assertFalse(cameraHelper.checkWithAlertIfTheCameraIsConnected())
    }

    @Test
    fun testDisconnectCameraFlow() {
        coEvery { connectionHelper.disconnectCamera() } returns Result.Success(Unit)
        runBlocking { cameraHelper.disconnectCamera() }
        coVerify { connectionHelper.disconnectCamera() }
    }
}
