/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.about

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.universalfiles.app.databinding.AboutFragmentBinding
import com.universalfiles.app.ui.LicensesDialogFragment
import com.universalfiles.app.util.createViewIntent
import com.universalfiles.app.util.startActivitySafe

class AboutFragment : Fragment() {
    private lateinit var binding: AboutFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        AboutFragmentBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.gitHubLayout.setOnClickListener { startActivitySafe(GITHUB_URI.createViewIntent()) }
        binding.licensesLayout.setOnClickListener { LicensesDialogFragment.show(this) }
        binding.privacyPolicyLayout.isVisible = true
        binding.privacyPolicyLayout.setOnClickListener {
            startActivitySafe(PRIVACY_POLICY_URI.createViewIntent())
        }
        binding.authorNameLayout.setOnClickListener {
            startActivitySafe(AUTHOR_TELEGRAM_URI.createViewIntent())
        }
    }

    companion object {
        private val GITHUB_URI = Uri.parse("https://github.com/yashhasan570/UniversalFiles")
        private val PRIVACY_POLICY_URI =
            Uri.parse("https://github.com/yashhasan570/UniversalFiles/blob/master/PRIVACY.md")
        private val AUTHOR_TELEGRAM_URI = Uri.parse("https://t.me/johnconstantine570")
    }
}
