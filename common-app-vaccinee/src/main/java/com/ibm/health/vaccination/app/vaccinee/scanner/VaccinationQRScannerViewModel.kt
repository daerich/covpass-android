/*
 * (C) Copyright IBM Deutschland GmbH 2021
 * (C) Copyright IBM Corp. 2021
 */

package com.ibm.health.vaccination.app.vaccinee.scanner

import com.ensody.reactivestate.StateFlowStore
import com.ensody.reactivestate.getData
import com.ibm.health.common.android.utils.BaseEvents
import com.ibm.health.common.android.utils.BaseState
import com.ibm.health.vaccination.app.vaccinee.dependencies.vaccineeDeps
import com.ibm.health.vaccination.sdk.android.cert.models.CombinedVaccinationCertificate
import com.ibm.health.vaccination.sdk.android.dependencies.sdkDeps
import kotlinx.coroutines.CoroutineScope

/**
 * Interface to communicate events from [VaccinationQRScannerViewModel] to [VaccinationQRScannerFragment].
 */
internal interface VaccinationQRScannerEvents : BaseEvents {
    fun onScanSuccess(certificateId: String)
}

/**
 * ViewModel holding the business logic for decoding the Vaccination Certificate.
 */
internal class VaccinationQRScannerViewModel(
    scope: CoroutineScope,
    store: StateFlowStore,
) : BaseState<VaccinationQRScannerEvents>(scope) {

    val lastCertificateId by store.getData<String?>(null)

    fun onQrContentReceived(qrContent: String) {
        launch {
            val vaccinationCertificate = sdkDeps.qrCoder.decodeVaccinationCert(qrContent)
            val certsFlow = vaccineeDeps.certRepository.certs
            certsFlow.update {
                it.addCertificate(
                    CombinedVaccinationCertificate(vaccinationCertificate, qrContent)
                )
            }
            val groupedCert = certsFlow.value.getGroupedCertificates(vaccinationCertificate.vaccination.id)
            lastCertificateId.value = groupedCert?.getMainCertId()
            eventNotifier {
                onScanSuccess(
                    vaccinationCertificate.vaccination.id
                )
            }
        }
    }
}
