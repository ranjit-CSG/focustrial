package com.lawmobile.data.mappers.impl

import com.lawmobile.data.dto.entities.UserDto
import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.User
import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser

object UserMapper : DomainMapper<Any, User> {

    override fun Any.toDomain(): User {
        return when (this) {
            is CameraUser -> toDomain()
            is UserDto -> toDomain()
            else -> throw NotImplementedError()
        }
    }

    private fun CameraUser.toDomain(): User = User(id = officerId, name = name, password = password)
    private fun UserDto.toDomain() = User(email = email, devicePassword = devicePassword)
}
