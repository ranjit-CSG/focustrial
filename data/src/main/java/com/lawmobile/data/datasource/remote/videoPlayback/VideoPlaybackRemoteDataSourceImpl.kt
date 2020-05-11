package com.lawmobile.data.datasource.remote.videoPlayback


import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoInfo
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result

class VideoPlaybackRemoteDataSourceImpl(private val cameraConnectService: CameraConnectService) :
    VideoPlaybackRemoteDataSource {
    override suspend fun getInformationResourcesVideo(cameraConnectFile: CameraConnectFile): Result<CameraConnectVideoInfo> {
        return cameraConnectService.getInformationResourcesVideo(cameraConnectFile)
    }
}