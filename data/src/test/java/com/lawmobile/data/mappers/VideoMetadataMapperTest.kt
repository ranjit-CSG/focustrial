package com.lawmobile.data.mappers

import com.lawmobile.body_cameras.entities.AssociatedFile
import com.lawmobile.body_cameras.entities.VideoInformation
import com.lawmobile.data.mappers.impl.VideoMetadataMapper.toCamera
import com.lawmobile.data.mappers.impl.VideoMetadataMapper.toDomain
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainAssociatedFile
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.enums.CameraType
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class VideoMetadataMapperTest {

    @Test
    fun cameraToDomain() {
        val videoInformation: VideoInformation = mockk(relaxed = true) {
            every { associatedFiles } returns listOf(
                AssociatedFile("3", ""),
                AssociatedFile("1", ""),
                AssociatedFile("5", ""),
            )
        }
        val domainVideoMetadata = videoInformation.toDomain()
        with(videoInformation) {
            domainVideoMetadata.let {
                assertTrue(it.fileName == fileName)
                assertTrue(it.metadata?.partnerID == metadata?.partnerID)
                assertTrue(it.nameFolder == nameFolder)
                assertTrue(it.officerId == officerId)
                assertTrue(it.path == path)
                assertTrue(it.serialNumber == x1sn)
                it.associatedFiles?.forEachIndexed { index, photo ->
                    assertTrue(photo.name == associatedFiles?.get(index)?.name)
                    assertTrue(photo.date == associatedFiles?.get(index)?.date)
                }
            }
        }
    }

    @Test
    fun domainToCameraX2() {
        mockkObject(CameraInfo)
        CameraInfo.cameraType = CameraType.X2
        val domainVideoMetadata: DomainVideoMetadata = mockk(relaxed = true) {
            every { associatedFiles } returns listOf(
                DomainAssociatedFile("", "3"),
                DomainAssociatedFile("", "1"),
                DomainAssociatedFile("", "5"),
            )
        }
        val cameraConnectVideoMetadata = domainVideoMetadata.toCamera()
        with(cameraConnectVideoMetadata) {
            domainVideoMetadata.let {
                assertTrue(it.fileName == fileName)
                assertTrue(it.metadata?.partnerID == metadata?.partnerID)
                assertTrue(it.nameFolder == nameFolder)
                assertTrue(it.officerId == officerId)
                assertTrue(it.path == path)
                assertTrue(it.serialNumber == x2sn)
                it.associatedFiles?.forEachIndexed { index, photo ->
                    assertTrue(photo.name == associatedFiles?.get(index)?.name)
                    assertTrue(photo.date == associatedFiles?.get(index)?.date)
                }
            }
        }
    }

    @Test
    fun domainToCameraX2x2snNotEmpty() {
        mockkObject(CameraInfo)
        CameraInfo.cameraType = CameraType.X2
        val domainVideoMetadata: DomainVideoMetadata = mockk(relaxed = true) {
            every { associatedFiles } returns listOf(
                DomainAssociatedFile("", "3"),
                DomainAssociatedFile("", "1"),
                DomainAssociatedFile("", "5"),
            )
            every { x2sn } returns "X2"
        }
        val cameraConnectVideoMetadata = domainVideoMetadata.toCamera()
        with(cameraConnectVideoMetadata) {
            domainVideoMetadata.let {
                assertTrue(it.fileName == fileName)
                assertTrue(it.metadata?.partnerID == metadata?.partnerID)
                assertTrue(it.nameFolder == nameFolder)
                assertTrue(it.officerId == officerId)
                assertTrue(it.path == path)
                assertTrue(it.x2sn == x2sn)
                it.associatedFiles?.forEachIndexed { index, photo ->
                    assertTrue(photo.name == associatedFiles?.get(index)?.name)
                    assertTrue(photo.date == associatedFiles?.get(index)?.date)
                }
            }
        }
    }
}
