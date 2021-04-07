package com.ibm.health.common.vaccination.app.onboarding

import android.os.Bundle
import android.view.View
import com.ibm.health.common.android.utils.viewBinding
import com.ibm.health.common.navigation.android.FragmentNav
import com.ibm.health.common.navigation.android.findNavigator
import com.ibm.health.common.vaccination.app.BaseFragment
import com.ibm.health.common.vaccination.app.databinding.WelcomeBinding

public abstract class BaseWelcomeFragment : BaseFragment() {

    private val binding by viewBinding(WelcomeBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.welcomeHeaderTextview.setText(getHeaderTextRes())
        binding.welcomeSubheaderTextview.setText(getSubheaderTextRes())
        binding.welcomeEncryptionHeaderTextview.setText(getEncryptionHeaderTextRes())
        binding.welcomeEncryptionTextview.setText(getEncryptionTextRes())
        binding.welcomeMainImageview.setImageResource(getMainImageRes())
        binding.welcomeStartButton.setOnClickListener { findNavigator().push(getOnboardingNav()) }
    }

    protected abstract fun getHeaderTextRes(): Int

    protected abstract fun getSubheaderTextRes(): Int

    protected abstract fun getEncryptionHeaderTextRes(): Int

    protected abstract fun getEncryptionTextRes(): Int

    protected abstract fun getMainImageRes(): Int

    protected abstract fun getOnboardingNav(): FragmentNav
}