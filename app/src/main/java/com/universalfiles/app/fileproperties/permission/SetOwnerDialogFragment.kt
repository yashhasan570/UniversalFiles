/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.universalfiles.app.fileproperties.permission

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import java8.nio.file.Path
import com.universalfiles.app.R
import com.universalfiles.app.file.FileItem
import com.universalfiles.app.filejob.FileJobService
import com.universalfiles.app.provider.common.PosixFileAttributes
import com.universalfiles.app.provider.common.PosixPrincipal
import com.universalfiles.app.provider.common.PosixUser
import com.universalfiles.app.provider.common.toByteString
import com.universalfiles.app.util.SelectionLiveData
import com.universalfiles.app.util.putArgs
import com.universalfiles.app.util.show
import com.universalfiles.app.util.viewModels

class SetOwnerDialogFragment : SetPrincipalDialogFragment() {
    override val viewModel: SetPrincipalViewModel by viewModels { { SetOwnerViewModel() } }

    @StringRes
    override val titleRes: Int = R.string.file_properties_permission_set_owner_title

    override fun createAdapter(selectionLiveData: SelectionLiveData<Int>): PrincipalListAdapter =
        UserListAdapter(selectionLiveData)

    override val PosixFileAttributes.principal: PosixPrincipal
        get() = owner()!!

    override fun setPrincipal(path: Path, principal: PrincipalItem, recursive: Boolean) {
        val owner = PosixUser(principal.id, principal.name?.toByteString())
        FileJobService.setOwner(path, owner, recursive, requireContext())
    }

    companion object {
        fun show(file: FileItem, fragment: Fragment) {
            SetOwnerDialogFragment().putArgs(Args(file)).show(fragment)
        }
    }
}
