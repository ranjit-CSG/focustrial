package net.safefleet.focus.screens

import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import net.safefleet.focus.R
import net.safefleet.focus.helpers.CustomAssertionActions.waitUntil

class SnapshotDetailScreen : BaseScreen() {

    fun goBack() = waitUntil { clickOn(R.id.imageButtonBackArrow) }

    fun clickAssociateOfficer() = waitUntil { clickOn(R.id.buttonAssociateOfficer) }

    fun typeOfficerIdToAssociate(officerId: String) = waitUntil { writeTo(R.id.editTextAssignToOfficer, officerId) }

    fun clickOnAssociateOfficerPopUp() {
        sleep(100)
        waitUntil { clickOn(R.id.buttonAssignToOfficer) }
    }

    fun isPhotoNameDisplayed(name: String) = waitUntil { assertDisplayed(R.id.photoNameValue, name) }

    fun isOfficerNameDisplayed(name: String) = waitUntil { assertDisplayed(R.id.officerValue, name) }

    fun isOfficerAssociateSuccessDisplayed() = waitUntil { assertDisplayed(R.string.file_list_associate_partner_id_success) }
}
