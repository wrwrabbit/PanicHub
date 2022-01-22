package com.panic.ui.pages

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.panic.ui.pages.buttons.AlarmButtonsFragment
import com.panic.ui.pages.responders.RespondersFragment

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val arrayOfTabs = Tabs.values()

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return when (arrayOfTabs[position]) {
            Tabs.Responders -> RespondersFragment.newInstance(position + 1)
            Tabs.Buttons -> AlarmButtonsFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return context.resources.getString(arrayOfTabs[position].tabTitle)
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return arrayOfTabs.size
    }
}