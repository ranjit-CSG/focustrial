package com.lawmobile.presentation.ui.fileList

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.extensions.*
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.snapshotDetail.SnapshotDetailActivity
import com.lawmobile.presentation.ui.videoPlayback.VideoPlaybackActivity
import com.lawmobile.presentation.utils.Constants.CAMERA_CONNECT_FILE
import com.lawmobile.presentation.utils.Constants.FILE_LIST_SELECTOR
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.VIDEO_LIST
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.android.synthetic.main.activity_file_list.*
import kotlinx.android.synthetic.main.alert_dialog_associate_partner_id.view.*
import maes.tech.intentanim.CustomIntent
import javax.inject.Inject

class FileListActivity : BaseActivity() {

    @Inject
    lateinit var fileListViewModel: FileListViewModel
    private lateinit var fileListAdapter: FileListAdapter
    private val snapshotListFragment = SnapshotListFragment.getActualInstance()
    private val videoListFragment = VideoListFragment.getActualInstance()
    private lateinit var associatePartnerIdDialog: AlertDialog
    private var activeList: String? = null
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)

        CustomIntent.customType(this, "bottom-to-up")
        showLoadingDialog()

        activeList = intent.extras?.getString(FILE_LIST_SELECTOR)

        when (activeList) {
            SNAPSHOT_LIST -> {
                setSnapshotFragment()
            }
            VIDEO_LIST -> {
                setVideoFragment()
            }
        }

        setObservers()
        setListeners()
    }

    override fun onResume() {
        super.onResume()

        fileListCheckBox.isChecked = false

        when (activeList) {
            SNAPSHOT_LIST -> {
                fileListViewModel.getSnapshotList()
            }
            VIDEO_LIST -> {
                fileListViewModel.getVideoList()
            }
        }
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "up-to-bottom")
    }

    private fun setObservers() {
        fileListViewModel.snapshotListLiveData.observe(this, Observer(::handleFileListResult))
        fileListViewModel.videoListLiveData.observe(this, Observer(::handleFileListResult))
        fileListViewModel.snapshotPartnerIdLiveData.observe(this, Observer(::handlePartnerIdResult))
        fileListViewModel.videoPartnerIdLiveData.observe(this, Observer(::handlePartnerIdResult))
        fileListViewModel.isWaitFinishedLiveData.observe(this, Observer(::handleTimeout))
    }

    private fun handleTimeout(timedOut: Boolean) {
        if (timedOut) {
            if (isLoading) {
                this.showToast(getString(R.string.loading_files_error), Toast.LENGTH_SHORT)
                Thread.sleep(500)
                finish()
            }
        }
    }

    private fun setListeners() {
        buttonSnapshotListSwitch.setOnClickListenerCheckConnection {
            if (activeList != SNAPSHOT_LIST) {
                activeList = SNAPSHOT_LIST
                it.isActivated = true
                buttonVideoListSwitch.isActivated = false
                showLoadingDialog()
                setSnapshotFragment()
                fileListViewModel.getSnapshotList()
                fileListCheckBox.isChecked = false
            }
        }

        buttonVideoListSwitch.setOnClickListenerCheckConnection {
            if (activeList != VIDEO_LIST) {
                activeList = VIDEO_LIST
                it.isActivated = true
                buttonSnapshotListSwitch.isActivated = false
                showLoadingDialog()
                setVideoFragment()
                fileListViewModel.getVideoList()
                fileListCheckBox.isChecked = false
            }
        }

        fileListCheckBox.setOnCheckedChangeListener { _, _ ->
            if (!noFilesTextView.isVisible)
                fileListAdapter.checkAllItems()
        }

        textViewFileListBack.setOnClickListenerCheckConnection {
            onBackPressed()
            application.onTerminate()
        }

        textViewFileListExit.setOnClickListener {
            this.createAlertConfirmAppExit()
        }

        associatePartnerIdListButton.setOnClickListener {
            if (it.isActivated)
                createAlertAssociatePartnerID()
            else
                this.showToast(
                    getString(R.string.partner_id_button_disabled_message),
                    Toast.LENGTH_SHORT
                )
        }
    }

    @SuppressLint("InflateParams")
    private fun createAlertAssociatePartnerID() {
        val dialogLayout =
            LayoutInflater.from(this)
                .inflate(R.layout.alert_dialog_associate_partner_id, null, false)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogLayout)

        associatePartnerIdDialog = dialogBuilder.create().apply {
            setCanceledOnTouchOutside(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }

        dialogLayout.associatePartnerIdButton.setOnClickListener {
            val partnerID = dialogLayout.partner_id_edit_text.text.toString()
            associatePartnerId(partnerID)
        }
    }

    private fun associatePartnerId(partnerId: String) {
        showLoadingDialog()

        if (partnerId.isEmpty()) {
            this.showToast(getString(R.string.valid_partner_id_message), Toast.LENGTH_SHORT)
            hideLoadingDialog()
            return
        }

        val listSelected =
            fileListAdapter.fileList.filter { it.isChecked }.map { it.cameraConnectFile }

        when (activeList) {
            SNAPSHOT_LIST -> fileListViewModel.associatePartnerIdToSnapshotList(
                listSelected,
                partnerId
            )
            VIDEO_LIST -> fileListViewModel.associatePartnerIdToVideoList(
                listSelected,
                partnerId
            )
        }
        associatePartnerIdDialog.dismiss()
    }

    private fun setSnapshotFragment() {
        snapshotListText.typeface = Typeface.DEFAULT_BOLD
        videoListText.typeface = Typeface.DEFAULT
        activeList = SNAPSHOT_LIST
        supportFragmentManager.attachFragment(
            R.id.fragmentListHolder,
            snapshotListFragment,
            SNAPSHOT_LIST
        )
    }

    private fun setVideoFragment() {
        videoListText.typeface = Typeface.DEFAULT_BOLD
        snapshotListText.typeface = Typeface.DEFAULT
        activeList = VIDEO_LIST
        supportFragmentManager.attachFragment(
            R.id.fragmentListHolder,
            videoListFragment,
            VIDEO_LIST
        )
    }

    private fun handleFileListResult(result: Result<DomainInformationFileResponse>) {
        when (result) {
            is Result.Success -> {
                if (result.data.errors.isNotEmpty()) {
                    showErrors(result.data.errors)
                }
                if (result.data.listItems.isNotEmpty()) {
                    fragmentListHolder.isVisible = true
                    noFilesTextView.isVisible = false
                    setAdapter(result.data.listItems)
                } else {
                    noFilesTextView.isVisible = true
                    fragmentListHolder.isVisible = false
                    when (activeList) {
                        SNAPSHOT_LIST -> noFilesTextView.text = getString(R.string.no_images_found)
                        VIDEO_LIST -> noFilesTextView.text = getString(R.string.no_videos_found)
                    }
                }
                hideLoadingDialog()
            }
            is Result.Error -> {
                this.showToast(getString(R.string.file_list_failed_load_files), Toast.LENGTH_LONG)
                hideLoadingDialog()
            }
        }
    }

    private fun setAdapter(listItems: ArrayList<DomainInformationFile>) {
        fileListAdapter =
            FileListAdapter(::fileItemClick, ::enableAssociatePartnerButton)
        fileListAdapter.fileList = listItems.sortedByDescending { it.cameraConnectFile.date }
        when (activeList) {
            SNAPSHOT_LIST -> snapshotListFragment.setFileListAdapter?.invoke(fileListAdapter)
            VIDEO_LIST -> videoListFragment.setFileListAdapter?.invoke(fileListAdapter)
        }
    }

    private fun showErrors(errors: ArrayList<String>) {
        var customMessage = getString(R.string.getting_files_error_description) + "\n"
        errors.forEach {
            customMessage += it + "\n"
        }
        val alertInformation = AlertInformation(R.string.getting_files_error, null, {
            it.dismiss()
        }, null, customMessage)
        this.createAlertInformation(alertInformation)
    }

    private fun enableAssociatePartnerButton(checked: Boolean) {
        associatePartnerIdListButton.run {
            isActivated = checked
            background = if (checked)
                ContextCompat.getDrawable(this@FileListActivity, R.drawable.ic_associate_partner_id)
            else
                ContextCompat.getDrawable(
                    this@FileListActivity,
                    R.drawable.ic_associate_partner_id_off
                )
        }
    }

    private fun handlePartnerIdResult(result: Result<Unit>?) {
        when (result) {
            is Result.Success -> {
                this.showToast(
                    getString(R.string.file_list_associate_partner_id_success),
                    Toast.LENGTH_SHORT
                )
            }
            is Result.Error -> {
                this.showToast(
                    result.exception.message
                        ?: getString(R.string.file_list_associate_partner_id_error),
                    Toast.LENGTH_SHORT
                )
            }
        }
        hideLoadingDialog()
    }

    private fun fileItemClick(domainInformationFile: DomainInformationFile) {
        showLoadingDialog()
        startFileListIntent(domainInformationFile.cameraConnectFile)
    }

    private fun startFileListIntent(cameraConnectFile: CameraConnectFile) {
        val fileListIntent = Intent()
        when (activeList) {
            SNAPSHOT_LIST -> fileListIntent.setClass(this, SnapshotDetailActivity::class.java)
            VIDEO_LIST -> fileListIntent.setClass(this, VideoPlaybackActivity::class.java)
        }
        fileListIntent.putExtra(CAMERA_CONNECT_FILE, cameraConnectFile)
        hideLoadingDialog()
        startActivity(fileListIntent)
    }
}
