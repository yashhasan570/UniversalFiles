/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.app

import android.os.AsyncTask
import android.os.Build
import android.webkit.WebView
import jcifs.context.SingletonContext
import com.universalfiles.app.BuildConfig
import com.universalfiles.app.coil.initializeCoil
import com.universalfiles.app.filejob.fileJobNotificationTemplate
import com.universalfiles.app.ftpserver.ftpServerServiceNotificationTemplate
import com.universalfiles.app.hiddenapi.HiddenApi
import com.universalfiles.app.provider.FileSystemProviders
import com.universalfiles.app.settings.Settings
import com.universalfiles.app.storage.FtpServerAuthenticator
import com.universalfiles.app.storage.SftpServerAuthenticator
import com.universalfiles.app.storage.SmbServerAuthenticator
import com.universalfiles.app.storage.StorageVolumeListLiveData
import com.universalfiles.app.storage.WebDavServerAuthenticator
import com.universalfiles.app.theme.custom.CustomThemeHelper
import com.universalfiles.app.theme.night.NightModeHelper
import java.util.Properties
import com.universalfiles.app.provider.ftp.client.Client as FtpClient
import com.universalfiles.app.provider.sftp.client.Client as SftpClient
import com.universalfiles.app.provider.smb.client.Client as SmbClient
import com.universalfiles.app.provider.webdav.client.Client as WebDavClient

val appInitializers = listOf(
    ::disableHiddenApiChecks,
    ::initializeWebViewDebugging,
    ::initializeCoil,
    ::initializeFileSystemProviders,
    ::upgradeApp,
    ::initializeLiveDataObjects,
    ::initializeCustomTheme,
    ::initializeNightMode,
    ::createNotificationChannels
)

private fun disableHiddenApiChecks() {
    HiddenApi.disableHiddenApiChecks()
}

private fun initializeWebViewDebugging() {
    if (BuildConfig.DEBUG) {
        WebView.setWebContentsDebuggingEnabled(true)
    }
}

private fun initializeFileSystemProviders() {
    FileSystemProviders.install()
    FileSystemProviders.overflowWatchEvents = true
    // SingletonContext.init() calls NameServiceClientImpl.initCache() which connects to network.
    AsyncTask.THREAD_POOL_EXECUTOR.execute {
        SingletonContext.init(
            Properties().apply {
                setProperty("jcifs.netbios.cachePolicy", "0")
                setProperty("jcifs.smb.client.maxVersion", "SMB1")
            }
        )
    }
    FtpClient.authenticator = FtpServerAuthenticator
    SftpClient.authenticator = SftpServerAuthenticator
    SmbClient.authenticator = SmbServerAuthenticator
    WebDavClient.authenticator = WebDavServerAuthenticator
}

private fun initializeLiveDataObjects() {
    // Force initialization of LiveData objects so that it won't happen on a background thread.
    StorageVolumeListLiveData.value
    Settings.FILE_LIST_DEFAULT_DIRECTORY.value
}

private fun initializeCustomTheme() {
    CustomThemeHelper.initialize(application)
}

private fun initializeNightMode() {
    NightModeHelper.initialize(application)
}

private fun createNotificationChannels() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationManager.createNotificationChannels(
            listOf(
                backgroundActivityStartNotificationTemplate.channelTemplate,
                fileJobNotificationTemplate.channelTemplate,
                ftpServerServiceNotificationTemplate.channelTemplate
            ).map { it.create(application) }
        )
    }
}
