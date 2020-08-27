package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4::class)
class AppNavigationTest : EspressoBaseTest<LoginActivity>(LoginActivity::class.java) {

    companion object {
        val liveViewScreen = LiveViewScreen()
        val fileListScreen = FileListScreen()
    }

    @Before
    fun login() {
        LoginScreen().login()
    }

    @Test
    fun b_verifySwitchBetweenSnapshotsAndVideosList_FMA_564() {
        with(fileListScreen) {
            liveViewScreen.openSnapshotList()
            clickOnSimpleListButton()
            isFileListDisplayed()
            goBack()
            liveViewScreen.openVideoList()
            isFileListDisplayed()
        }
    }

    @Test
    fun c_verifyLiveViewToggleBehaviorAfterNavigation_FMA_214() {
        with(liveViewScreen) {
            isLiveViewDisplayed()
            isLiveViewToggleEnabled()

            switchLiveViewToggle()
            isLiveViewToggleDisabled()

            openSnapshotList()
            fileListScreen.goBack()

            isLiveViewDisplayed()
            isLiveViewToggleDisabled()

            switchLiveViewToggle()
            isLiveViewToggleEnabled()
        }
    }

    @Test
    fun a_verifyRecordingAfterNavigation_FMA_549() {
        with(liveViewScreen) {
            isLiveViewDisplayed()

            startRecording()
            isRecordingInProgress()

            openSnapshotList()
            fileListScreen.goBack()

            isRecordingInProgress()

            openVideoList()
            fileListScreen.goBack()

            isRecordingInProgress()

            stopRecording()
            isRecordingNotInProgress()
        }
    }
}
