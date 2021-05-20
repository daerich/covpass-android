/*
 * (C) Copyright IBM Deutschland GmbH 2021
 * (C) Copyright IBM Corp. 2021
 */

package com.ibm.health.vaccination.app.certchecker.onboarding

import com.ibm.health.common.navigation.android.FragmentNav
import com.ibm.health.common.vaccination.app.onboarding.BaseWelcomeFragment
import com.ibm.health.vaccination.app.certchecker.R
import kotlinx.parcelize.Parcelize

@Parcelize
internal class WelcomeFragmentNav : FragmentNav(WelcomeFragment::class)

/**
 * CovPass Check specific welcome screen. Overrides the abstract functions from [BaseWelcomeFragment].
 */
internal class WelcomeFragment : BaseWelcomeFragment() {

    override fun getHeaderTextRes() = R.string.start_onboarding_title

    override fun getSubheaderTextRes() = R.string.start_onboarding_message

    override fun getEncryptionHeaderTextRes() = R.string.start_onboarding_secure_title

    override fun getEncryptionTextRes() = R.string.start_onboarding_secure_message

    override fun getMainImageRes() = R.drawable.onboarding_welcome

    override fun getOnboardingNav(): FragmentNav = OnboardingContainerFragmentNav()
}
