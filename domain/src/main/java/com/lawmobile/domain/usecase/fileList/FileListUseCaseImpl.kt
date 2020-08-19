package com.lawmobile.domain.usecase.fileList

import com.lawmobile.domain.repository.fileList.FileListRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

class FileListUseCaseImpl(private val fileListRepository: FileListRepository) : FileListUseCase {
    override suspend fun savePartnerIdVideos(
        cameraConnectFileList: List<CameraConnectFile>,
        partnerID: String
    ): Result<Unit> = fileListRepository.savePartnerIdVideos(cameraConnectFileList, partnerID)

    override suspend fun savePartnerIdSnapshot(
        cameraConnectFileList: List<CameraConnectFile>,
        partnerID: String
    ): Result<Unit> = fileListRepository.savePartnerIdSnapshot(cameraConnectFileList, partnerID)
}