/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.navigation

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java8.nio.file.Path
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import com.universalfiles.app.R
import com.universalfiles.app.databinding.EditBookmarkDirectoryDialogBinding
import com.universalfiles.app.filelist.FileListActivity
import com.universalfiles.app.filelist.toUserFriendlyString
import com.universalfiles.app.util.ParcelableArgs
import com.universalfiles.app.util.ParcelableParceler
import com.universalfiles.app.util.ParcelableState
import com.universalfiles.app.util.args
import com.universalfiles.app.util.finish
import com.universalfiles.app.util.getState
import com.universalfiles.app.util.launchSafe
import com.universalfiles.app.util.layoutInflater
import com.universalfiles.app.util.putState
import com.universalfiles.app.util.setTextWithSelection

class EditBookmarkDirectoryDialogFragment : AppCompatDialogFragment() {
    private val openPathLauncher =
        registerForActivityResult(FileListActivity.OpenDirectoryContract(), ::onOpenPathResult)

    private val args by args<Args>()

    private lateinit var path: Path

    private lateinit var binding: EditBookmarkDirectoryDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        path = savedInstanceState?.getState<State>()?.path ?: args.bookmarkDirectory.path
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext(), theme)
            .setTitle(R.string.navigation_edit_bookmark_directory_title)
            .apply {
                binding = EditBookmarkDirectoryDialogBinding.inflate(context.layoutInflater)
                val bookmarkDirectory = args.bookmarkDirectory
                binding.nameLayout.placeholderText = bookmarkDirectory.defaultName
                if (savedInstanceState == null) {
                    binding.nameEdit.setTextWithSelection(bookmarkDirectory.name)
                }
                updatePathText()
                binding.pathText.setOnClickListener { onEditPath() }
                setView(binding.root)
            }
            .setPositiveButton(android.R.string.ok) { _, _ -> save() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
            .setNeutralButton(R.string.remove) { _, _ -> remove() }
            .create()
            .apply {
                window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putState(State(path))
    }

    private fun onEditPath() {
        openPathLauncher.launchSafe(path, this)
    }

    private fun onOpenPathResult(result: Path?) {
        result ?: return
        path = result
        updatePathText()
    }

    private fun updatePathText() {
        binding.pathText.setText(path.toUserFriendlyString())
    }

    private fun save() {
        val customName = binding.nameEdit.text.toString()
            .takeIf { it.isNotEmpty() && it != binding.nameLayout.placeholderText }
        val bookmarkDirectory = args.bookmarkDirectory.copy(customName = customName, path = path)
        BookmarkDirectories.replace(bookmarkDirectory)
        finish()
    }

    private fun remove() {
        BookmarkDirectories.remove(args.bookmarkDirectory)
        finish()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        finish()
    }

    @Parcelize
    class Args(val bookmarkDirectory: BookmarkDirectory) : ParcelableArgs

    @Parcelize
    private class State(var path: @WriteWith<ParcelableParceler> Path) : ParcelableState
}
