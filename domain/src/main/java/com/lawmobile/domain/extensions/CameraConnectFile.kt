package com.lawmobile.domain.extensions

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile

fun CameraConnectFile.getCreationDate(): String {

    val year = "20" + nameFolder.substring(0, 2)
    val month = nameFolder.substring(2, 4)
    val day = nameFolder.substring(4, 6)

    val date = "$year-$month-$day"
    val time = name.substring(0, 6)
    val hour = time.substring(0, 2)
    val min = time.substring(2, 4)
    val sec = time.substring(4, 6)

    return String.format("%s %s:%s:%s", date, hour, min, sec)
}