package com.lawmobile.domain.usecase.validatePasswordOfficer

import com.lawmobile.domain.entities.DomainUser
import com.safefleet.mobile.commons.helpers.Result

interface ValidatePasswordOfficerUseCase {
    suspend fun getUserInformation(): Result<DomainUser>
}