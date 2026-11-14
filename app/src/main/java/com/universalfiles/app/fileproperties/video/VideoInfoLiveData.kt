/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.fileproperties.video

import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.util.Size
import java.time.Duration
import java8.nio.file.Path
import com.universalfiles.app.compat.use
import com.universalfiles.app.fileproperties.PathObserverLiveData
import com.universalfiles.app.fileproperties.date
import com.universalfiles.app.fileproperties.extractMetadataNotBlank
import com.universalfiles.app.fileproperties.location
import com.universalfiles.app.util.Failure
import com.universalfiles.app.util.Loading
import com.universalfiles.app.util.Stateful
import com.universalfiles.app.util.Success
import com.universalfiles.app.util.setDataSource
import com.universalfiles.app.util.valueCompat

class VideoInfoLiveData(path: Path) : PathObserverLiveData<Stateful<VideoInfo>>(path) {
    init {
        loadValue()
        observe()
    }

    override fun loadValue() {
        value = Loading(value?.value)
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            val value = try {
                val videoInfo = MediaMetadataRetriever().use { retriever ->
                    retriever.setDataSource(path)
                    val title = retriever.extractMetadataNotBlank(
                        MediaMetadataRetriever.METADATA_KEY_TITLE
                    )
                    val width = retriever.extractMetadataNotBlank(
                        MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
                    )?.toIntOrNull()
                    val height = retriever.extractMetadataNotBlank(
                        MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
                    )?.toIntOrNull()
                    val dimensions = if (width != null && height != null) {
                        Size(width, height)
                    } else {
                        null
                    }
                    val duration = retriever.extractMetadataNotBlank(
                        MediaMetadataRetriever.METADATA_KEY_DURATION
                    )?.toLongOrNull()?.let { Duration.ofMillis(it) }
                    val date = retriever.date
                    val location = retriever.location
                    val bitRate = retriever.extractMetadataNotBlank(
                        MediaMetadataRetriever.METADATA_KEY_BITRATE
                    )?.toLongOrNull()
                    VideoInfo(title, dimensions, duration, date, location, bitRate)
                }
                Success(videoInfo)
            } catch (e: Exception) {
                Failure(valueCompat.value, e)
            }
            postValue(value)
        }
    }
}
