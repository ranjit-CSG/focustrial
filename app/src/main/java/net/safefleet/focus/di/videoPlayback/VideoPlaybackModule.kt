package net.safefleet.focus.di.videoPlayback

import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSource
import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSourceImpl
import com.lawmobile.data.repository.videoPlayback.VideoPlaybackRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.repository.videoPlayback.VideoPlaybackRepository
import com.lawmobile.domain.usecase.videoPlayback.VideoPlaybackUseCase
import com.lawmobile.domain.usecase.videoPlayback.VideoPlaybackUseCaseImpl
import com.lawmobile.presentation.ui.videoPlayback.VideoInformationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class VideoPlaybackModule {
    companion object {
        @Provides
        fun provideVideoPlaybackRemoteDataSource(cameraService: CameraServiceFactory): VideoPlaybackRemoteDataSource =
            VideoPlaybackRemoteDataSourceImpl(cameraService)

        @Provides
        fun provideVideoPlaybackRepository(videoPlaybackRemoteDataSource: VideoPlaybackRemoteDataSource): VideoPlaybackRepository =
            VideoPlaybackRepositoryImpl(videoPlaybackRemoteDataSource)

        @Provides
        fun provideVideoPlaybackUseCase(videoPlaybackRepository: VideoPlaybackRepository): VideoPlaybackUseCase =
            VideoPlaybackUseCaseImpl(videoPlaybackRepository)

        @Provides
        fun provideVideoMetadataManager() = VideoInformationManager()
    }
}
