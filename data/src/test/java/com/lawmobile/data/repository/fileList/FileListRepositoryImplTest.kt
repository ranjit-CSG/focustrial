package com.lawmobile.data.repository.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.lawmobile.data.entities.RemoteVideoMetadata
import com.lawmobile.data.entities.VideoListMetadata
import com.lawmobile.domain.entities.DomainInformationFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.avml.cameras.entities.VideoMetadata
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class FileListRepositoryImplTest {

    private val fileListRemoteDataSource: FileListRemoteDataSource = mockk()

    private val fileListRepositoryImpl: FileListRepositoryImpl by lazy {
        FileListRepositoryImpl(fileListRemoteDataSource)
    }

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun testGetSnapshotListFlow() {
        coEvery { fileListRemoteDataSource.getSnapshotList() } returns Result.Success(listOf(mockk()))
        runBlocking {
            fileListRepositoryImpl.getSnapshotList()
        }
        coVerify { fileListRemoteDataSource.getSnapshotList() }
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        coEvery { fileListRemoteDataSource.getSnapshotList() } returns Result.Success(
            listOf(
                cameraConnectFile
            )
        )
        FileList.listOfImages = emptyList()
        val response = Result.Success(listOf(DomainInformationFile(cameraConnectFile, null)))
        runBlocking {
            Assert.assertEquals(fileListRepositoryImpl.getSnapshotList(), response)
        }
    }


    @Test
    fun testGetSnapshotListSuccessWithLessValuesInDataSource() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        coEvery { fileListRemoteDataSource.getSnapshotList() } returns Result.Success(
            listOf(
                cameraConnectFile
            )
        )
        FileList.listOfImages = listOf(
            DomainInformationFile(cameraConnectFile, null),
            DomainInformationFile(cameraConnectFile, null)
        )
        runBlocking {
            Assert.assertEquals(
                fileListRepositoryImpl.getSnapshotList(),
                Result.Success(FileList.listOfImages)
            )
        }
    }

    @Test
    fun testGetSnapshotListSuccessWithEqualsValuesInDataSource() {
        mockkObject(FileList)
        every { FileList.changeListOfImages(any()) } just Runs

        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        coEvery { fileListRemoteDataSource.getSnapshotList() } returns Result.Success(
            listOf(
                cameraConnectFile,
                cameraConnectFile
            )
        )
        val listImages = listOf(
            DomainInformationFile(cameraConnectFile, null),
            DomainInformationFile(cameraConnectFile, null)
        )
        FileList.listOfImages = listImages
        runBlocking {
            val response = fileListRepositoryImpl.getSnapshotList()
            Assert.assertEquals(response, Result.Success(FileList.listOfImages))
        }

        coVerify { FileList.changeListOfImages(listImages) }
    }

    @Test
    fun testGetSnapshotListFailed() {
        val result = Result.Error(mockk())
        coEvery { fileListRemoteDataSource.getSnapshotList() } returns result
        runBlocking {
            Assert.assertEquals(fileListRepositoryImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetVideoListFlow() {
        coEvery { fileListRemoteDataSource.getVideoList() } returns Result.Success(
            listOf(mockk(relaxed = true))
        )
        runBlocking {
            fileListRepositoryImpl.getVideoList()
        }
        coVerify { fileListRemoteDataSource.getVideoList() }
    }

    @Test
    fun testGetVideoListSuccess() {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        coEvery { fileListRemoteDataSource.getVideoList() } returns Result.Success(
            listOf(cameraConnectFile)
        )
        FileList.listOfVideos = emptyList()
        val response = Result.Success(listOf(DomainInformationFile(cameraConnectFile, null)))
        runBlocking {
            Assert.assertEquals(fileListRepositoryImpl.getVideoList(), response)
        }
    }

    @Test
    fun testGetVideoListSuccessLessInResponseDataSource() {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        coEvery { fileListRemoteDataSource.getVideoList() } returns Result.Success(
            listOf(cameraConnectFile)
        )
        FileList.listOfVideos = listOf(
            DomainInformationFile(cameraConnectFile, null),
            DomainInformationFile(cameraConnectFile, null)
        )
        runBlocking {
            Assert.assertEquals(
                fileListRepositoryImpl.getVideoList(),
                Result.Success(FileList.listOfVideos)
            )
        }
    }

    @Test
    fun testGetVideoListSuccessEqualsInResponseDataSource() {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        mockkObject(FileList)
        every { FileList.changeListOfVideos(any()) } just Runs
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        coEvery { fileListRemoteDataSource.getVideoList() } returns Result.Success(
            listOf(cameraConnectFile, cameraConnectFile)
        )
        val listOfVideos = listOf(
            DomainInformationFile(cameraConnectFile, null),
            DomainInformationFile(cameraConnectFile, null)
        )
        FileList.listOfVideos = listOfVideos
        runBlocking {
            Assert.assertEquals(
                fileListRepositoryImpl.getVideoList(),
                Result.Success(FileList.listOfVideos)
            )
        }

        coVerify { FileList.changeListOfVideos(listOfVideos) }
    }

    @Test
    fun testGetVideoListFailed() {
        val result = Result.Error(mockk())
        coEvery { fileListRemoteDataSource.getVideoList() } returns result
        runBlocking {
            Assert.assertEquals(fileListRepositoryImpl.getVideoList(), result)
        }
    }

    @Test
    fun testSavePartnerIdVideosFlow() {
        val cameraConnectFile = CameraConnectFile("", "", "", "")
        val remoteVideoMetadata = RemoteVideoMetadata(CameraConnectVideoMetadata(""), false)
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns remoteVideoMetadata
        every { VideoListMetadata.saveOrUpdateVideoMetadata(any()) } returns Unit
        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns Result.Success(Unit)

        runBlocking {
            fileListRepositoryImpl.savePartnerIdVideos(listOf(cameraConnectFile), "")
        }

        coVerify { fileListRemoteDataSource.savePartnerIdVideos(any()) }
    }

    @Test
    fun testSavePartnerIdVideosSuccess() {
        val result = Result.Success(Unit)
        val cameraConnectFile = CameraConnectFile("", "", "", "1")
        val remoteVideoMetadata = RemoteVideoMetadata(
            CameraConnectVideoMetadata("", "", "", "2", "", VideoMetadata(partnerID = "abc")),
            false
        )

        VideoListMetadata.metadataList = mutableListOf(remoteVideoMetadata)
        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns result

        runBlocking {
            Assert.assertEquals(
                fileListRepositoryImpl.savePartnerIdVideos(
                    listOf(cameraConnectFile),
                    "1234"
                ), result
            )
            Assert.assertEquals(1, VideoListMetadata.metadataList.size)
        }

        val resultMetadata = CameraConnectVideoMetadata(
            "",
            "",
            "",
            "1",
            "",
            metadata = VideoMetadata(partnerID = "1234")
        )

        coVerify {
            delay(100)
            VideoListMetadata.getIndexVideoMetadata(any())
            fileListRemoteDataSource.savePartnerIdVideos(resultMetadata)
        }
    }

    @Test
    fun testSavePartnerIdVideosSuccessNull() {
        val result = Result.Success(Unit)
        val cameraConnectFile = CameraConnectFile("", "", "", "")
        VideoListMetadata.metadataList = mutableListOf()

        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns result

        runBlocking {
            Assert.assertEquals(
                fileListRepositoryImpl.savePartnerIdVideos(
                    listOf(cameraConnectFile),
                    "1234"
                ), result
            )
            Assert.assertEquals(1, VideoListMetadata.metadataList.size)
        }

        val resultMetadata = CameraConnectVideoMetadata(
            "",
            "",
            "",
            "",
            "",
            metadata = VideoMetadata(partnerID = "1234")
        )

        coVerify { fileListRemoteDataSource.savePartnerIdVideos(resultMetadata) }

    }

    @Test
    fun testSavePartnerIdVideosFailed() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)

        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns Result.Error(mockk())

        runBlocking {
            Assert.assertTrue(
                fileListRepositoryImpl.savePartnerIdVideos(
                    listOf(cameraConnectFile),
                    ""
                ) is Result.Error
            )
        }
    }

    @Test
    fun testSavePartnerIdSnapshotFlow() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)

        coEvery { fileListRemoteDataSource.savePartnerIdSnapshot(any()) } returns Result.Success(
            Unit
        )
        runBlocking {
            fileListRepositoryImpl.savePartnerIdSnapshot(listOf(cameraConnectFile), "")
        }
        coVerify { fileListRemoteDataSource.savePartnerIdSnapshot(any()) }
    }

    @Test
    fun testSavePartnerIdSnapshotSuccess() {
        val result = Result.Success(Unit)
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)

        coEvery { fileListRemoteDataSource.savePartnerIdSnapshot(any()) } returns result

        runBlocking {
            Assert.assertEquals(
                fileListRepositoryImpl.savePartnerIdSnapshot(
                    listOf(
                        cameraConnectFile
                    ), ""
                ), result
            )
        }

        coVerify {
            delay(100)
            fileListRemoteDataSource.savePartnerIdSnapshot(any())
        }
    }

    @Test
    fun testSavePartnerIdSnapshotFailed() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)

        coEvery { fileListRemoteDataSource.savePartnerIdSnapshot(any()) } returns Result.Error(mockk())

        runBlocking {
            Assert.assertTrue(
                fileListRepositoryImpl.savePartnerIdSnapshot(
                    listOf(cameraConnectFile),
                    ""
                ) is Result.Error
            )
        }
    }
}