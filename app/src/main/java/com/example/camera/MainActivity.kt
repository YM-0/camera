package com.example.camera

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*
import android.app.DatePickerDialog
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var imageLinearLayout: LinearLayout
    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_latest_image -> selectedFragment = LatestImageFragment()
                R.id.nav_select_date -> selectedFragment = DateSelectedImageFragment()
            }

            if (selectedFragment != null) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, selectedFragment)
                transaction.commit()
            }

            true
        }

        // Set default screen
        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, LatestImageFragment())
            transaction.commit()
        }
    }
}
