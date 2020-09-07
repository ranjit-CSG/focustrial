package com.lawmobile.presentation.ui.fileList

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.*
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.simpleList.SimpleFileListFragment
import com.lawmobile.presentation.ui.thumbnailList.ThumbnailFileListFragment
import com.lawmobile.presentation.utils.Constants.FILE_LIST_SELECTOR
import com.lawmobile.presentation.utils.Constants.FILE_LIST_TYPE
import com.lawmobile.presentation.utils.Constants.SIMPLE_FILE_LIST
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.THUMBNAIL_FILE_LIST
import com.lawmobile.presentation.utils.Constants.VIDEO_LIST
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.doIfError
import com.safefleet.mobile.commons.helpers.doIfSuccess
import com.safefleet.mobile.commons.helpers.hideKeyboard
import kotlinx.android.synthetic.main.activity_file_list.*
import kotlinx.android.synthetic.main.bottom_sheet_assign_to_officer.*
import kotlinx.android.synthetic.main.custom_app_bar.*
import kotlinx.android.synthetic.main.file_list_filter_dialog.*

class FileListActivity : BaseActivity() {

    private val fileListViewModel: FileListViewModel by viewModels()
    private val simpleFileListFragment = SimpleFileListFragment.getActualInstance()
    private var thumbnailFileListFragment = ThumbnailFileListFragment.getActualInstance()
    private var listType: String? = null
    private lateinit var actualFragment: String
    private val sheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(
            bottomSheetPartnerId
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)

        listType = intent.extras?.getString(FILE_LIST_SELECTOR)

        setObservers()
        setCustomAppBar()

        when (listType) {
            VIDEO_LIST -> setSimpleFileListFragment()
            SNAPSHOT_LIST -> setThumbnailListFragment()
        }

