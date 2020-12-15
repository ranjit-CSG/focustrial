package com.safefleet.lawmobile.di.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSourceImpl
import com.lawmobile.data.repository.fileList.FileListRepositoryImpl
import com.lawmobile.domain.repository.fileList.FileListRepository
import com.lawmobile.domain.usecase.fileList.FileListUseCase
import com.lawmobile.domain.usecase.fileList.FileListUseCaseImpl
import com.safefleet.mobile.external_hardware.cameras.CameraService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
class FileListModule {

    companion object {
        @Provides
        fun provideFileListRemoteDataSource(cameraService: CameraService): FileListRemoteDataSource =
            FileListRemoteDataSourceImpl(cameraService)

        @Provides
        fun provideFileListRepository(fileListRemoteDataSource: FileListRemoteDataSource): FileListRepository =
            FileListRepositoryImpl(fileListRemoteDataSource)

        @Provides
        fun provideFileListUseCase(fileListRepository: FileListRepository): FileListUseCase =
            FileListUseCaseImpl(fileListRepository)
    }
}