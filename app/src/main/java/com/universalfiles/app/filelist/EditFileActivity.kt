/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.filelist

import android.os.Bundle
import java8.nio.file.Path
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import com.universalfiles.app.app.AppActivity
import com.universalfiles.app.file.MimeType
import com.universalfiles.app.file.fileProviderUri
import com.universalfiles.app.util.ParcelableArgs
import com.universalfiles.app.util.ParcelableParceler
import com.universalfiles.app.util.args
import com.universalfiles.app.util.createEditIntent
import com.universalfiles.app.util.startActivitySafe

// Use a trampoline activity so that we can have a proper icon and title.
class EditFileActivity : AppActivity() {
    private val args by args<Args>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivitySafe(args.path.fileProviderUri.createEditIntent(args.mimeType))
        finish()
    }

    @Parcelize
    class Args(
        val path: @WriteWith<ParcelableParceler> Path,
        val mimeType: MimeType
    ) : ParcelableArgs
}
