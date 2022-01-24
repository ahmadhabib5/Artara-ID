package com.example.artara_id.feature.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.artara_id.feature.about.support.SupportFragment
import com.example.artara_id.feature.about.teams.TeamsFragment

class SelectionPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {

        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = TeamsFragment()
            1 -> fragment = SupportFragment()
        }
        return fragment as Fragment
    }
}