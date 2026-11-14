/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.provider.linux

import java8.nio.file.attribute.FileAttributeView
import com.universalfiles.app.provider.root.RootFileSystemProvider
import com.universalfiles.app.provider.root.RootableFileSystemProvider

object LinuxFileSystemProvider : RootableFileSystemProvider(
    { LocalLinuxFileSystemProvider(it as LinuxFileSystemProvider) },
    { RootFileSystemProvider(LocalLinuxFileSystemProvider.SCHEME) }
) {
    override val localProvider: LocalLinuxFileSystemProvider
        get() = super.localProvider as LocalLinuxFileSystemProvider

    override val rootProvider: RootFileSystemProvider
        get() = super.rootProvider as RootFileSystemProvider

    internal val fileSystem: LinuxFileSystem
        get() = localProvider.fileSystem

    internal fun supportsFileAttributeView(type: Class<out FileAttributeView>): Boolean =
        LocalLinuxFileSystemProvider.supportsFileAttributeView(type)
}
