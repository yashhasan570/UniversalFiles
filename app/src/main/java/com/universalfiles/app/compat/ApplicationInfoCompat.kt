/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.compat

import android.content.pm.ApplicationInfo
import android.os.Build
import com.universalfiles.app.hiddenapi.RestrictedHiddenApi
import com.universalfiles.app.util.lazyReflectedField

@RestrictedHiddenApi
private val versionCodeField by lazyReflectedField(ApplicationInfo::class.java, "versionCode")

@RestrictedHiddenApi
private val longVersionCodeField by lazyReflectedField(
    ApplicationInfo::class.java, "longVersionCode"
)

val ApplicationInfo.longVersionCodeCompat: Long
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            longVersionCodeField.getLong(this)
        } else {
            versionCodeField.getInt(this).toLong()
        }
