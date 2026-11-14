/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.provider

import java8.nio.file.Files
import java8.nio.file.ProviderNotFoundException
import java8.nio.file.spi.FileSystemProvider
import com.universalfiles.app.provider.archive.ArchiveFileSystemProvider
import com.universalfiles.app.provider.common.AndroidFileTypeDetector
import com.universalfiles.app.provider.content.ContentFileSystemProvider
import com.universalfiles.app.provider.document.DocumentFileSystemProvider
import com.universalfiles.app.provider.ftp.FtpFileSystemProvider
import com.universalfiles.app.provider.ftp.FtpesFileSystemProvider
import com.universalfiles.app.provider.ftp.FtpsFileSystemProvider
import com.universalfiles.app.provider.linux.LinuxFileSystemProvider
import com.universalfiles.app.provider.root.isRunningAsRoot
import com.universalfiles.app.provider.sftp.SftpFileSystemProvider
import com.universalfiles.app.provider.smb.SmbFileSystemProvider
import com.universalfiles.app.provider.webdav.WebDavFileSystemProvider
import com.universalfiles.app.provider.webdav.WebDavsFileSystemProvider

object FileSystemProviders {
    /**
     * If set, WatchService implementations will skip processing any event data and simply send an
     * overflow event to all the registered keys upon successful read from the inotify fd. This can
     * help reducing the JNI and GC overhead when large amount of inotify events are generated.
     * Simply sending an overflow event to all the keys is okay because we use only one key per
     * service for WatchServicePathObservable.
     */
    @Volatile
    var overflowWatchEvents = false

    fun install() {
        FileSystemProvider.installDefaultProvider(LinuxFileSystemProvider)
        FileSystemProvider.installProvider(ArchiveFileSystemProvider)
        if (!isRunningAsRoot) {
            FileSystemProvider.installProvider(ContentFileSystemProvider)
            FileSystemProvider.installProvider(DocumentFileSystemProvider)
            FileSystemProvider.installProvider(FtpFileSystemProvider)
            FileSystemProvider.installProvider(FtpsFileSystemProvider)
            FileSystemProvider.installProvider(FtpesFileSystemProvider)
            FileSystemProvider.installProvider(SftpFileSystemProvider)
            FileSystemProvider.installProvider(SmbFileSystemProvider)
            FileSystemProvider.installProvider(WebDavFileSystemProvider)
            FileSystemProvider.installProvider(WebDavsFileSystemProvider)
        }
        Files.installFileTypeDetector(AndroidFileTypeDetector)
    }

    operator fun get(scheme: String): FileSystemProvider {
        for (provider in FileSystemProvider.installedProviders()) {
            if (provider.scheme.equals(scheme, ignoreCase = true)) {
                return provider
            }
        }
        throw ProviderNotFoundException(scheme)
    }
}
