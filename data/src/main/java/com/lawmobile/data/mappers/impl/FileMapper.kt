package com.lawmobile.data.mappers.impl

import com.lawmobile.data.mappers.CameraMapper
import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.DomainCameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile

object FileMapper :
    DomainMapper<CameraFile, DomainCameraFile>,
    CameraMapper<DomainCameraFile, CameraFile> {
    override fun CameraFile.toDomain() = DomainCameraFile(date, name, nameFolder, path)
    override fun DomainCameraFile.toCamera() = CameraFile(name, date, path, nameFolder)
    fun List<DomainCameraFile>.toCameraList(): List<CameraFile> = map { it.toCamera() }
}
