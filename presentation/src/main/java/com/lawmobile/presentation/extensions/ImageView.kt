package com.lawmobile.presentation.extensions

import android.widget.ImageView
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.presentation.R

fun ImageView.setImageDependingOnEventTag(eventTag: EventTag) {
    when (eventTag) {
        EventTag.ERROR -> {
            setImageResource(R.drawable.ic_error_icon)
            tag = R.drawable.ic_error_icon
        }
        EventTag.WARNING -> {
            setImageResource(R.drawable.ic_warning_icon)
            tag = R.drawable.ic_warning_icon
        }
        EventTag.NOTIFICATION, EventTag.INFORMATION -> {
            setImageResource(R.drawable.ic_info_icon)
            tag = R.drawable.ic_info_icon
        }
        EventTag.INTERNET -> {
            setImageResource(R.drawable.ic_no_internet)
            tag = R.drawable.ic_no_internet
        }
        EventTag.BLUETOOTH -> {
            setImageResource(R.drawable.ic_bluetooth_error)
            tag = R.drawable.ic_bluetooth_error
        }
    }
}
