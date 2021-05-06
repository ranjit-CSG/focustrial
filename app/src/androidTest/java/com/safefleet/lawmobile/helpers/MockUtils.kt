package com.safefleet.lawmobile.helpers

import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.utils.CameraHelper
import com.safefleet.lawmobile.di.mocksServiceCameras.CameraConnectServiceX1Mock
import com.safefleet.lawmobile.testData.CameraFilesData
import com.safefleet.lawmobile.testData.TestLoginData
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject

class MockUtils {

    companion object {
        var wifiEnabled = true
        var cameraSSID = TestLoginData.SSID_X1.value
        var bodyWornDiagnosisResult: Result<Boolean> = Result.Success(true)
        var progressBatteryCamera: Result<Int> = Result.Success(90)
        var cameraConnectServiceX1Mock = CameraConnectServiceX1Mock()
    }

    fun disconnectCamera() {
        val cameraHelperMock: CameraHelper = mockk {
            every { checkWithAlertIfTheCameraIsConnected() } returns false
        }

        // Assign CameraHelper Mock to CameraHelper object in runtime
        mockkObject(CameraHelper)
        every { CameraHelper.getInstance() } returns cameraHelperMock

        CameraConnectServiceX1Mock.result = Result.Error(Exception())
    }

    fun restoreCameraConnection() {
        unmockkObject(CameraHelper)
        CameraConnectServiceX1Mock.result = Result.Success(100)
    }

    fun clearSnapshotsOnX1() {
        CameraConnectServiceX1Mock.snapshotsList = FileResponseWithErrors()
        CameraConnectServiceX1Mock.takenPhotos = 0
    }

    fun restoreSnapshotsOnX1() {
        CameraConnectServiceX1Mock.snapshotsList =
            CameraFilesData.DEFAULT_SNAPSHOT_LIST.value
    }

    fun clearVideosOnX1() {
        CameraConnectServiceX1Mock.videoList = FileResponseWithErrors()
        CameraConnectServiceX1Mock.takenVideos = 0
    }

    fun restoreVideosOnX1() {
        CameraConnectServiceX1Mock.videoList =
            CameraFilesData.DEFAULT_VIDEO_LIST.value
    }

    fun turnWifiOff() {
//      This function only works when used before launching an app Activity
        wifiEnabled = false
    }

    fun turnWifiOn() {
        wifiEnabled = true
    }

    fun setCameraType(cameraType: CameraType) {
        if (cameraType == CameraType.X1) {
            cameraSSID = TestLoginData.SSID_X1.value
        } else {
            cameraSSID = TestLoginData.SSID_X2.value
        }
    }

    fun setBodyWornDiagnosisResult(result: Result<Boolean>) {
        bodyWornDiagnosisResult = result
    }

    fun setBatteryProgressCamera(progress: Int) {
        progressBatteryCamera = Result.Success(progress)
    }
}
