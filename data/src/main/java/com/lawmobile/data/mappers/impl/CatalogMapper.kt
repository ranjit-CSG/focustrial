package com.lawmobile.data.mappers.impl

import com.lawmobile.data.mappers.CameraMapper
import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.MetadataEvent
import com.safefleet.mobile.external_hardware.cameras.entities.CameraCatalog

object CatalogMapper :
    DomainMapper<CameraCatalog, MetadataEvent>,
    CameraMapper<MetadataEvent, CameraCatalog> {
    override fun CameraCatalog.toDomain(): MetadataEvent = MetadataEvent(id, name, type)
    override fun MetadataEvent.toCamera(): CameraCatalog = CameraCatalog(id, name, type)
    fun List<CameraCatalog>.toDomainList() = map { it.toDomain() }
}
