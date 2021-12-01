package com.lawmobile.presentation.ui.login.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentPairingResultBinding
import com.lawmobile.presentation.ui.base.BaseFragment
import com.safefleet.mobile.kotlin_commons.helpers.Result

class PairingResultFragment : BaseFragment() {

    private var _binding: FragmentPairingResultBinding? = null
    private val binding get() = _binding!!

    private val pairingViewModel: PairingViewModel by viewModels()
    lateinit var onConnectionSuccessful: () -> Unit
    private lateinit var animation: Animation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentPairingResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pairingViewModel.resetProgress()
        setObservers()
        setListeners()
        setAnimation()
        startConnectionToHotspotCamera()
    }

    private fun setAnimation() {
        animation = AnimationUtils.loadAnimation(context, R.anim.top_to_bottom_anim).apply {
            startOffset = 0
        }
    }

    private fun setListeners() {
        binding.buttonRetry.setOnClickListener {
            startConnectionToHotspotCamera()
        }
    }

    private fun showLoadingProgress() {
        binding.pairingProgressLayout.isVisible = true
        binding.pairingResultLayout.isVisible = false
        binding.buttonRetry.isVisible = false
    }

    private fun startConnectionToHotspotCamera() {
        showLoadingProgress()
        verifyConnectionWithTheCamera()
    }

    private fun saveSerialNumber() {
        val serialNumberCamera = pairingViewModel.getNetworkName()
        CameraInfo.serialNumber = serialNumberCamera.replace("X", "")
    }

    private fun verifyConnectionWithTheCamera() {
        activity?.runOnUiThread {
            verifyProgressConnectionWithTheCamera()
        }
    }

    private fun verifyProgressConnectionWithTheCamera() {
        pairingViewModel.getProgressConnectionWithTheCamera()
    }

    private fun setProgressInViewOfProgress(progress: Int) {
        val percent = "$progress%"
        binding.textViewProgressConnection.text = percent
        if (progress == PERCENT_TOTAL_CONNECTION_CAMERA) {
            saveSerialNumber()
            showSuccessResult()
        }
    }

    private fun setObservers() {
        pairingViewModel.cameraPairingProgress = ::manageResponseProgressInConnectionCamera
        pairingViewModel.isWaitFinishedLiveData.observe(
            viewLifecycleOwner
        ) {
            it.getContentIfNotHandled()?.run {
                if (this) onConnectionSuccessful()
            }
        }
    }

    private fun manageResponseProgressInConnectionCamera(result: Result<Int>) {
        when (result) {
            is Result.Success -> setProgressInViewOfProgress(result.data)
            is Result.Error -> showErrorResult()
        }
    }

    private fun showSuccessResult() {
        binding.pairingProgressLayout.isVisible = false
        binding.pairingResultLayout.isVisible = true
        binding.buttonRetry.isVisible = false
        binding.imageViewResultPairing.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_successful_green,
                null
            )
        )
        binding.textViewResultPairing.setText(R.string.success_connection_to_camera)
        binding.pairingResultLayout.startAnimation(animation)
        pairingViewModel.waitToFinish(ANIMATION_DURATION)
    }

    private fun showErrorResult() {
        binding.pairingProgressLayout.isVisible = false
        binding.pairingResultLayout.isVisible = true
        binding.buttonRetry.isVisible = true
        binding.imageViewResultPairing.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_error_big,
                null
            )
        )
        binding.textViewResultPairing.setText(R.string.error_connection_to_camera)
        binding.pairingResultLayout.startAnimation(animation)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        val TAG = PairingResultFragment::class.java.simpleName
        private const val PERCENT_TOTAL_CONNECTION_CAMERA = 100
        private const val ANIMATION_DURATION = 1200L

        fun createInstance(onConnectionSuccess: () -> Unit): PairingResultFragment =
            PairingResultFragment().apply { this.onConnectionSuccessful = onConnectionSuccess }
    }
}