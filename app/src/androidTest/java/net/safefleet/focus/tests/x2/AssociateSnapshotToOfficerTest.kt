package net.safefleet.focus.tests.x2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.lawmobile.presentation.utils.FeatureSupportHelper
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import net.safefleet.focus.screens.FileListScreen
import net.safefleet.focus.screens.LiveViewScreen
import net.safefleet.focus.screens.LoginScreen
import net.safefleet.focus.screens.SnapshotDetailScreen
import net.safefleet.focus.tests.EspressoStartActivityBaseTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AssociateSnapshotToOfficerTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    private val snapshotDetailScreen = SnapshotDetailScreen()
    private val liveViewScreen = LiveViewScreen()
    private val fileListScreen = FileListScreen()
    private val loginScreen = LoginScreen()
    private val officer = "OfficerT"
    private val snapshotName = "11021800.JPG"

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        loginScreen.loginWithoutSSO()
        FeatureSupportHelper.supportAssociateOfficerID = true
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-2914
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun linkSnapshotToPoliceOfficer() {
        with(snapshotDetailScreen) {
            liveViewScreen.openSnapshotList()

            fileListScreen.clickOnItemInPosition(1)
            isPhotoNameDisplayed(snapshotName)

            clickAssociateOfficer()
            typeOfficerIdToAssociate(officer)
            clickOnAssociateOfficerPopUp()
            isOfficerAssociateSuccessDisplayed()
            isOfficerNameDisplayed(officer)

            clickOnBack()

            fileListScreen.clickOnItemInPosition(1)
            isPhotoNameDisplayed(snapshotName)
            isOfficerNameDisplayed(officer)
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-3197
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun linkSixSnapshotsToSamePoliceOfficer() {
        with(snapshotDetailScreen) {
            liveViewScreen.openSnapshotList()

            fileListScreen.clickOnSelectFilesToAssociate()

            for (item in 0..5) {
                fileListScreen.clickOnItemInPosition(item)
            }

            clickAssociateOfficer()
            typeOfficerIdToAssociate(officer)
            clickOnAssociateOfficerPopUp()
            isOfficerAssociateSuccessDisplayed()

            for (item in 0..5) {
                fileListScreen.clickOnItemInPosition(item)
                isOfficerNameDisplayed(officer)
                clickOnBack()
            }
        }
    }
}
