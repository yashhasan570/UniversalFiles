/*
 * Copyright (c) 2022 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.provider.ftp

import java8.nio.file.Path
import com.universalfiles.app.provider.ftp.client.Authority

fun Authority.createFtpRootPath(): Path =
    FtpFileSystemProvider.getOrNewFileSystem(this).rootDirectory
