package net.safefleet.focus.screens

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import net.safefleet.focus.R
import net.safefleet.focus.helpers.CustomAssertionActions.customSwipeRight
import net.safefleet.focus.helpers.CustomAssertionActions.waitUntil

class MainMenuScreen : BaseScreen() {

    fun clickOnMainMenu() {
        waitUntil { assertDisplayed(R.id.buttonMenu) }
        waitUntil { clickOn(R.id.buttonMenu) }
    }

    fun clickOnViewSnapshots() = waitUntil { clickOn(R.id.text_view_snapshots) }

    fun clickOnViewVideos() = clickOn(R.id.text_view_videos)

    fun clickOnNotifications() = clickOn(R.id.text_view_notification)

    fun clickOnViewDiagnose() = clickOn(R.id.text_view_diagnose)

    fun clickOnViewHelp() = waitUntil { clickOn(R.id.text_view_help) }

    fun clickOnCloseMenu() = clickOn(R.id.closeMenu)

    fun clickOnLogout() = clickOn(R.id.viewLogout)

    fun swipeRightMainMenu() {
        onView(withId(R.id.layoutMenu)).perform(customSwipeRight())
    }

    fun isViewSnapshotsDisplayed() =
        assertDisplayed(R.id.text_view_snapshots, R.string.live_view_menu_item_view_snapshots)

    fun isViewVideosDisplayed() =
        assertDisplayed(R.id.text_view_videos, R.string.live_view_menu_item_view_videos)

    fun isViewNotificationsDisplayed() =
        assertDisplayed(R.id.text_view_notification, R.string.live_view_menu_item_notification)

    fun isBodyWornDiagnosisDisplayed() =
        assertDisplayed(R.id.text_view_diagnose, R.string.live_view_menu_item_diagnose)

    fun isViewHelpDisplayed() =
        assertDisplayed(R.id.text_view_help, R.string.live_view_menu_item_help)

    fun isCloseMenuButtonDisplayed() = assertDisplayed(R.id.closeMenu)

    fun isLogoutButtonDisplayed() {
        assertDisplayed(R.id.viewLogout)
    }

    fun isMenuButtonNotContained() = waitUntil { assertNotContains(R.id.buttonMenu) }

    fun isMainMenuNotDisplayed() {
        assertNotDisplayed(R.id.text_view_snapshots, R.string.live_view_menu_item_view_snapshots)
        assertNotDisplayed(R.id.text_view_videos, R.string.live_view_menu_item_view_videos)
        assertNotDisplayed(R.id.text_view_notification, R.string.live_view_menu_item_notification)
        assertNotDisplayed(R.id.text_view_diagnose, R.string.live_view_menu_item_diagnose)
        assertNotDisplayed(R.id.text_view_help, R.string.live_view_menu_item_help)
        assertNotDisplayed(R.id.closeMenu)
        assertNotDisplayed(R.id.viewLogout)
    }

    fun isDashboardDisplayed() = waitUntil { assertDisplayed(R.id.text_view_dashboard, R.string.live_view_menu_item_dashboard) }
}
