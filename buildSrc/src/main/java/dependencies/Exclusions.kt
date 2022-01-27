package dependencies

object Exclusions {
    val sonarqube = mutableSetOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",

        //Dagger 2
        "**/*Dagger*Component*.*",
        "**/*Module.*",
        "**/*Modules.*",
        "**/*Module$*.*",
        "**/*MembersInjector*.*",
        "**/*_Factory*.*",
        "**/*Provide*Factory*.*",
        "**/hilt_aggregated_deps/**",

        //Classes I intentionally don't want to test
        "**/*Activity.*",
        "**/*Constants.*",
        "**/*RootedHelper.*",
        "**/App.class",
        "**/*Adapter.*",
        "**/*ViewHolder*.*",
        "**/app/*.*",

        "**/*Fragment.**",
        "**/*Manifest.**",
        "**/*Module.**",
        "**/*Application.**",
        "**/res/**",
        "**/entities/**",
        "**/helpers/**",
        "**/enums/**",
        "**/security/**",
        "**/models/**",
        "**/state/**",
        "**/utils/**",
        "**/app/**",
        "**/adapters/**",
        "**/widgets/**",
        "**/extensions/*.*",
        "**/*VLCMediaPlayer.*",
        "**/*DataStatus.**",
        "**/*WifiStatus.**",
        "**/root/**",
        "**/databinding.**",

        "**/database.**",
        "**/FakeHttpClient.**",
        "**/WifiHelperImpl.**",
        "**/FeatureSupportHelper.**",
        "**/SimpleNetworkManager.**",
        "**/OnSwipeTouchListener.**",
        "**/LiveActivityBaseViewModel.**",
        "**/CameraEventsManager.**",
        "**/DateHelper.**",
        "**/EspressoIdlingResource.**",
        "**/WifiHelper.**",
        "**/RunWithDelay.**",
        "**/Build.**",
        "**/SplashViewModel.**",
        "**/AuthStateManagerFactoryImpl.**",

        //Temporal empty file that is decreasing the coverage
        "**/LoginX1ViewModel.**",
        "**/EspressoIdlingResource.**",
        "**/RequestInterceptor.**",
        "**/LoginUseCases.**",
        "**/PreferencesManagerImpl.**", //here since could not found a way to mock datastore.edit
        "**/ConnectionHelperImpl.**",
        "**/VideoInformationManager.**"
    )
    val pitest = mutableSetOf(
        //Dagger 2
        "**.*Dagger*Component*",
        "**.*Module*",
        "**.*Modules*",
        "**.*MembersInjector*",
        "**.*_Factory*",
        "**.*Provide*Factory*",

        //Classes I intentionally don't want to test
        "**.*Activity*",
        "**.*Constants*",
        "**.App.class",
        "**.*Adapter*",
        "**.*ViewHolder*",

        "**.*Fragment*",
        "**.*Manifest*",
        "**.*Module*",
        "**.*Application*",
        "**.res.**",
        "**.entities.**",
        "**.security.**",
        "**.models.**",
        "**.adapters.**",
        "**.widgets.**",
        "**.extensions.**",
        "**.*Fragment*",
        "**.*RootedHelper.*",

        //Class to error #line is not valid line for pointer
        "com.lawmobile.data.repository.videoPlayback.VideoPlaybackRepositoryImpl*",
        "com.lawmobile.domain.CameraInfo*",
        "com.lawmobile.domain.entities.VideoListMetadata*",
        "com.lawmobile.presentation.ui.base.BaseViewModel*",
        "com.lawmobile.data.repository.thumbnailList.ThumbnailListRepositoryImpl*",
        "com.lawmobile.data.repository.simpleList.SimpleListRepositoryImpl*",
        "com.lawmobile.data.repository.events.EventsRepositoryImpl*",
        "com.lawmobile.presentation.utils.MobileDataStatus*",
        "com.lawmobile.presentation.utils.WifiStatus*",
        "com.lawmobile.data.repository.fileList.FileListRepositoryImpl*",
        "com.lawmobile.data.repository.liveStreaming.LiveStreamingRepositoryImpl*",
        "com.lawmobile.data.repository.snapshotDetail.SnapshotDetailRepositoryImpl*",
        "com.lawmobile.data.mappers.FileResponseMapper*",
        "com.lawmobile.presentation.utils.MobileDataStatus*",
        "com.lawmobile.presentation.utils.OnSwipeTouchListener*",
        "com.lawmobile.presentation.utils.CameraEventsManager*",
        "com.lawmobile.data.dto.api.validateOfficerId.ValidateOfficerIdApiImpl*",
        "com.lawmobile.presentation.ui.splash.SplashViewModel*",
        "com.lawmobile.data.dto.api.authorization.AuthorizationApiImpl*",
        "com.lawmobile.presentation.utils.PreferencesManagerImpl*",
        "com.lawmobile.data.dto.api.user.UserApiImpl*",
        "com.lawmobile.data.repository.audioDetail.AudioDetailRepositoryImpl*"
    )
}