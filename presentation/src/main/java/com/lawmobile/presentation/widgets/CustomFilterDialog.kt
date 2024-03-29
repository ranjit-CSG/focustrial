package com.lawmobile.presentation.widgets

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridLayout
import androidx.core.view.isVisible
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.enums.BackOfficeType
import com.lawmobile.domain.extensions.getDateDependingOnNameLength
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FileListFilterDialogBinding
import com.lawmobile.presentation.extensions.ifIsNotEmptyLet
import com.lawmobile.presentation.extensions.showDateAndTimePickerDialog
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetFilterTag

class CustomFilterDialog constructor(
    private val tagsGridLayout: GridLayout,
    private var onApplyClick: (Boolean) -> Unit,
    private var onCloseClick: () -> Unit
) : Dialog(tagsGridLayout.context, true, null), View.OnClickListener {

    private lateinit var binding: FileListFilterDialogBinding

    var listToFilter: List<DomainInformationForList> = emptyList()
    var currentFilters = mutableListOf<String>()
    var filteredList: List<DomainInformationForList> = emptyList()

    private var startDate = context.getString(R.string.start_date_filter)
    private var endDate = context.getString(R.string.end_date_filter)
    private var event = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = findViewById<View>(android.R.id.content) as ViewGroup
        binding = FileListFilterDialogBinding.inflate(layoutInflater, view)

        setListeners()
        setEventsSpinner()
        setDefaultFilters()
    }

    private fun setListeners() = with(binding) {
        buttonApplyFilter.setOnClickListener(this@CustomFilterDialog)
        buttonCancelFilter.setOnClickListener(this@CustomFilterDialog)
        closeFilterView.setOnClickListener(this@CustomFilterDialog)
        startDateTextView.setOnClickListener(this@CustomFilterDialog)
        endDateTextView.setOnClickListener(this@CustomFilterDialog)
        buttonClearStartDate.setOnClickListener(this@CustomFilterDialog)
        buttonClearEndDate.setOnClickListener(this@CustomFilterDialog)
    }

    override fun show() {
        super.show()
        showClearButtons()
    }

    fun isEventSpinnerFilterVisible(isVisible: Boolean) {
        binding.eventsSpinnerFilter.isVisible = isVisible
    }

    private fun setEventsSpinner() {
        var selectText = context.getString(R.string.select_event)
        var noDataText = context.getString(R.string.no_event)
        if (CameraInfo.backOfficeType == BackOfficeType.NEXUS) {
            selectText = context.getString(R.string.select_category)
            noDataText = context.getString(R.string.no_category)
        }
        val events = mutableListOf(
            selectText, noDataText
        ).apply { addAll(CameraInfo.metadataEvents.map { it.name }) }
        binding.eventsSpinnerFilter.adapter = ArrayAdapter(context, R.layout.spinner_item, events)
    }

    private fun setDefaultFilters() {
        if (this::binding.isInitialized) {
            binding.startDateTextView.text = startDate
            binding.endDateTextView.text = endDate
            binding.eventsSpinnerFilter.setSelection(event)
        }
    }

    private fun showClearButtons() {
        if (this::binding.isInitialized) {
            binding.buttonClearStartDate.isVisible = try {
                currentFilters[START_DATE_POSITION].isNotEmpty()
            } catch (e: Exception) {
                false
            }
            binding.buttonClearEndDate.isVisible = try {
                currentFilters[END_DATE_POSITION].isNotEmpty()
            } catch (e: Exception) {
                false
            }
        }
    }

    override fun dismiss() {
        super.dismiss()
        onCloseClick()
    }

    override fun onClick(v: View?) = with(binding) {
        when (v) {
            buttonApplyFilter -> {
                setListOfFilters()
                applyFiltersToLists()
                dismiss()
            }
            buttonCancelFilter, closeFilterView -> cancelChangesAndRestore()
            startDateTextView ->
                startDateTextView.showDateAndTimePickerDialog(0, 0) {
                    buttonClearStartDate.isVisible = true
                }
            endDateTextView ->
                endDateTextView.showDateAndTimePickerDialog(23, 59) {
                    buttonClearEndDate.isVisible = true
                }
            buttonClearStartDate -> clearStartDateFilter()
            buttonClearEndDate -> clearEndDateFilter()
        }
    }

    private fun cancelChangesAndRestore() = with(binding) {
        var selectText = context.getString(R.string.select_event)
        var noDataText = context.getString(R.string.no_event)
        if (CameraInfo.backOfficeType == BackOfficeType.NEXUS) {
            selectText = context.getString(R.string.select_category)
            noDataText = context.getString(R.string.no_category)
        }
        when (currentFilters.getOrNull(EVENT_POSITION)) {
            "", null, selectText ->
                eventsSpinnerFilter.setSelection(0)
            noDataText ->
                eventsSpinnerFilter.setSelection(1)
            else -> {
                val eventIndex = getEventIndexFromList() + 2
                eventsSpinnerFilter.setSelection(eventIndex)
            }
        }

        if (!currentFilters.getOrNull(START_DATE_POSITION).isNullOrEmpty()) {
            startDateTextView.text = currentFilters[START_DATE_POSITION]
        } else {
            startDateTextView.text = context.getString(R.string.start_date_filter)
        }

        if (!currentFilters.getOrNull(END_DATE_POSITION).isNullOrEmpty()) {
            endDateTextView.text = currentFilters[END_DATE_POSITION]
        } else {
            endDateTextView.text = context.getString(R.string.end_date_filter)
        }

        saveFiltersAsDefault()
        dismiss()
    }

    private fun clearStartDateFilter() {
        binding.startDateTextView.text = context.getString(R.string.start_date_filter)
        binding.buttonClearStartDate.isVisible = false
    }

    private fun clearEndDateFilter() {
        binding.endDateTextView.text = context.getString(R.string.end_date_filter)
        binding.buttonClearEndDate.isVisible = false
    }

    private fun saveFiltersAsDefault() {
        if (this::binding.isInitialized) {
            startDate = binding.startDateTextView.text.toString()
            endDate = binding.endDateTextView.text.toString()
            event = binding.eventsSpinnerFilter.selectedItemId.toInt()
        } else {
            startDate = getStartDateFromList()
            endDate = getEndDateFromList()
            event = getEventIndexFromList()
        }
    }

    private fun getEndDateFromList() =
        if (currentFilters[END_DATE_POSITION].isNotEmpty()) currentFilters[END_DATE_POSITION]
        else context.getString(R.string.end_date_filter)

    private fun getStartDateFromList() =
        if (currentFilters[START_DATE_POSITION].isNotEmpty()) currentFilters[START_DATE_POSITION]
        else context.getString(R.string.start_date_filter)

    private fun getEventIndexFromList(): Int {
        val index =
            CameraInfo.metadataEvents.indexOfFirst { it.name == currentFilters[EVENT_POSITION] }
        return if (index == -1) 0
        else index
    }

    private fun setListOfFilters() {
        currentFilters =
            mutableListOf(getStartDateFromView(), getEndDateFromView(), getEventFromView())
    }

    private fun getStartDateFromView() =
        if (binding.startDateTextView.text != context.getString(R.string.start_date_filter)) binding.startDateTextView.text.toString() else ""

    private fun getEndDateFromView() =
        if (binding.endDateTextView.text != context.getString(R.string.end_date_filter)) binding.endDateTextView.text.toString() else ""

    private fun getEventFromView() =
        if (binding.eventsSpinnerFilter.selectedItemId > 0) binding.eventsSpinnerFilter.selectedItem.toString() else ""

    fun applyFiltersToLists() {
        tagsGridLayout.removeAllViews()

        var dateTag = ""
        var filteringList = mutableListOf<DomainInformationForList>()
            .apply { addAll(listToFilter) }

        tagsGridLayout.let {
            currentFilters[START_DATE_POSITION].ifIsNotEmptyLet { startDate ->
                val dateWithoutHour = startDate.split(" ")[0]
                filteringList =
                    filteringList.filter { it.domainCameraFile.getDateDependingOnNameLength() >= startDate } as MutableList

                if (currentFilters[END_DATE_POSITION].isEmpty())
                    createTagInPosition(
                        it.childCount,
                        START_DATE_TAG + dateWithoutHour
                    )
                else dateTag = dateWithoutHour
            }

            currentFilters[END_DATE_POSITION].ifIsNotEmptyLet { endDate ->
                val dateWithoutHour = endDate.split(" ")[0]
                filteringList =
                    filteringList.filter { it.domainCameraFile.getDateDependingOnNameLength() <= endDate } as MutableList

                if (currentFilters[START_DATE_POSITION].isEmpty())
                    createTagInPosition(
                        it.childCount,
                        END_DATE_TAG + dateWithoutHour
                    )
                else {
                    dateTag += DATE_RANGE_TAG + dateWithoutHour
                    createTagInPosition(it.childCount, dateTag)
                }
            }

            currentFilters[EVENT_POSITION].ifIsNotEmptyLet { event ->
                filteringList =
                    filteringList.filter {
                    when (it) {
                        is DomainInformationFile -> {
                            it.domainVideoMetadata?.metadata?.event?.name ==
                                if (event == NO_EVENT_TAG) null else event
                        }
                        is DomainInformationImage -> {
                            it.domainVideoMetadata?.metadata?.event?.name ==
                                if (event == NO_EVENT_TAG) null else event
                        }
                        else -> {
                            false
                        }
                    }
                } as MutableList
                createTagInPosition(it.childCount, EVENT_TAG + event)
            }
        }

        filteredList = filteringList

        saveFiltersAsDefault()
        showClearButtons()
        onApplyClick.invoke(isAnyFilters())
    }

    private fun isAnyFilters() = currentFilters.any { it != "" }

    private fun createTagInPosition(position: Int, text: String) {
        tagsGridLayout.addView(
            SafeFleetFilterTag(context, null, 0).apply {
                tagText = text
                onClicked = {
                    clearTagFilter(text)
                    applyFiltersToLists()
                }
            },
            position
        )
    }

    private fun clearTagFilter(text: String) {
        when {
            text.contains(DATE_RANGE_TAG) -> {
                with(currentFilters) {
                    set(START_DATE_POSITION, "")
                    set(END_DATE_POSITION, "")
                }
                startDate = context.getString(R.string.start_date_filter)
                endDate = context.getString(R.string.end_date_filter)
            }
            text.contains(START_DATE_TAG) -> {
                currentFilters[START_DATE_POSITION] = ""
                startDate = context.getString(R.string.start_date_filter)
            }
            text.contains(END_DATE_TAG) -> {
                currentFilters[END_DATE_POSITION] = ""
                endDate = context.getString(R.string.end_date_filter)
            }
            text == currentFilters[EVENT_POSITION] -> {
                currentFilters[EVENT_POSITION] = ""
                event = 0
            }
        }
        setDefaultFilters()
    }

    companion object {
        private const val START_DATE_POSITION = 0
        private const val END_DATE_POSITION = 1
        private const val EVENT_POSITION = 2
        private const val START_DATE_TAG = "After: "
        private const val END_DATE_TAG = "Before: "
        private const val DATE_RANGE_TAG = " / "
        private const val EVENT_TAG = ""
        private const val NO_EVENT_TAG = "No event"
    }
}
