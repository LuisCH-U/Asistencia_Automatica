package com.pr_asistencia.asistencia_auto.activities.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.pr_asistencia.asistencia_auto.App
import com.pr_asistencia.asistencia_auto.R
import com.pr_asistencia.asistencia_auto.activities.LoginActivity

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnCerrarSesion)?.setOnClickListener {
            cerrarSesion()
        }

        view.findViewById<Button>(R.id.btnChangeTheme)?.setOnClickListener {
            toggleTheme()
        }
    }

    private fun toggleTheme() {
        val prefs = requireContext().getSharedPreferences("config", Context.MODE_PRIVATE)
        val darkModeActual = prefs.getBoolean(
            "darkMode",
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        )
        val nuevoDarkMode = !darkModeActual

        prefs.edit {
            putBoolean("darkMode", nuevoDarkMode)
        }

        AppCompatDelegate.setDefaultNightMode(
            if (nuevoDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun cerrarSesion() {
        val prefs = App.instance.securePrefs()

        prefs.edit().clear().apply()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
