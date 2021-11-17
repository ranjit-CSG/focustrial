package com.lawmobile.domain.entities.customEvents

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType

object BluetoothErrorEvent {

    const val value = "bluetooth_issues"
    const val title = "The mobile Bluetooth is off"
    const val message = "Please turn on the Bluetooth."

    val event = CameraEvent(
        name = value,
        eventTag = EventTag.BLUETOOTH,
        eventType = EventType.NOTIFICATION
    )
}