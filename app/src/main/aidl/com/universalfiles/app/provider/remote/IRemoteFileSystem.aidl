package com.universalfiles.app.provider.remote;

import com.universalfiles.app.provider.remote.ParcelableException;

interface IRemoteFileSystem {
    void close(out ParcelableException exception);
}
