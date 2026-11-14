/*
 * Copyright (c) 2023 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.universalfiles.app.compat.registerReceiverCompat

class RuntimeBroadcastReceiver(
    private val filter: IntentFilter,
    private val receiver: BroadcastReceiver,
    private val context: Context,
    private val flags: Int = ContextCompat.RECEIVER_NOT_EXPORTED
) {
    fun register() {
        context.registerReceiverCompat(receiver, filter, flags)
    }

    fun unregister() {
        context.unregisterReceiver(receiver)
    }
}
