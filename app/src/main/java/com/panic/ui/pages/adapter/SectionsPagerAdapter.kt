package com.panic.ui.pages.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import com.panic.ui.pages.Tabs
import com.panic.ui.pages.buttons.TriggersFragment
import com.panic.ui.pages.responders.RespondersFragment

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter constructor(
    activity: FragmentActivity
) : BaseFragmentPagerAdapter(activity) {

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