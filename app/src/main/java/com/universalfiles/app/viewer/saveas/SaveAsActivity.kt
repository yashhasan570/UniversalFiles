/*
 * Copyright (c) 2024 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.viewer.saveas

import android.os.Bundle
import android.os.Environment
import java8.nio.file.Path
import java8.nio.file.Paths
import com.universalfiles.app.R
import com.universalfiles.app.app.AppActivity
import com.universalfiles.app.file.MimeType
import com.universalfiles.app.file.asMimeTypeOrNull
import com.universalfiles.app.filejob.FileJobService
import com.universalfiles.app.filelist.FileListActivity
import com.universalfiles.app.util.saveAsPath
import com.universalfiles.app.util.showToast

class SaveAsActivity : AppActivity() {
    private val createFileLauncher =
        registerForActivityResult(FileListActivity.CreateFileContract(), ::onCreateFileResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val mimeType = intent.type?.asMimeTypeOrNull() ?: MimeType.ANY
        val path = intent.saveAsPath
        if (path == null) {
            showToast(R.string.save_as_error)
            finish()
            return
        }
        val title = path.fileName.toString()
        val initialPath =
            Paths.get(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
            )
        createFileLauncher.launch(Triple(mimeType, title, initialPath))
    }

    private fun onCreateFileResult(result: Path?) {
        if (result == null) {
            finish()
            return
        }
        FileJobService.save(intent.saveAsPath!!, result, this)
        finish()
    }
}
