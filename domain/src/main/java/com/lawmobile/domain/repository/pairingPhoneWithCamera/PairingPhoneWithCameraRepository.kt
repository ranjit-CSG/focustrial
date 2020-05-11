package com.lawmobile.domain.repository.pairingPhoneWithCamera

import androidx.lifecycle.LiveData
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.commons.helpers.Result

interface PairingPhoneWithCameraRepository : BaseRepository {
    val progressPairingCamera: LiveData<Result<Int>>
    suspend fun loadPairingCamera(hostnameToConnect: String, ipAddressClient: String)
    fun getSSIDSavedIfExist(): Result<String>
    fun saveSerialNumberOfCamera(serialNumber: String)
}