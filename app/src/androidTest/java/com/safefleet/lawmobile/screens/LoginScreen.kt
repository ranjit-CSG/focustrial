package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.testData.TestLoginData
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo

class LoginScreen : BaseScreen() {

    fun isLogoDisplayed() = assertDisplayed(R.id.imageViewFMALogo)

    fun isInstructionsTextDisplayed() =
        assertDisplayed(R.id.buttonInstructionsToLinkCamera, R.string.instructions_to_link_camera)

    fun isWaitingForCameraTextDisplayed() =
        assertDisplayed(R.id.textWaitingForCamera, R.string.waiting_for_camera)

    fun isWelcomeTextDisplayed() =
        assertContains(R.id.textViewPassword, R.string.welcome_officer)

    fun typePassword(officerPassword: String = TestLoginData.OFFICER_PASSWORD.value) =
        writeTo(R.id.editTextOfficerPassword, officerPassword)

    fun go() = clickOn(R.id.buttonLogin)

    fun isIncorrectPasswordToastDisplayed() {
        toastMessage.isToastDisplayed(R.string.incorrect_password)
        //toastMessage.waitUntilToastDisappears(R.string.incorrect_password)
    }

    fun login() {
        try {
            go()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        typePassword()
        go()
    }

}
