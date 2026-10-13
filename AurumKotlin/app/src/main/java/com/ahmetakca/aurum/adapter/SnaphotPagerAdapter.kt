package com.ahmetakca.aurum.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ahmetakca.aurum.views.SnapshotFragment
import com.ahmetakca.aurum.views.SummaryFragment

class SnaphotPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SummaryFragment()
            1 -> SnapshotFragment()
            else -> throw IllegalArgumentException("Geçersiz pozisyon: $position")
        }
    }
}