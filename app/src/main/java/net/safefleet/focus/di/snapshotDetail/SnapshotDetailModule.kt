package net.safefleet.focus.di.snapshotDetail

import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSource
import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSourceImpl
import com.lawmobile.data.repository.snapshotDetail.SnapshotDetailRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.repository.snapshotDetail.SnapshotDetailRepository
import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCase
import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class SnapshotDetailModule {

    companion object {
        @Provides
        fun provideSnapshotDetailRemoteDataSource(cameraService: CameraServiceFactory): SnapshotDetailRemoteDataSource =
            SnapshotDetailRemoteDataSourceImpl(cameraService)

        @Provides
        fun provideSnapshotDetailRepository(snapshotDetailRemoteDataSource: SnapshotDetailRemoteDataSource): SnapshotDetailRepository =
            SnapshotDetailRepositoryImpl(snapshotDetailRemoteDataSource)

        @Provides
        fun provideSnapshotDetailUseCase(snapshotDetailRepository: SnapshotDetailRepository): SnapshotDetailUseCase =
            SnapshotDetailUseCaseImpl(snapshotDetailRepository)
    }
}
