package com.pr_asistencia.asistencia_auto.activities

import android.os.Build
import android.os.Bundle
import android.transition.Fade
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.pr_asistencia.asistencia_auto.R
import com.pr_asistencia.asistencia_auto.activities.fragments.AsistenciasFragment
import com.pr_asistencia.asistencia_auto.activities.fragments.HomeFragment
import com.pr_asistencia.asistencia_auto.activities.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColors.applyToActivityIfAvailable(
                this,
                DynamicColorsOptions.Builder().build()
            )
        }

        window.enterTransition = Fade().apply { duration = 300 }
        window.exitTransition = Fade().apply { duration = 300 }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_list -> AsistenciasFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> null
            }

            if (fragment != null) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit()
                true
            } else {
                false
            }
        }

        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_home
        }
    }
}
