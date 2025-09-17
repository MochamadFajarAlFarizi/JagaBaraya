package com.fajar.jagabaraya.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fajar.jagabaraya.DiterimaFragment
import com.fajar.jagabaraya.LaporanListFragment

class LaporanPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
//            0 -> LaporanListFragment.newInstance("diterima")
//            1 -> LaporanListFragment.newInstance("diproses")
//            else -> LaporanListFragment.newInstance("selesai")
            0 -> DiterimaFragment()
            1 -> DiterimaFragment()
            else -> DiterimaFragment()
        }
    }
}
