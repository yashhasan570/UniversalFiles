/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.filelist

import java8.nio.file.Path
import com.universalfiles.app.file.MimeType
import com.universalfiles.app.file.isSupportedArchive
import com.universalfiles.app.provider.archive.archiveFile
import com.universalfiles.app.provider.archive.isArchivePath
import com.universalfiles.app.provider.document.isDocumentPath
import com.universalfiles.app.provider.document.resolver.DocumentResolver
import com.universalfiles.app.provider.linux.isLinuxPath

val Path.name: String
    get() = fileName?.toString() ?: if (isArchivePath) archiveFile.fileName.toString() else "/"

fun Path.toUserFriendlyString(): String = if (isLinuxPath) toFile().path else toUri().toString()

fun Path.isArchiveFile(mimeType: MimeType): Boolean = !isArchivePath && mimeType.isSupportedArchive

val Path.isLocalPath: Boolean
    get() =
        isLinuxPath || (isDocumentPath && DocumentResolver.isLocal(this as DocumentResolver.Path))

val Path.isRemotePath: Boolean
    get() = !isLocalPath
