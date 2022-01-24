package com.example.artara_id.feature.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.artara_id.R
import com.example.artara_id.databinding.ActivityAboutBinding
import com.google.android.material.tabs.TabLayoutMediator

class AboutActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sectionPagerAdapter = SelectionPagerAdapter(this@AboutActivity)
        binding.viewPager2.adapter = sectionPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) {tab, position->
            if (getString(TAB_TITLES[position]).equals(getString(R.string.ourTeams), true)) {
                tab.text = getString(TAB_TITLES[position])
            } else {
                tab.text = getString(TAB_TITLES[position])
            }
        }.attach()

    }

    companion object {
        private val TAB_TITLES = intArrayOf(
            R.string.ourTeams,
            R.string.support
        )
    }
}