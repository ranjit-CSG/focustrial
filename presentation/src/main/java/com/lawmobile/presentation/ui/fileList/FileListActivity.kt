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
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.presentation.R
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
    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)

        CustomIntent.customType(this, "bottom-to-up")

        when (intent.extras?.getString(FILE_LIST_SELECTOR)) {
            SNAPSHOT_LIST -> {
                setSnapshotFragment()
            }
            VIDEO_LIST -> {
                setVideoFragment()
            }
        }

        setObservers()
        setListeners()
        createDialog()
        loadingDialog.show()
    }

    override fun onResume() {
        super.onResume()

        fileListCheckBox.isChecked = false

        if (buttonVideoListSwitch.isActivated) {
            fileListViewModel.getVideoList()
            return
        }

        fileListViewModel.getSnapshotList()
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "up-to-bottom")
    }

    private fun createDialog() {
        loadingDialog = this.createAlertProgress()
    }

    private fun setObservers() {
        fileListViewModel.snapshotListLiveData.observe(this, Observer(::handleFileListResult))
        fileListViewModel.videoListLiveData.observe(this, Observer(::handleFileListResult))
        fileListViewModel.snapshotPartnerIdLiveData.observe(this, Observer(::handlePartnerIdResult))
        fileListViewModel.videoPartnerIdLiveData.observe(this, Observer(::handlePartnerIdResult))
    }

    private fun setListeners() {
        buttonSnapshotListSwitch.setOnClickListenerCheckConnection {
            if (!it.isActivated) {
                loadingDialog.show()
                setSnapshotFragment()
                fileListViewModel.getSnapshotList()
                fileListCheckBox.isChecked = false
            }
        }

        buttonVideoListSwitch.setOnClickListenerCheckConnection {
            if (!it.isActivated) {
                loadingDialog.show()
                setVideoFragment()
                fileListViewModel.getVideoList()
                fileListCheckBox.isChecked = false
            }
        }

        fileListCheckBox.setOnCheckedChangeListener { _, _ ->
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
        val alertDialog = dialogBuilder.show()
        alertDialog.setCanceledOnTouchOutside(true)
        dialogLayout.associatePartnerIdButton.setOnClickListener {
            loadingDialog.show()
            val partnerID = dialogLayout.partner_id_edit_text.text.toString()
            if (partnerID.isEmpty()) {
                this.showToast(getString(R.string.valid_partner_id_message), Toast.LENGTH_SHORT)
                loadingDialog.dismiss()
                return@setOnClickListener
            }
            val listSelected =
                fileListAdapter.fileList.filter { it.isChecked }.map { it.cameraConnectFile }
            when (buttonSnapshotListSwitch.isActivated) {
                true -> fileListViewModel.associatePartnerIdToSnapshotList(
                    listSelected,
                    partnerID
                )
                false -> fileListViewModel.associatePartnerIdToVideoList(
                    listSelected,
                    partnerID
                )
            }
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun setSnapshotFragment() {
        snapshotListText.typeface = Typeface.DEFAULT_BOLD
        videoListText.typeface = Typeface.DEFAULT
        buttonSnapshotListSwitch.isActivated = true
        buttonVideoListSwitch.isActivated = false
        supportFragmentManager.attachFragment(
            R.id.fragmentListHolder,
            snapshotListFragment,
            SNAPSHOT_LIST
        )
    }

    private fun setVideoFragment() {
        videoListText.typeface = Typeface.DEFAULT_BOLD
        snapshotListText.typeface = Typeface.DEFAULT
        buttonVideoListSwitch.isActivated = true
        buttonSnapshotListSwitch.isActivated = false
        supportFragmentManager.attachFragment(
            R.id.fragmentListHolder,
            videoListFragment,
            VIDEO_LIST
        )
    }

    private fun handleFileListResult(result: Result<List<DomainInformationFile>>) {
        when (result) {
            is Result.Success -> {
                if (result.data.isNotEmpty()) {
                    fragmentListHolder.isVisible = true
                    noFilesTextView.isVisible = false
                    fileListAdapter =
                        FileListAdapter(::fileItemClick, ::enableAssociatePartnerButton)
                    fileListAdapter.fileList = result.data.sortedByDescending { it.cameraConnectFile.date }
                    when (buttonSnapshotListSwitch.isActivated) {
                        true -> snapshotListFragment.setFileListAdapter?.invoke(fileListAdapter)
                        false -> videoListFragment.setFileListAdapter?.invoke(fileListAdapter)
                    }
                } else {
                    noFilesTextView.isVisible = true
                    fragmentListHolder.isVisible = false
                    when (buttonSnapshotListSwitch.isActivated) {
                        true -> noFilesTextView.text = getString(R.string.no_images_found)
                        false -> noFilesTextView.text = getString(R.string.no_videos_found)
                    }
                }
                loadingDialog.dismiss()
            }
            is Result.Error -> {
                this.showToast(getString(R.string.file_list_failed_load_files), Toast.LENGTH_LONG)
                loadingDialog.dismiss()
            }
        }
    }

    private fun enableAssociatePartnerButton(checked: Boolean) {
        associatePartnerIdListButton.run {
            isActivated = checked
            background = if (checked)
                getDrawable(R.drawable.ic_associate_partner_id)
            else
                getDrawable(R.drawable.ic_associate_partner_id_off)
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
        loadingDialog.dismiss()
    }

    private fun fileItemClick(domainInformationFile: DomainInformationFile) {
        loadingDialog.show()
        when (buttonSnapshotListSwitch.isActivated) {
            true -> startSnapshotIntent(domainInformationFile.cameraConnectFile)
            false -> startVideoIntent(domainInformationFile.cameraConnectFile)
        }
    }

    private fun startSnapshotIntent(cameraConnectFile: CameraConnectFile) {
        val fileListIntent = Intent(this, SnapshotDetailActivity::class.java)
        fileListIntent.putExtra(CAMERA_CONNECT_FILE, cameraConnectFile)
        startActivity(fileListIntent)
    }

    private fun startVideoIntent(cameraConnectFile: CameraConnectFile) {
        val fileListIntent = Intent(this, VideoPlaybackActivity::class.java)
        fileListIntent.putExtra(CAMERA_CONNECT_FILE, cameraConnectFile)
        startActivity(fileListIntent)
    }
}
