package com.universalfiles.app.provider.remote;

import com.universalfiles.app.provider.remote.ParcelableException;
import com.universalfiles.app.util.RemoteCallback;

interface IRemotePathObservable {
    void addObserver(in RemoteCallback observer);

    void close(out ParcelableException exception);
}
