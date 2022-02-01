package com.panic.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.currentNavFragment(): Fragment? {
    return supportFragmentManager.primaryNavigationFragment
        ?.childFragmentManager
        ?.primaryNavigationFragment
}