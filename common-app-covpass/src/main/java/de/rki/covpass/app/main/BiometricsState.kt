/*
* ISC License
*
* Copyright (c) Erich Ericson 2022
*
* Permission to use, copy, modify, and/or distribute this software for any
* purpose with or without fee is hereby granted, provided that the above
* copyright notice and this permission notice appear in all copies.
*
* THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
* REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
* AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
* INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
* LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
* OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
* PERFORMANCE OF THIS SOFTWARE.
*/

package de.rki.covpass.app.main

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import com.ibm.health.common.android.utils.viewBinding
import com.ibm.health.common.navigation.android.FragmentNav
import com.ibm.health.common.navigation.android.findNavigator
import de.rki.covpass.app.databinding.AuthpageBinding
import de.rki.covpass.app.databinding.CovpassMainBinding
import de.rki.covpass.commonapp.BaseFragment
import kotlinx.parcelize.Parcelize

public interface AuthResult {
    public fun recvRes(res : Boolean)
}

internal class BiometricsState(context: Context?) {
    private val bioman = context?.let { BiometricManager.from(it) }
    private var promptInfo : BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("CovPassBiometrics")
            .setNegativeButtonText("Not available")
            .setSubtitle("Please confirm your identity to proceed")
            .setAllowedAuthenticators(BIOMETRIC_WEAK)
            .build();

    internal fun checkState(): Boolean = bioman?.canAuthenticate(BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS

    internal fun showBiometrics(ctx : Context?, frag : BaseFragment) {
        val biometricPrompt = BiometricPrompt(frag,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(ctx,
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                    frag.findNavigator().popUntil<AuthResult>()?.recvRes(false)
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(ctx,
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()
                    frag.findNavigator().popUntil<AuthResult>()?.recvRes(true)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(ctx, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                    frag.findNavigator().popUntil<AuthResult>()?.recvRes(false)
                }
            })
        biometricPrompt.authenticate(promptInfo);
    }
}

@Parcelize /* Sometimes OO is stupid */
internal class AuthenticationNav : FragmentNav(AuthenticationFragment::class)

internal class AuthenticationFragment : BaseFragment() {
    private val binding by viewBinding(AuthpageBinding::inflate)
    override fun onResume() {
        super.onResume()
        val biostat = BiometricsState(context)
        if (biostat.checkState()) {
            biostat.showBiometrics(context, this)
        }
    }
}
