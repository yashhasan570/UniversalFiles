/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.filelist

import android.content.Intent
import android.os.Bundle
import java8.nio.file.Path
import com.universalfiles.app.app.AppActivity
import com.universalfiles.app.app.application
import com.universalfiles.app.file.MimeType
import com.universalfiles.app.file.asMimeTypeOrNull
import com.universalfiles.app.file.fileProviderUri
import com.universalfiles.app.filejob.FileJobService
import com.universalfiles.app.provider.archive.isArchivePath
import com.universalfiles.app.util.createViewIntent
import com.universalfiles.app.util.extraPath
import com.universalfiles.app.util.startActivitySafe

class OpenFileActivity : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val path = intent.extraPath
        val mimeType = intent.type?.asMimeTypeOrNull()
        if (path != null && mimeType != null) {
            openFile(path, mimeType)
        }
        finish()
    }

    private fun openFile(path: Path, mimeType: MimeType) {
        if (path.isArchivePath) {
            FileJobService.open(path, mimeType, false, this)
        } else {
            val intent = path.fileProviderUri.createViewIntent(mimeType)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                .apply { extraPath = path }
            startActivitySafe(intent)
        }
    }

    companion object {
        private const val ACTION_OPEN_FILE = "com.universalfiles.app.intent.action.OPEN_FILE"

        fun createIntent(path: Path, mimeType: MimeType): Intent =
            Intent(ACTION_OPEN_FILE)
                .setPackage(application.packageName)
                .setType(mimeType.value)
                .apply { extraPath = path }
    }
}
