package com.universalfiles.app.provider.remote;

import com.universalfiles.app.provider.common.ParcelableFileTime;
import com.universalfiles.app.provider.common.ParcelablePosixFileMode;
import com.universalfiles.app.provider.common.PosixGroup;
import com.universalfiles.app.provider.common.PosixUser;
import com.universalfiles.app.provider.remote.ParcelableException;
import com.universalfiles.app.provider.remote.ParcelableObject;

interface IRemotePosixFileAttributeView {
    ParcelableObject readAttributes(out ParcelableException exception);

    void setTimes(
        in ParcelableFileTime lastModifiedTime,
        in ParcelableFileTime lastAccessTime,
        in ParcelableFileTime createTime,
        out ParcelableException exception
    );

    void setOwner(in PosixUser owner, out ParcelableException exception);

    void setGroup(in PosixGroup group, out ParcelableException exception);

    void setMode(in ParcelablePosixFileMode mode, out ParcelableException exception);

    void setSeLinuxContext(in ParcelableObject context, out ParcelableException exception);

    void restoreSeLinuxContext(out ParcelableException exception);
}
