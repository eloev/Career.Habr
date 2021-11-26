package com.yelloyew.careerhabr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

private lateinit var bottomNavigation: BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val navController = findNavController(R.id.host_fragment)
        bottomNavigation = findViewById(R.id.bottom_nav)
        bottomNavigation.setupWithNavController(navController)
    }

    fun hideBottomNav(hide: Boolean){
        bottomNavigation.isVisible = !hide
    }
}