        setListeners()
        configureBottomSheet()
    }

    private fun setCustomAppBar() {
        when (listType) {
            SNAPSHOT_LIST -> {
                fileListAppBarTitle.text = getString(R.string.snapshots_title)
            }
            VIDEO_LIST -> {
                fileListAppBarTitle.text = getString(R.string.videos_title)
                buttonThumbnailList.isVisible = false
                buttonSimpleList.isVisible = false
            }
        }
    }

    private fun setObservers() {
        fileListViewModel.snapshotPartnerIdLiveData.observe(this, Observer(::handlePartnerIdResult))
        fileListViewModel.videoPartnerIdLiveData.observe(this, Observer(::handlePartnerIdResult))
    }

    private fun setSimpleFileListFragment() {
        actualFragment = SIMPLE_FILE_LIST
        buttonSimpleList.isActivated = true
        buttonThumbnailList.isActivated = false
        resetButtonAssociate()
        simpleFileListFragment.onFileCheck = ::enableAssociatePartnerButton
        simpleFileListFragment.arguments = Bundle().apply { putString(FILE_LIST_TYPE, listType) }
        supportFragmentManager.attachFragment(
            R.id.fragmentListHolder,
            simpleFileListFragment,
            SIMPLE_FILE_LIST
        )
    }

    private fun setListeners() {
        buttonThumbnailList.setClickListenerCheckConnection { setThumbnailListFragment() }
        buttonSimpleList.setClickListenerCheckConnection { setSimpleFileListFragment() }
        backArrowFileListAppBar.setOnClickListenerCheckConnection { onBackPressed() }
        buttonSelectSnapshotsToAssociate.setOnClickListenerCheckConnection { showCheckBoxes() }
        buttonAssociatePartnerIdList.setOnClickListenerCheckConnection { showAssignToOfficerBottomSheet() }
        buttonOpenFilters?.setOnClickListenerCheckConnection { showFilterDialog() }
    }

    private fun showFilterDialog() {
        this.createFilterDialog().apply {
            when (listType) {
                VIDEO_LIST -> eventsSpinnerFilter.isVisible = true
                SNAPSHOT_LIST -> eventsSpinnerFilter.isVisible = false
            }

            onApplyClick = {
                applyFiltersToLists(it)
                dismiss()
            }
        }
    }

    private fun applyFiltersToLists(filters: List<String>) {
        when (actualFragment) {
            SIMPLE_FILE_LIST -> {
                var filteredList = simpleFileListFragment.simpleFileListAdapter?.fileList

                if (filters[START_DATE_POSITION].isNotEmpty()) {
                    filteredList =
                        filteredList?.filter { it.cameraConnectFile.getCreationDate() >= filters[0] }
                }
                if (filters[END_DATE_POSITION].isNotEmpty()) {
                    filteredList =
                        filteredList?.filter { it.cameraConnectFile.getCreationDate() <= filters[1] }
                }
                if (filters[EVENT_POSITION].isNotEmpty()) {
                    filteredList =
                        filteredList?.filter { it.cameraConnectVideoMetadata?.metadata?.event?.name == filters[2] }
                }

                if (filteredList != null)
                    simpleFileListFragment.simpleFileListAdapter?.fileList = filteredList
            }
            THUMBNAIL_FILE_LIST -> {
                var filteredList = thumbnailFileListFragment.thumbnailFileListAdapter?.fileList

                if (filters[START_DATE_POSITION].isNotEmpty()) {
                    filteredList =
                        filteredList?.filter { it.cameraConnectFile.getCreationDate() >= filters[0] } as ArrayList<DomainInformationImage>
                }
                if (filters[END_DATE_POSITION].isNotEmpty()) {
                    filteredList =
                        filteredList?.filter { it.cameraConnectFile.getCreationDate() <= filters[1] } as ArrayList<DomainInformationImage>
                }

                if (filteredList != null)
                    thumbnailFileListFragment.thumbnailFileListAdapter?.fileList = filteredList
            }
        }
    }

    private fun configureBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        buttonAssignToOfficer.setOnClickListenerCheckConnection {
            associatePartnerId(editTextAssignToOfficer.text.toString())
            hideKeyboard()
        }
        buttonCloseAssignToOfficer.setOnClickListenerCheckConnection {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> shadowFileListView.isVisible = false
                    else -> shadowFileListView.isVisible = true
                }
            }
        })
    }

    private fun associatePartnerId(partnerId: String) {

        if (partnerId.isEmpty()) {
            constraintLayoutFileList.showErrorSnackBar(getString(R.string.valid_partner_id_message))
            return
        }

        showLoadingDialog()

        val listSelected = when (actualFragment) {
            SIMPLE_FILE_LIST -> simpleFileListFragment.simpleFileListAdapter?.fileList?.filter { it.isChecked }
                ?.map { it.cameraConnectFile }
            THUMBNAIL_FILE_LIST -> thumbnailFileListFragment.thumbnailFileListAdapter?.fileList?.filter { it.isAssociatedToVideo }
                ?.map { it.cameraConnectFile }
            else -> throw Exception("List type not supported")
        }

        listSelected?.let {
            when (listType) {
                SNAPSHOT_LIST -> fileListViewModel.associatePartnerIdToSnapshotList(
                    it,
                    partnerId
                )
                VIDEO_LIST -> fileListViewModel.associatePartnerIdToVideoList(
                    it,
                    partnerId
                )
            }
        }

        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setThumbnailListFragment() {
        actualFragment = THUMBNAIL_FILE_LIST
        buttonThumbnailList.isActivated = true
        resetButtonAssociate()
        thumbnailFileListFragment = ThumbnailFileListFragment.getActualInstance(true)
        thumbnailFileListFragment.onImageCheck = ::enableAssociatePartnerButton
        supportFragmentManager.attachFragment(
            R.id.fragmentListHolder,
            thumbnailFileListFragment,
            THUMBNAIL_FILE_LIST
        )
        buttonSimpleList.isActivated = false
    }

    private fun resetButtonAssociate() {
        with(buttonSelectSnapshotsToAssociate) {
            isActivated = false
            text = when (listType) {
                VIDEO_LIST -> getString(R.string.select_videos_to_associate)
                else -> getString(R.string.select_snapshots_to_associate)
            }
        }

        when (actualFragment) {
            SIMPLE_FILE_LIST -> {
                if (simpleFileListFragment.simpleFileListAdapter?.showCheckBoxes == true)
                    simpleFileListFragment.showCheckBoxes()
            }
            THUMBNAIL_FILE_LIST -> {
                if (thumbnailFileListFragment.thumbnailFileListAdapter?.showCheckBoxes == true)
                    thumbnailFileListFragment.showCheckBoxes()
            }
        }

        buttonAssociatePartnerIdList.isVisible = false
    }

    private fun activateButtonAssociate() {
        with(buttonSelectSnapshotsToAssociate) {
            isActivated = true
            text = getString(R.string.cancel)
        }
        when (actualFragment) {
            SIMPLE_FILE_LIST -> simpleFileListFragment.showCheckBoxes()
            THUMBNAIL_FILE_LIST -> thumbnailFileListFragment.showCheckBoxes()
        }
    }

    private fun showCheckBoxes() {
        if (buttonSelectSnapshotsToAssociate.isActivated) resetButtonAssociate()
        else activateButtonAssociate()
    }

    private fun showAssignToOfficerBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun enableAssociatePartnerButton(activate: Boolean) {
        buttonAssociatePartnerIdList.run {
            isActivated = activate
            buttonAssociatePartnerIdList.isVisible = activate
        }
    }

    private fun handlePartnerIdResult(result: Result<Unit>) {
        with(result) {
            doIfSuccess {
                constraintLayoutFileList.showSuccessSnackBar(getString(R.string.file_list_associate_partner_id_success))
                resetButtonAssociate()
            }
            doIfError {
                constraintLayoutFileList.showErrorSnackBar(
                    it.message ?: getString(R.string.file_list_associate_partner_id_error)
                )
            }
        }
        hideLoadingDialog()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        SimpleFileListFragment.instance = null
        ThumbnailFileListFragment.instance = null
    }

    override fun onDestroy() {
        super.onDestroy()
        SimpleFileListFragment.instance = null
        ThumbnailFileListFragment.instance = null
    }

    companion object {
        const val START_DATE_POSITION = 0
        const val END_DATE_POSITION = 1
        const val EVENT_POSITION = 2
    }
}
