package com.panic.ui.home.pages.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.panic.ui.home.pages.Tabs
import com.panic.ui.home.pages.triggers.TriggersFragment
import com.panic.ui.home.pages.responders.RespondersFragment

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter constructor(
    fragment: Fragment
) : BaseFragmentPagerAdapter(fragment) {

    private val arrayOfTabs = Tabs.values()

    override fun createFragment(position: Int): Fragment {
        return when (arrayOfTabs[position]) {
            Tabs.Responders -> RespondersFragment.newInstance(position + 1)
            Tabs.Triggers -> TriggersFragment.newInstance(position + 1)
        }
    }

    override fun getItemCount(): Int {
        return Tabs.values().size
    }
}