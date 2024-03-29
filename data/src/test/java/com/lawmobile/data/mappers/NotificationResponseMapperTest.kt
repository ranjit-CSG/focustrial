package com.lawmobile.data.mappers

import com.lawmobile.body_cameras.entities.NotificationResponse
import com.lawmobile.data.mappers.impl.NotificationResponseMapper.toDomain
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.utils.DateHelper
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Assert
import org.junit.jupiter.api.Test

class NotificationResponseMapperTest {

    @Test
    fun testCameraToDomainNotification() {
        mockkObject(DateHelper)
        every { DateHelper.getCurrentDate() } returns "07/22/2020"
        val cameraNotification = NotificationResponse("7", "low_storage_warning", "value")
        val response = cameraNotification.toDomain()
        Assert.assertEquals(
            response,
            CameraEvent(
                name = "low_storage_warning",
                eventType = EventType.NOTIFICATION,
                eventTag = EventTag.INFORMATION,
                date = "07/22/2020",
                value = "value",
                isRead = true
            )
        )
    }

    @Test
    fun testCameraToDomainCameraEvent() {
        mockkObject(DateHelper)
        every { DateHelper.getCurrentDate() } returns "07/22/2020"
        val cameraNotification = NotificationResponse("7", "new_event", "value")
        val response = cameraNotification.toDomain()
        Assert.assertEquals(
            response,
            CameraEvent(
                name = "new_event",
                eventType = EventType.CAMERA,
                eventTag = EventTag.INFORMATION,
                date = "07/22/2020",
                value = "value",
                isRead = true
            )
        )
    }
}
