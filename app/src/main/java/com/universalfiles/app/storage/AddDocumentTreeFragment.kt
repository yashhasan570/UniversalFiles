/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.storage

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.universalfiles.app.file.DocumentTreeUri
import com.universalfiles.app.file.asDocumentTreeUriOrNull
import com.universalfiles.app.file.takePersistablePermission
import com.universalfiles.app.util.finish
import com.universalfiles.app.util.launchSafe

class AddDocumentTreeFragment : Fragment() {
    private val openDocumentTreeLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree(), this::onOpenDocumentTreeResult
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState == null) {
            openDocumentTreeLauncher.launchSafe(null, this)
        }
    }

    private fun onOpenDocumentTreeResult(result: Uri?) {
        val treeUri = result?.asDocumentTreeUriOrNull()
        if (treeUri != null) {
            addDocumentTree(treeUri)
        }
        finish()
    }

    private fun addDocumentTree(treeUri: DocumentTreeUri) {
        treeUri.takePersistablePermission()
        val documentTree = DocumentTree(null, null, treeUri)
        Storages.addOrReplace(documentTree)
    }
}
