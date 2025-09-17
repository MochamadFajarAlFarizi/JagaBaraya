package com.fajar.jagabaraya

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var isFabExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        loadFragment(HomeFragment())

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val fabOption1 = findViewById<FloatingActionButton>(R.id.fab_option1)
        val fabOption2 = findViewById<FloatingActionButton>(R.id.fab_option2)


        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> loadFragment(HomeFragment())
                R.id.laporanku -> loadFragment(LaporanFragment())
                R.id.forum -> loadFragment(BeritaFragment())
                R.id.profile -> loadFragment(ProfileFragment())
            }
            true
        }


        fab.setOnClickListener {
            if (isFabExpanded) {
                fabOption1.hide()
                fabOption2.hide()
            } else {
                fabOption1.show()
                fabOption2.show()
            }
            isFabExpanded = !isFabExpanded
        }


        fabOption1.setOnClickListener {

            loadFragment(DaruratFragment())


            fab.hide()
            fabOption1.hide()
            fabOption2.hide()
            isFabExpanded = false
        }

        fabOption2.setOnClickListener {

            loadFragment(LaporForumFragment())


            fab.hide()
            fabOption1.hide()
            fabOption2.hide()
            isFabExpanded = false
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack(null) 
            .commit()
    }

}
