package com.universalfiles.app.provider.remote;

import com.universalfiles.app.provider.remote.IRemoteFileSystem;
import com.universalfiles.app.provider.remote.IRemoteFileSystemProvider;
import com.universalfiles.app.provider.remote.IRemotePosixFileAttributeView;
import com.universalfiles.app.provider.remote.IRemotePosixFileStore;
import com.universalfiles.app.provider.remote.ParcelableObject;

interface IRemoteFileService {
    IRemoteFileSystemProvider getRemoteFileSystemProviderInterface(String scheme);

    IRemoteFileSystem getRemoteFileSystemInterface(in ParcelableObject fileSystem);

    IRemotePosixFileStore getRemotePosixFileStoreInterface(in ParcelableObject fileStore);

    IRemotePosixFileAttributeView getRemotePosixFileAttributeViewInterface(
        in ParcelableObject attributeView
    );
}
