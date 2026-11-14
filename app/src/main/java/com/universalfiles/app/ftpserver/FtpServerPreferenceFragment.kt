/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.ftpserver

import android.os.Bundle
import com.universalfiles.app.R
import com.universalfiles.app.ui.PreferenceFragmentCompat

class FtpServerPreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.ftp_server)
    }
}
