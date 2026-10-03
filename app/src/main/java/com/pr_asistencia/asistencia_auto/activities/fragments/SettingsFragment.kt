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
import com.google.android.material.button.MaterialButtonToggleGroup
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

        view.findViewById<MaterialButtonToggleGroup>(R.id.grupoTema)?.apply {
            when (leerModoTema()) {
                1 -> check(R.id.btnTemaClaro)
                2 -> check(R.id.btnTemaOscuro)
                else -> check(R.id.btnTemaSistema)
            }

            addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (!isChecked) return@addOnButtonCheckedListener

                val modo = when (checkedId) {
                    R.id.btnTemaClaro -> 1
                    R.id.btnTemaOscuro -> 2
                    else -> 0
                }

                guardarModoTema(modo)

                AppCompatDelegate.setDefaultNightMode(
                    when (modo) {
                        1 -> AppCompatDelegate.MODE_NIGHT_NO
                        2 -> AppCompatDelegate.MODE_NIGHT_YES
                        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }
                )
            }
        }
    }

    private fun leerModoTema(): Int {
        val prefs = requireContext().getSharedPreferences("config", Context.MODE_PRIVATE)
        return when {
            prefs.contains("modoTema") -> prefs.getInt("modoTema", 0)
            prefs.contains("darkMode") -> if (prefs.getBoolean("darkMode", false)) 2 else 1
            else -> 0
        }
    }

    private fun guardarModoTema(modo: Int) {
        val prefs = requireContext().getSharedPreferences("config", Context.MODE_PRIVATE)
        prefs.edit {
            putInt("modoTema", modo)
        }
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
