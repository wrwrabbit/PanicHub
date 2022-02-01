package com.panic.ui.home.pages.adapter

import android.content.Intent
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.lang.ref.WeakReference

abstract class BaseFragmentPagerAdapter constructor(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private val registeredFragments = SparseArray<WeakReference<Fragment>>()

    fun getRegisteredFragment(position: Int): Fragment {
        return registeredFragments.get(position).get() ?: addFragment(createFragment(position))
    }

    fun getItem(position: Int): Fragment {
        return getRegisteredFragment(position)
    }

    abstract override fun createFragment(position: Int): Fragment

    fun addFragment(fragment: Fragment, position: Int = -1): Fragment {
        val index = if (position == -1) registeredFragments.size() else position
        fragment.let { registeredFragments.put(index, WeakReference(it)) }
        return fragment
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        for (i in 0 until itemCount) {
            val fragment = getRegisteredFragment(i)
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}
