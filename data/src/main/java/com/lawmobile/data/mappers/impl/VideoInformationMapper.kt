package com.lawmobile.data.mappers.impl

import com.lawmobile.body_cameras.entities.VideoFileInfo
import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.DomainInformationVideo

object VideoInformationMapper : DomainMapper<VideoFileInfo, DomainInformationVideo> {
    override fun VideoFileInfo.toDomain(): DomainInformationVideo =
        DomainInformationVideo(
            size,
            date,
            duration,
            urlVideo
        )
}
