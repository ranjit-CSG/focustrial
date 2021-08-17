package com.lawmobile.presentation.ui.fileList.simpleList

import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.usecase.simpleList.SimpleListUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class SimpleListViewModelTest {
    private val simpleListUseCase: SimpleListUseCase = mockk()
    private val simpleListViewModel: SimpleListViewModel by lazy {
        SimpleListViewModel(simpleListUseCase)
    }

    private val dispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        mockkObject(BaseViewModel)
        every { BaseViewModel.getLoadingTimeOut() } returns 1000
    }

    @Test
    fun testGetSnapshotListSuccess() = runBlockingTest {
        val domainInformationFileResponse: DomainInformationFileResponse = mockk()
        val result = Result.Success(domainInformationFileResponse)
        coEvery { simpleListUseCase.getSnapshotList() } returns result
        simpleListViewModel.getSnapshotList()
        Assert.assertEquals(simpleListViewModel.fileListLiveData.value, result)
        coVerify { simpleListUseCase.getSnapshotList() }
    }

    @Test
    fun testGetSnapshotListError() = runBlockingTest {
        val result = Result.Error(mockk())
        coEvery { simpleListUseCase.getSnapshotList() } returns result
        simpleListViewModel.getSnapshotList()
        Assert.assertEquals(simpleListViewModel.fileListLiveData.value, result)
        coVerify { simpleListUseCase.getSnapshotList() }
    }

    @Test
    fun testGetSnapshotListErrorTimeOut() = runBlockingTest {
        mockkObject(BaseViewModel)
        every { BaseViewModel.getLoadingTimeOut() } returns 0
        val domainInformationFileResponse: DomainInformationFileResponse = mockk()
        val result = Result.Success(domainInformationFileResponse)
        coEvery { simpleListUseCase.getSnapshotList() } returns result
        simpleListViewModel.getSnapshotList()
        Assert.assertTrue(simpleListViewModel.fileListLiveData.value is Result.Error)
    }

    @Test
    fun testGetVideoListSuccess() = runBlockingTest {
        val domainInformationFileResponse: DomainInformationFileResponse = mockk()
        val result = Result.Success(domainInformationFileResponse)
        coEvery { simpleListUseCase.getVideoList() } returns result
        simpleListViewModel.getVideoList()
        Assert.assertEquals(simpleListViewModel.fileListLiveData.value, result)
        coVerify { simpleListUseCase.getVideoList() }
    }

    @Test
    fun testGetVideoListError() = runBlockingTest {
        val result = Result.Error(mockk())
        coEvery { simpleListUseCase.getVideoList() } returns result
        simpleListViewModel.getVideoList()
        Assert.assertEquals(simpleListViewModel.fileListLiveData.value, result)
        coVerify { simpleListUseCase.getVideoList() }
    }

    @Test
    fun testGetVideoListErrorTimeout() = runBlockingTest {
        mockkObject(BaseViewModel)
        every { BaseViewModel.getLoadingTimeOut() } returns 0
        val domainInformationFileResponse: DomainInformationFileResponse = mockk()
        val result = Result.Success(domainInformationFileResponse)
        coEvery { simpleListUseCase.getVideoList() } returns result
        simpleListViewModel.getVideoList()
        Assert.assertTrue(simpleListViewModel.fileListLiveData.value is Result.Error)
    }
}
