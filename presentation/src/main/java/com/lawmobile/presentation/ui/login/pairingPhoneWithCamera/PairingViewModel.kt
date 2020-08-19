package com.lawmobile.presentation.ui.login.pairingPhoneWithCamera

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.pairingPhoneWithCamera.PairingPhoneWithCameraUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch

class PairingViewModel @ViewModelInject constructor(
    private val pairingPhoneWithCameraUseCase: PairingPhoneWithCameraUseCase,
    private val wifiHelper: WifiHelper
) :
    BaseViewModel() {

    val progressConnectionWithTheCamera: MutableLiveData<Result<Int>> =
        pairingPhoneWithCameraUseCase.progressPairingCamera as MutableLiveData<Result<Int>>

    private val validateConnectionMediatorLiveData: MediatorLiveData<Result<Unit>> = MediatorLiveData()
    val validateConnectionLiveData: LiveData<Result<Unit>> get() = validateConnectionMediatorLiveData

    fun getProgressConnectionWithTheCamera() {
        val gateway = wifiHelper.getGatewayAddress()
        val ipAddress = wifiHelper.getIpAddress()
        if (ipAddress.isEmpty() || gateway.isEmpty()) {
            progressConnectionWithTheCamera.postValue(
                Result.Error(Exception(EXCEPTION_GET_PARAMS_TO_CONNECT))
            )
        }

        viewModelScope.launch {
            pairingPhoneWithCameraUseCase.loadPairingCamera(gateway, ipAddress)
        }
    }

    fun isPossibleConnection(){
        val gateway = wifiHelper.getGatewayAddress()
        if (gateway.isEmpty()) {
            validateConnectionMediatorLiveData.postValue(
                Result.Error(Exception(EXCEPTION_GET_PARAMS_TO_CONNECT))
            )
        }
        viewModelScope.launch {
            validateConnectionMediatorLiveData.postValue(pairingPhoneWithCameraUseCase.isPossibleTheConnection(gateway))
        }
    }

    fun resetProgress() {
        progressConnectionWithTheCamera.value = Result.Success(0)
    }

    fun isValidNumberCameraBWC(codeCamera: String): Boolean =
        codeCamera.contains(CAMERA_SSID_IDENTIFIER)

    fun getNetworkName() = wifiHelper.getSSIDWiFi()

    fun isWifiEnable(): Boolean = wifiHelper.isWifiEnable()

    companion object {
        const val EXCEPTION_GET_PARAMS_TO_CONNECT =
            "Exception in get params to configure connection"
        const val CAMERA_SSID_IDENTIFIER = "X57"
    }

}