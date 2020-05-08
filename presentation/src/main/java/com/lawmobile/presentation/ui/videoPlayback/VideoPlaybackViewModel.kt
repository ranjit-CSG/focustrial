package com.lawmobile.presentation.ui.videoPlayback

import android.view.SurfaceView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entity.DomainInformationVideo
import com.lawmobile.domain.usecase.videoPlayback.VideoPlaybackUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch
import javax.inject.Inject

class VideoPlaybackViewModel @Inject constructor(
    private val videoPlaybackUseCase: VideoPlaybackUseCase,
    private val vlcMediaPlayer: VLCMediaPlayer
) : BaseViewModel() {

    private val domainInformationVideoMediatorLiveData =
        MediatorLiveData<Result<DomainInformationVideo>>()
    val domainInformationVideoLiveData get() = domainInformationVideoMediatorLiveData

    private val currentTimeVideoMediator by lazy { MediatorLiveData<Long>() }
    val currentTimeVideo: LiveData<Long> get() = currentTimeVideoMediator

    fun getInformationResourcesVideo(cameraConnectFile: CameraConnectFile) {
        viewModelScope.launch {
            domainInformationVideoMediatorLiveData.postValue(
                videoPlaybackUseCase.getInformationResourcesVideo(cameraConnectFile)
            )
        }
    }

    fun createVLCMediaPlayer(url: String, view: SurfaceView) {
        vlcMediaPlayer.createMediaPlayer(url, view)
    }

    fun playVLCMediaPlayer() {
        vlcMediaPlayer.playMediaPlayer()
    }

    fun pauseMediaPlayer() {
        vlcMediaPlayer.pauseMediaPlayer()
    }

    fun stopMediaPlayer() {
        vlcMediaPlayer.stopMediaPlayer()
    }

    fun changeAspectRatio() {
        vlcMediaPlayer.changeAspectRatio()
    }

    fun isMediaPlayerPaying() = vlcMediaPlayer.isMediaPlayerPaying()

    fun getTimeInMillisMediaPlayer() {
        currentTimeVideoMediator.postValue(vlcMediaPlayer.getTimeInMillisMediaPlayer())
    }

    fun setProgressMediaPlayer(progress: Float) {
        vlcMediaPlayer.setProgressMediaPlayer(progress)
    }
}