/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.universalfiles.app.R
import com.universalfiles.app.databinding.AddLanSmbServerFragmentBinding
import com.universalfiles.app.ui.StaticAdapter
import com.universalfiles.app.util.Failure
import com.universalfiles.app.util.Loading
import com.universalfiles.app.util.Stateful
import com.universalfiles.app.util.fadeToVisibilityUnsafe
import com.universalfiles.app.util.finish
import com.universalfiles.app.util.launchSafe
import com.universalfiles.app.util.viewModels

class AddLanSmbServerFragment : Fragment() {
    private val addSmbServerLauncher = registerForActivityResult(
        EditSmbServerActivity.Contract(), this::onAddSmbServerResult
    )

    private val viewModel by viewModels { { AddLanSmbServerViewModel() } }

    private lateinit var binding: AddLanSmbServerFragmentBinding

    private lateinit var loadingAdapter: StaticAdapter
    private lateinit var serverListAdapter: LanSmbServerListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        AddLanSmbServerFragmentBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.reload() }
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        loadingAdapter = StaticAdapter(R.layout.lan_smb_server_loading_item)
        serverListAdapter = LanSmbServerListAdapter { addSmbServer(it) }
        val addAdapter = StaticAdapter(R.layout.lan_smb_server_add_item) { addSmbServer(null) }
        binding.recyclerView.adapter = ConcatAdapter(
            ConcatAdapter.Config.Builder()
                .setStableIdMode(ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS)
                .build(), loadingAdapter, serverListAdapter, addAdapter
        )

        viewModel.lanSmbServerListLiveData.observe(viewLifecycleOwner) {
            onLanSmbServerListChanged(it)
        }
    }

    private fun onLanSmbServerListChanged(stateful: Stateful<List<LanSmbServer>>) {
        if (stateful is Failure) {
            stateful.throwable.printStackTrace()
        }
        val isLoading = stateful is Loading
        binding.swipeRefreshLayout.isEnabled = !isLoading
        binding.swipeRefreshLayout.isRefreshing = false
        binding.progress.fadeToVisibilityUnsafe(isLoading)
        val servers = stateful.value ?: emptyList()
        loadingAdapter.itemCount = if (isLoading && servers.isEmpty()) 1 else 0
        serverListAdapter.replace(servers)
    }

    private fun addSmbServer(server: LanSmbServer?) {
        addSmbServerLauncher.launchSafe(EditSmbServerFragment.Args(host = server?.host), this)
    }

    private fun onAddSmbServerResult(result: Boolean) {
        if (result) {
            finish()
        }
    }
}
