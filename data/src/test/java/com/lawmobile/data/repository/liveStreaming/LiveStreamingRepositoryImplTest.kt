package com.lawmobile.data.repository.liveStreaming

import com.lawmobile.data.datasource.remote.liveStreaming.LiveStreamingRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LiveStreamingRepositoryImplTest {

    private val liveStreamingRemoteDataSource: LiveStreamingRemoteDataSource = mockk()

    private val liveStreamingRepositoryImpl: LiveStreamingRepositoryImpl by lazy {
        LiveStreamingRepositoryImpl(liveStreamingRemoteDataSource)
    }

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun testGetUrlForLiveStreamVerifyFlow() {
        every { liveStreamingRemoteDataSource.getUrlForLiveStream() } returns "xyz"
        liveStreamingRepositoryImpl.getUrlForLiveStream()
        verify { liveStreamingRemoteDataSource.getUrlForLiveStream() }
    }

    @Test
    fun testGetUrlForLiveStreamVerifyValue() {
        every { liveStreamingRemoteDataSource.getUrlForLiveStream() } returns "xyz"
        val url = liveStreamingRepositoryImpl.getUrlForLiveStream()
        Assert.assertEquals("xyz", url)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyValueEmpty() {
        every { liveStreamingRemoteDataSource.getUrlForLiveStream() } returns ""
        val url = liveStreamingRemoteDataSource.getUrlForLiveStream()
        Assert.assertEquals(url, "")
    }

    @Test
    fun testStartRecordVideoFlow() {
        coEvery { liveStreamingRemoteDataSource.startRecordVideo() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRepositoryImpl.startRecordVideo()
        }
        coVerify { liveStreamingRemoteDataSource.startRecordVideo() }
    }

    @Test
    fun testStartRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingRemoteDataSource.startRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRepositoryImpl.startRecordVideo(), result)
        }
    }

    @Test
    fun testStartRecordVideoFailed() {
        val result = Result.Error(Exception("Message"))
        coEvery { liveStreamingRemoteDataSource.startRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRepositoryImpl.startRecordVideo(), result)
        }
    }

    @Test
    fun testStopRecordVideoFlow() {
        coEvery { liveStreamingRemoteDataSource.stopRecordVideo() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRepositoryImpl.stopRecordVideo()
        }
        coVerify { liveStreamingRemoteDataSource.stopRecordVideo() }
    }

    @Test
    fun testStopRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingRemoteDataSource.stopRecordVideo() } returns result
        FileList.listOfVideos = listOf(mockk(relaxed = true))
        runBlocking {
            Assert.assertEquals(liveStreamingRepositoryImpl.stopRecordVideo(), result)
            Assert.assertTrue(FileList.listOfVideos.isEmpty())
        }
    }

    @Test
    fun testStopRecordVideoFailed() {
        val result = Result.Error(Exception("Message"))
        coEvery { liveStreamingRemoteDataSource.stopRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRepositoryImpl.stopRecordVideo(), result)
        }
    }

    @Test
    fun testTakePhotoFlow() {
        coEvery { liveStreamingRemoteDataSource.takePhoto() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRepositoryImpl.takePhoto()
        }

        coEvery { liveStreamingRemoteDataSource.takePhoto() }
    }

    @Test
    fun testTakePhotoSuccess() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingRemoteDataSource.takePhoto() } returns result
        FileList.listOfImages = listOf(mockk(relaxed = true))
        runBlocking {
            Assert.assertEquals(liveStreamingRepositoryImpl.takePhoto(), result)
            Assert.assertTrue(FileList.listOfImages.isEmpty())
        }
    }

    @Test
    fun testTakePhotoFailed() {
        val result = Result.Error(Exception(""))
        coEvery { liveStreamingRemoteDataSource.takePhoto() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRepositoryImpl.takePhoto(), result)
        }
    }

    @Test
    fun testGetCatalogInfoSuccess() {
        coEvery { liveStreamingRemoteDataSource.getCatalogInfo() } returns Result.Success(
            mockk()
        )
        runBlocking {
            val result = liveStreamingRepositoryImpl.getCatalogInfo()
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { liveStreamingRemoteDataSource.getCatalogInfo() }
    }

    @Test
    fun testGetCatalogInfoError() {
        coEvery { liveStreamingRemoteDataSource.getCatalogInfo() } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result = liveStreamingRepositoryImpl.getCatalogInfo()
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { liveStreamingRemoteDataSource.getCatalogInfo() }
    }

    @Test
    fun testGetBatteryLevel(){
        val result = Result.Success(10)
        coEvery { liveStreamingRemoteDataSource.getBatteryLevel() } returns result
        runBlocking {
            val response = liveStreamingRepositoryImpl.getBatteryLevel()
            Assert.assertEquals(response, result)
        }
        coVerify { liveStreamingRemoteDataSource.getBatteryLevel() }
    }

    @Test
    fun testGetFreeStorage(){
        val result = Result.Success("10000")
        coEvery { liveStreamingRemoteDataSource.getFreeStorage() } returns result
        runBlocking {
            val response = liveStreamingRepositoryImpl.getFreeStorage()
            Assert.assertEquals(response,result)
        }
        coVerify { liveStreamingRemoteDataSource.getFreeStorage() }
    }

    @Test
    fun testGetTotalStorage(){
        val result = Result.Success("10000")
        coEvery { liveStreamingRemoteDataSource.getTotalStorage() } returns result
        runBlocking {
            val response = liveStreamingRepositoryImpl.getTotalStorage()
            Assert.assertEquals(response,result)
        }
        coVerify { liveStreamingRemoteDataSource.getTotalStorage() }
    }

}