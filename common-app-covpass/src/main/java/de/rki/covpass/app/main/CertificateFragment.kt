/*
 * (C) Copyright IBM Deutschland GmbH 2021
 * (C) Copyright IBM Corp. 2021
 */

package de.rki.covpass.app.main

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.ensody.reactivestate.android.autoRun
import com.ensody.reactivestate.android.reactiveState
import com.ensody.reactivestate.dispatchers
import com.ensody.reactivestate.get
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.ibm.health.common.android.utils.viewBinding
import com.ibm.health.common.navigation.android.FragmentNav
import com.ibm.health.common.navigation.android.findNavigator
import com.ibm.health.common.navigation.android.getArgs
import com.journeyapps.barcodescanner.BarcodeEncoder
import de.rki.covpass.app.databinding.CertificateBinding
import de.rki.covpass.app.dependencies.covpassDeps
import de.rki.covpass.app.detail.DetailFragmentNav
import de.rki.covpass.commonapp.BaseFragment
import de.rki.covpass.sdk.cert.models.BoosterResult
import de.rki.covpass.sdk.cert.models.CovCertificate
import de.rki.covpass.sdk.cert.models.GroupedCertificatesId
import de.rki.covpass.sdk.cert.models.GroupedCertificatesList
import kotlinx.coroutines.invoke
import kotlinx.parcelize.Parcelize

@Parcelize
internal class CertificateFragmentNav(val certId: GroupedCertificatesId) : FragmentNav(CertificateFragment::class)

/**
 * Fragment which shows a [CovCertificate]
 */
internal class CertificateFragment : BaseFragment() {

    internal val args: CertificateFragmentNav by lazy { getArgs() }
    private val viewModel by reactiveState { CertificateViewModel(scope) }
    private val binding by viewBinding(CertificateBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        autoRun {
            // TODO: Optimize this, so we only update if our cert has changed and not something else
            updateViews(get(covpassDeps.certRepository.certs))
        }
    }

    private fun updateViews(certificateList: GroupedCertificatesList) {
        val certId = args.certId
        val groupedCertificate = certificateList.getGroupedCertificates(certId) ?: return
        val mainCombinedCertificate = groupedCertificate.getMainCertificate()
        val mainCertificate = mainCombinedCertificate.covCertificate
        val isMarkedAsFavorite = certificateList.isMarkedAsFavorite(certId)

        val certStatus = mainCombinedCertificate.status
        val isFavoriteButtonVisible = certificateList.certificates.size > 1

        launchWhenStarted {
            binding.certificateCard.qrCodeImage = generateQRCode(mainCombinedCertificate.qrContent)
        }

        val showBoosterNotification = !groupedCertificate.hasSeenBoosterDetailNotification &&
            groupedCertificate.boosterNotification.result == BoosterResult.Passed

        binding.certificateCard.createCertificateCardView(
            mainCertificate.fullName,
            isMarkedAsFavorite,
            certStatus,
            showBoosterNotification
        )
        binding.certificateCard.isFavoriteButtonVisible = isFavoriteButtonVisible
        binding.certificateCard.setOnFavoriteClickListener {
            viewModel.onFavoriteClick(args.certId)
        }
        binding.certificateCard.setOnCardClickListener {
            findNavigator().push(DetailFragmentNav(args.certId))
        }
        binding.certificateCard.setOnCertificateStatusClickListener {
            findNavigator().push(DetailFragmentNav(args.certId))
        }
    }

    private suspend fun generateQRCode(qrContent: String): Bitmap {
        return dispatchers.default {
            BarcodeEncoder().encodeBitmap(
                qrContent,
                BarcodeFormat.QR_CODE,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.widthPixels,
                mapOf(EncodeHintType.MARGIN to 0)
            )
        }
    }
}
