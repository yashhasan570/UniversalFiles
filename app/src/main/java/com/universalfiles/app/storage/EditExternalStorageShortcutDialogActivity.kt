/*
 * Copyright (c) 2023 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.storage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import com.universalfiles.app.app.AppActivity
import com.universalfiles.app.util.args
import com.universalfiles.app.util.putArgs

class EditExternalStorageShortcutDialogActivity : AppActivity() {
    private val args by args<EditExternalStorageShortcutDialogFragment.Args>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Calls ensureSubDecor().
        findViewById<View>(android.R.id.content)
        if (savedInstanceState == null) {
            val fragment = EditExternalStorageShortcutDialogFragment().putArgs(args)
            supportFragmentManager.commit {
                add(fragment, EditExternalStorageShortcutDialogFragment::class.java.name)
            }
        }
    }
}
