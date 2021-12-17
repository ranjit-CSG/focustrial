package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.NotificationType
import com.lawmobile.domain.utils.DateHelper
import com.safefleet.mobile.external_hardware.cameras.entities.NotificationResponse

object NotificationResponseMapper {
    fun cameraToDomain(notificationResponse: NotificationResponse): CameraEvent {
        val eventName = notificationResponse.type.split(":").last()
        val eventType = NotificationType.getByValue(eventName).getTypeOfEvent()
        val eventTag = when (notificationResponse.type.split(":").first()) {
            EventTag.WARNING.value -> EventTag.WARNING
            EventTag.ERROR.value -> EventTag.ERROR
            else -> EventTag.INFORMATION
        }

        return CameraEvent(
            name = eventName,
            eventType = eventType,
            eventTag = eventTag,
            value = notificationResponse.param,
            date = DateHelper.getCurrentDate(),
            isRead = true
        )
    }
}
