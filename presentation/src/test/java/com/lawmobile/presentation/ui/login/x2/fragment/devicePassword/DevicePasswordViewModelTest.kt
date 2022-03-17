package com.lawmobile.presentation.ui.login.x2.fragment.devicePassword

import com.lawmobile.presentation.InstantExecutorExtension
import io.mockk.clearMocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class DevicePasswordViewModelTest {

    private val viewModel = DevicePasswordViewModel()

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        clearMocks()
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun testGetOfficerId() {
        Assert.assertEquals("", viewModel.officerId)
    }

    @Test
    fun testSetOfficerId() {
        val officerId = "123"
        viewModel.officerId = officerId
        Assert.assertEquals(officerId, viewModel.officerId)
    }

    @Test
    fun testGetOfficerPassword() {
        Assert.assertEquals("", viewModel.officerPassword)
    }

    @Test
    fun testSetOfficerPassword() {
        val password = "123"
        viewModel.officerPassword = password
        Assert.assertEquals(password, viewModel.officerPassword)
    }
}