package com.lawmobile.presentation.ui.base.appBar.x2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AppBarX2ViewModel @Inject constructor(
    private val eventsUseCase: EventsUseCase,
    private val IODispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private val _pendingNotificationsCountResult: MediatorLiveData<Result<Int>> = MediatorLiveData()
    val pendingNotificationCountResult: LiveData<Result<Int>> get() = _pendingNotificationsCountResult

    suspend fun getUnreadNotificationCount() = withContext(IODispatcher) {
        _pendingNotificationsCountResult.postValue(eventsUseCase.getPendingNotificationsCount())
    }
}
