package com.safefleet.lawmobile.screens

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.safefleet.lawmobile.helpers.isActivated
import com.safefleet.lawmobile.helpers.isNotActivated
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn

class LiveViewScreen : BaseScreen() {

    fun switchLiveViewToggle() = clickOn(R.id.buttonSwitchLiveView)

    fun switchFullScreenMode() = waitUntil { clickOn(R.id.toggleFullScreenLiveView) }

    fun openSnapshotList() = clickOn(R.id.buttonSnapshotList)

    fun openVideoList() = clickOn(R.id.buttonVideoList)

    fun openHelpPage() = clickOn(R.id.buttonOpenHelpPage)

    fun takeSnapshot() {
        clickOn(R.id.buttonSnapshot)
        assertDisplayed(R.string.live_view_take_photo_success)
        waitUntil { assertNotExist(R.string.live_view_take_photo_success) }
    }

    fun startRecording() {
        isRecordingNotInProgress()
        clickOn(R.id.buttonRecord)
    }

    fun stopRecording() {
        isRecordingInProgress()
        clickOn(R.id.buttonRecord)
        waitUntil { isRecordingNotInProgress() }
    }

    fun isLiveViewNotDisplayed() {
        assertNotExist(R.id.buttonSwitchLiveView)
        assertNotExist(R.id.liveStreamingView)
        assertNotExist(R.id.buttonRecord)
        assertNotExist(R.id.buttonSnapshot)
        assertNotExist(R.id.buttonOpenHelpPage)
    }

    fun isLiveViewDisplayed() {
        assertDisplayed(R.id.liveStreamingView)
        assertDisplayed(R.id.toggleFullScreenLiveView)

        assertDisplayed(R.id.imageViewBattery)
        assertDisplayed(R.id.textViewBatteryPercent)
        assertDisplayed(R.id.progressBatteryLevel)

        assertDisplayed(R.id.imageViewStorage)
        assertDisplayed(R.id.textViewStorageLevels)
        assertDisplayed(R.id.progressStorageLevel)

        assertDisplayed(R.id.buttonSnapshot)
        assertDisplayed(R.string.take_snapshots)

        assertDisplayed(R.id.buttonRecord)
        assertDisplayed(R.string.record_video)

        assertDisplayed(R.id.buttonOpenHelpPage)

        assertDisplayed(R.id.textLiveViewSwitch, R.string.live_view_label)

        assertDisplayed(R.id.buttonSnapshotList)
        assertDisplayed(R.string.view_snapshots)

        assertDisplayed(R.id.buttonVideoList)
        assertDisplayed(R.string.view_videos)
    }

    fun isVideoInFullScreen() {
        assertNotDisplayed(R.id.buttonSwitchLiveView)
        assertNotDisplayed(R.id.buttonRecord)
        assertNotDisplayed(R.id.buttonSnapshot)

        assertDisplayed(R.id.liveStreamingView)
        assertDisplayed(R.id.toggleFullScreenLiveView)
    }

    fun isLiveViewToggleDisabled() {
        onView(withId(R.id.buttonSwitchLiveView)).check(matches(isNotActivated()))
    }

    fun isLiveViewToggleEnabled() {
        onView(withId(R.id.buttonSwitchLiveView)).check(matches(isActivated()))
    }

    fun isRecordingNotInProgress() {
        onView(withId(R.id.buttonRecord)).check(matches(isNotActivated()))
        assertNotDisplayed(R.id.textLiveViewRecording)
    }

    fun isRecordingInProgress() {
        onView(withId(R.id.buttonRecord)).check(matches(isActivated()))
        assertDisplayed(R.id.textLiveViewRecording)
    }

    fun isTextBatteryIndicatorContained(text: String) {
        waitUntil { assertContains(R.id.textViewBatteryPercent, text) }
    }
}
