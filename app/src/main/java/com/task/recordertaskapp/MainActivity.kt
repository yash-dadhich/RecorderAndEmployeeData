package com.task.recordertaskapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.task.recordertaskapp.fragments.EmployeesFragment
import com.task.recordertaskapp.fragments.RecordingsFragment


// java
class MainActivity : AppCompatActivity() {

    private val sharedPreferences by lazy {
        getSharedPreferences("audio_prefs", MODE_PRIVATE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)

        tabLayout.addTab(tabLayout.newTab().setText("RECORDINGS"))
        tabLayout.addTab(tabLayout.newTab().setText("EMPLOYEE"))

        val fragmentManager: FragmentManager = supportFragmentManager
        val adapter: FragmentStateAdapter =
            object : FragmentStateAdapter(fragmentManager, lifecycle) {
                override fun createFragment(position: Int): Fragment {
                    return if (position == 0) {
                        RecordingsFragment()
                    } else {
                        EmployeesFragment()
                    }
                }

                override fun getItemCount(): Int {
                    return 2
                }
            }

        viewPager.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

    override fun onResume() {
        super.onResume()

        // Retrieve and show the stored data
        val audioName = sharedPreferences.getString("audio_name", "No Name")
        val backgroundColor = sharedPreferences.getInt("background_color", R.color.black)

        // Show the data as a Toast
        Toast.makeText(this, "Audio Name: $audioName, Background Color ID: $backgroundColor", Toast.LENGTH_LONG).show()


    }
}