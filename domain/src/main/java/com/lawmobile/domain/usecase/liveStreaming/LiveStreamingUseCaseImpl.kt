package com.lawmobile.domain.usecase.liveStreaming

import com.lawmobile.domain.repository.liveStreaming.LiveStreamingRepository
import com.safefleet.mobile.commons.helpers.Result

class LiveStreamingUseCaseImpl(private val liveStreamingRepository: LiveStreamingRepository) :
    LiveStreamingUseCase {
    override fun getUrlForLiveStream(): String {
        return liveStreamingRepository.getUrlForLiveStream()
    }

    override suspend fun takePhoto(): Result<Unit> {
        return liveStreamingRepository.takePhoto()
    }
}