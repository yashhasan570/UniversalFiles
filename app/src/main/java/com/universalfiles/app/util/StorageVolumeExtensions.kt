package com.universalfiles.app.util

import android.os.storage.StorageVolume
import com.universalfiles.app.compat.directoryCompat

val StorageVolume.isMounted: Boolean
    get() = directoryCompat != null
