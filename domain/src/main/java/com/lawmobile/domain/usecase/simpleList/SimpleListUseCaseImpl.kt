package com.lawmobile.domain.usecase.simpleList

import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.repository.simpleList.SimpleListRepository
import com.safefleet.mobile.commons.helpers.Result
import javax.inject.Inject

class SimpleListUseCaseImpl @Inject constructor(private val simpleListRepository: SimpleListRepository) :
    SimpleListUseCase {
    override suspend fun getSnapshotList(): Result<DomainInformationFileResponse> =
        simpleListRepository.getSnapshotList()

    override suspend fun getVideoList(): Result<DomainInformationFileResponse> =
        simpleListRepository.getVideoList()
}