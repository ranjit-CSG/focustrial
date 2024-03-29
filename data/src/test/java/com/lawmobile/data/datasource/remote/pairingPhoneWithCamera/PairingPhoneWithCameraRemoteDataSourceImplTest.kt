package com.lawmobile.data.datasource.remote.pairingPhoneWithCamera

import com.lawmobile.body_cameras.CameraService
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PairingPhoneWithCameraRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk(relaxed = true)
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }

    private val pairingPhoneWithCameraRemoteDataSourceImpl by lazy {
        PairingPhoneWithCameraRemoteDataSourceImpl(cameraServiceFactory)
    }

    @Test
    fun testLoadPairingCamera() {
        val progressPairingCamera: ((Result<Int>) -> Unit) = { }
        coEvery { cameraService.loadPairingCamera(any(), any()) } just Runs

        runBlocking {
            pairingPhoneWithCameraRemoteDataSourceImpl.loadPairingCamera(
                "",
                "",
                progressPairingCamera
            )
        }
        Assert.assertTrue(cameraService.progressPairingCamera != null)
        coVerify { cameraService.loadPairingCamera("", "") }
    }

    @Test
    fun testIsPossibleTheConnection() {
        coEvery { cameraService.isPossibleTheConnection(any()) } returns Result.Success(Unit)
        runBlocking { pairingPhoneWithCameraRemoteDataSourceImpl.isPossibleTheConnection("10.10.10.4") }
        coVerify { cameraService.isPossibleTheConnection("10.10.10.4") }
    }

    @Test
    fun testCleanCacheFiles() {
        coEvery { cameraService.cleanCacheFiles() } just Runs

        runBlocking {
            pairingPhoneWithCameraRemoteDataSourceImpl.cleanCacheFiles()
        }
        coVerify { cameraService.cleanCacheFiles() }
    }
}
