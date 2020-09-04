package com.lawmobile.presentation.widgets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.showDatePickerDialog
import kotlinx.android.synthetic.main.file_list_filter_dialog.*

class CustomFilterDialog constructor(
    context: Context, cancelable: Boolean
) : Dialog(context, cancelable, null), View.OnClickListener {

    var onApplyClick: ((List<String>) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.file_list_filter_dialog)

        buttonApplyFilter.setOnClickListener(this)
        buttonCancelFilter.setOnClickListener(this)
        closeFilterView.setOnClickListener(this)
        startDateTextView.setOnClickListener(this)
        endDateTextView.setOnClickListener(this)

        setEventsSpinner()
    }

    private fun setEventsSpinner() {
        val events = mutableListOf(context.getString(R.string.none))
        events.addAll(CameraInfo.events.map { it.name })
        eventsSpinnerFilter.adapter = ArrayAdapter(context, R.layout.spinner_item, events)
    }

    override fun onClick(v: View?) {
        when (v) {
            buttonApplyFilter -> onApplyClick?.invoke(getListOfFilters())
            buttonCancelFilter, closeFilterView -> dismiss()
            startDateTextView -> context.showDatePickerDialog(startDateTextView)
            endDateTextView -> context.showDatePickerDialog(endDateTextView)
        }
    }

    private fun getListOfFilters(): List<String> = listOf(getStartDate(), getEndDate(), getEvent())

    private fun getStartDate() =
        if (startDateTextView.text != context.getString(R.string.start_date_filter)) startDateTextView.text.toString() else ""

    private fun getEndDate() =
        if (endDateTextView.text != context.getString(R.string.end_date_filter)) endDateTextView.text.toString() else ""

    private fun getEvent() =
        if (eventsSpinnerFilter.selectedItem.toString() != context.getString(R.string.none)) eventsSpinnerFilter.selectedItem.toString() else ""

}