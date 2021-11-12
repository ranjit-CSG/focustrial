package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.Alert
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.safefleet.lawmobile.helpers.ToastMessage
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.clearText
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import com.schibsted.spain.barista.interaction.BaristaScrollInteractions.safelyScrollTo

class VideoPlaybackScreen : BaseScreen() {

    fun clickOnSave() = clickOn(R.id.saveButtonVideoPlayback)

    fun clickOnAddSnapshots() = clickOn(R.id.buttonAssociateSnapshots)

    fun selectEvent(data: VideoInformation) {
        clickOn(R.id.eventValue)
        data.metadata?.event?.name?.let { waitUntil { clickOn(it) } }
    }

    private fun selectGender(data: VideoInformation) {
        clickOn(R.id.genderValue)
        data.metadata?.gender?.let { clickOn(it) }
    }

    private fun selectRace(data: VideoInformation) {
        clickOn(R.id.raceValue)
        data.metadata?.race?.let { clickOn(it) }
    }

    fun typePartnerId(data: VideoInformation) =
        data.officerId?.let { writeTo(R.id.partnerIdValue, it) }

    fun updateField(inputId: Int, value: String) {
        clearText(inputId)
        writeTo(R.id.ticket1Value, value)
    }

    fun fieldHasValue(inputId: Int, value: String) {
        assertDisplayed(value)
    }

    fun fillAllFields(data: VideoInformation) {
        waitUntil { selectEvent(data) }
        waitUntil { selectGender(data) }
        waitUntil { selectRace(data) }

        data.officerId?.let { writeTo(R.id.partnerIdValue, it) }
        data.metadata?.run {
            ticketNumber?.let { writeTo(R.id.ticket1Value, it) }
            ticketNumber2?.let { writeTo(R.id.ticket2Value, it) }
            caseNumber?.let { writeTo(R.id.case1Value, it) }
            caseNumber2?.let { writeTo(R.id.case2Value, it) }
            dispatchNumber?.let { writeTo(R.id.dispatch1Value, it) }
            dispatchNumber2?.let { writeTo(R.id.dispatch2Value, it) }
            location?.let { writeTo(R.id.locationValue, it) }
            remarks?.let { writeTo(R.id.notesValue, it) }
            firstName?.let { writeTo(R.id.firstNameValue, it) }
            lastName?.let { writeTo(R.id.lastNameValue, it) }
            driverLicense?.let { writeTo(R.id.driverLicenseValue, it) }
            licensePlate?.let { writeTo(R.id.licensePlateValue, it) }
        }
    }

    fun checkIfFieldsAreFilled() {
        assertNotDisplayed(R.id.eventValue, "Select")
        assertNotDisplayed(R.id.genderValue, "Select")
        assertNotDisplayed(R.id.raceValue, "Select")
        waitUntil { assertNotDisplayed(R.id.partnerIdValue, "") }
        assertNotDisplayed(R.id.ticket2Value, "")
        assertNotDisplayed(R.id.case1Value, "")
        assertNotDisplayed(R.id.case2Value, "")
        assertNotDisplayed(R.id.dispatch1Value, "")
        assertNotDisplayed(R.id.dispatch2Value, "")
        assertNotDisplayed(R.id.locationValue, "")
        assertNotDisplayed(R.id.notesValue, "")
        assertNotDisplayed(R.id.firstNameValue, "")
        assertNotDisplayed(R.id.lastNameValue, "")
        assertNotDisplayed(R.id.driverLicenseValue, "")
        assertNotDisplayed(R.id.licensePlateValue, "")
    }

    fun isSavedSuccessDisplayed() =
        ToastMessage.isToastDisplayed(R.string.video_metadata_saved_success)

    fun isEventMandatoryDisplayed() {
        ToastMessage.isToastDisplayed(R.string.event_mandatory)
        waitUntil { ToastMessage.isToastNotDisplayed(R.string.event_mandatory) }
    }

    fun isMetadataChangesAlertDisplayed() = Alert.isMetadataChangesDisplayed()

    fun isAddSnapshotsButtonDisplayed() {
        safelyScrollTo(R.id.buttonAssociateSnapshots)
        waitUntil { assertDisplayed(R.id.buttonAssociateSnapshots) }
    }

    fun thereIsNoSnapshotAssociated() {
        safelyScrollTo(R.id.layoutAssociatedSnapshots)
        assertNotDisplayed(R.id.layoutAssociatedSnapshots)
    }

    fun thereAreSnapshotsAssociated() {
        safelyScrollTo(R.id.layoutAssociatedSnapshots)
        assertDisplayed(R.id.layoutAssociatedSnapshots)
    }

    fun checkIfFieldsAreUpdated(data: VideoInformation) {
        data.officerId?.let { checkIfFieldIsUpdated(R.id.partnerIdValue, it) }

        data.metadata?.run {
            ticketNumber?.let { checkIfFieldIsUpdated(R.id.ticket1Value, it) }
            ticketNumber2?.let { checkIfFieldIsUpdated(R.id.ticket2Value, it) }
            caseNumber?.let { checkIfFieldIsUpdated(R.id.case1Value, it) }
            caseNumber2?.let { checkIfFieldIsUpdated(R.id.case2Value, it) }
            dispatchNumber?.let { checkIfFieldIsUpdated(R.id.dispatch1Value, it) }
            dispatchNumber2?.let { checkIfFieldIsUpdated(R.id.dispatch2Value, it) }
            location?.let { checkIfFieldIsUpdated(R.id.locationValue, it) }
            remarks?.let { checkIfFieldIsUpdated(R.id.notesValue, it) }
            firstName?.let { checkIfFieldIsUpdated(R.id.firstNameValue, it) }
            lastName?.let { checkIfFieldIsUpdated(R.id.lastNameValue, it) }
            driverLicense?.let { checkIfFieldIsUpdated(R.id.driverLicenseValue, it) }
            licensePlate?.let { checkIfFieldIsUpdated(R.id.licensePlateValue, it) }
        }
    }

    fun checkIfFieldIsUpdated(id: Int, value: String) {
        safelyScrollTo(id)
        waitUntil { assertDisplayed(id, value) }
    }
}
