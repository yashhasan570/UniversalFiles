/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.util

import android.media.MediaDataSource
import android.media.MediaMetadataRetriever
import java.io.IOException
import java.nio.ByteBuffer
import java8.nio.channels.SeekableByteChannel
import java8.nio.file.Path
import com.universalfiles.app.provider.common.newByteChannel
import com.universalfiles.app.provider.document.isDocumentPath
import com.universalfiles.app.provider.document.resolver.DocumentResolver
import com.universalfiles.app.provider.ftp.isFtpPath
import com.universalfiles.app.provider.linux.isLinuxPath

val Path.isMediaMetadataRetrieverCompatible: Boolean
    get() = !isFtpPath

fun MediaMetadataRetriever.setDataSource(path: Path) {
    when {
        path.isLinuxPath -> setDataSource(path.toFile().path)
        path.isDocumentPath ->
            DocumentResolver.openParcelFileDescriptor(path as DocumentResolver.Path, "r")
                .use { pfd -> setDataSource(pfd.fileDescriptor) }
        else -> {
            val channel = try {
                path.newByteChannel()
            } catch (e: IOException) {
                throw IllegalArgumentException(e)
            }
            setDataSource(PathMediaDataSource(channel))
        }
    }
}

private class PathMediaDataSource(private val channel: SeekableByteChannel) : MediaDataSource() {
    @Throws(IOException::class)
    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        channel.position(position)
        return channel.read(ByteBuffer.wrap(buffer, offset, size))
    }

    @Throws(IOException::class)
    override fun getSize(): Long {
        return channel.size()
    }

    @Throws(IOException::class)
    override fun close() {
        channel.close()
    }
}
