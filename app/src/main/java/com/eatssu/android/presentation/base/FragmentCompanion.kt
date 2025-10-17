package com.eatssu.android.presentation.base

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

private const val FRAGMENT_ARGS_KEY = "fragment_args"

abstract class FragmentCompanion(
    private val fragmentBuilder: () -> Fragment,
) {
    fun newInstance(): Fragment {
        return fragmentBuilder()
    }
}

abstract class FragmentCompanionWithArgs<TArgs>(
    private val fragmentBuilder: () -> Fragment,
    private val argsClass: KClass<TArgs>
) where TArgs : Parcelable {

    fun newInstance(args: TArgs): Fragment {
        return fragmentBuilder().apply {
            arguments = Bundle().apply {
                putParcelable(FRAGMENT_ARGS_KEY, args)
            }
        }
    }

    val Fragment.fragmentOptions: TArgs?
        get() = arguments?.let {
            BundleCompat.getParcelable(it, FRAGMENT_ARGS_KEY, argsClass.java)
        }
}
