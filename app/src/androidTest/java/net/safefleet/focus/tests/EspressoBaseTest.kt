package net.safefleet.focus.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.Suppress
import androidx.test.rule.GrantPermissionRule
import net.safefleet.focus.di.mocksServiceCameras.CameraConnectServiceMock
import net.safefleet.focus.helpers.MockUtils
import org.junit.After
import org.junit.Rule
import org.junit.runner.RunWith

@LargeTest
@Suppress
@RunWith(AndroidJUnit4::class)
open class EspressoBaseTest {

    companion object {
        val mockUtils = MockUtils()
    }

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION")

    @After
    fun restoreData() {
        mockUtils.restoreCameraConnection()
        mockUtils.restoreSnapshotsOnBodyCamera()
        mockUtils.restoreVideosOnBodyCamera()
        mockUtils.turnWifiOn()
        CameraConnectServiceMock.takenPhotos = 0
        CameraConnectServiceMock.takenVideos = 0
    }
}
