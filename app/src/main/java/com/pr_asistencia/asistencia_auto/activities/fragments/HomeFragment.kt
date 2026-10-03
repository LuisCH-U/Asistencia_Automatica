package com.pr_asistencia.asistencia_auto.activities.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.ALARM_SERVICE
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.materialswitch.MaterialSwitch
import com.pr_asistencia.asistencia_auto.App
import com.pr_asistencia.asistencia_auto.R
import com.pr_asistencia.asistencia_auto.activities.LoginActivity
import com.pr_asistencia.asistencia_auto.firebase.FirebaseManager
import com.pr_asistencia.asistencia_auto.helper.AlarmHelper
import com.pr_asistencia.asistencia_auto.helper.NotificationHelper
import com.pr_asistencia.asistencia_auto.manager.AttendanceManager
import com.pr_asistencia.asistencia_auto.receiver.AttendanceReceiver
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : Fragment() {

    private lateinit var txtHoraEntrada: TextView
    private lateinit var txtHoraSalida: TextView
    private lateinit var switchAutomatico: MaterialSwitch
    private lateinit var switchActivo: MaterialSwitch
    private lateinit var switchFeriados: MaterialSwitch
    private lateinit var btnGuardar: Button
    private lateinit var btnMarcarAhora: Button
    private lateinit var btnVerAsistencias: Button
    private lateinit var checkLunes: CheckBox
    private lateinit var checkMartes: CheckBox
    private lateinit var checkMiercoles: CheckBox
    private lateinit var checkJueves: CheckBox
    private lateinit var checkViernes: CheckBox
    private lateinit var checkSabado: CheckBox
    private lateinit var checkDomingo: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtHoraEntrada = view.findViewById(R.id.txtHoraEntrada)
        txtHoraSalida = view.findViewById(R.id.txtHoraSalida)

        switchAutomatico = view.findViewById(R.id.switchAutomatico)
        switchActivo = view.findViewById(R.id.switchActivo)
        switchFeriados = view.findViewById(R.id.switchFeriados)

        checkLunes = view.findViewById(R.id.checkLunes)
        checkMartes = view.findViewById(R.id.checkMartes)
        checkMiercoles = view.findViewById(R.id.checkMiercoles)
        checkJueves = view.findViewById(R.id.checkJueves)
        checkViernes = view.findViewById(R.id.checkViernes)
        checkSabado = view.findViewById(R.id.checkSabado)
        checkDomingo = view.findViewById(R.id.checkDomingo)

        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnMarcarAhora = view.findViewById(R.id.btnMarcarAhora)
        btnVerAsistencias = view.findViewById(R.id.btnVerAsistencia)

        txtHoraEntrada.setOnClickListener {
            seleccionarHora(txtHoraEntrada)
        }
        txtHoraSalida.setOnClickListener {
            seleccionarHora(txtHoraSalida)
        }

        btnGuardar.setOnClickListener {
            guardarConfiguracion()
        }

        btnMarcarAhora.setOnClickListener {
            marcarManual()
        }

        btnVerAsistencias.setOnClickListener {
            requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottomNav)
                ?.selectedItemId = R.id.nav_list
        }

        view.findViewById<Button>(R.id.btnCerrarSesion)?.setOnClickListener {
            cerrarSesion()
        }

        solicitarPermisoExactAlarm()

        cargarConfiguracion()
    }

    private fun solicitarPermisoExactAlarm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(ALARM_SERVICE) as AlarmManager

            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun seleccionarHora(textView: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        TimePickerDialog(
            requireContext(),
            { _, h, m -> textView.text = String.format("%02d:%02d", h, m) },
            hour,
            minute,
            true
        ).show()
    }

    private fun guardarConfiguracion() {
        val prefs = requireContext().getSharedPreferences("config", Context.MODE_PRIVATE)

        prefs.edit {
            putString("horaEntrada", txtHoraEntrada.text.toString()
            ).putString("horaSalida", txtHoraSalida.text.toString()
            ).putBoolean("automatico", switchAutomatico.isChecked
            ).putBoolean("activo", switchActivo.isChecked
            ).putBoolean("feriadosActivos", switchFeriados.isChecked
            ).putBoolean("lunes", checkLunes.isChecked
            ).putBoolean("martes", checkMartes.isChecked
            ).putBoolean("miercoles", checkMiercoles.isChecked
            ).putBoolean("jueves", checkJueves.isChecked
            ).putBoolean("viernes", checkViernes.isChecked
            ).putBoolean("sabado", checkSabado.isChecked
            ).putBoolean("domingo", checkDomingo.isChecked)
        }

        val securePrefs = App.instance.securePrefs()
        val user = securePrefs.getString("user", "") ?: ""
        val tenant = securePrefs.getString("tenant", "InLearning") ?: ""

        val data = hashMapOf<String, Any>(
            "horaEntrada" to txtHoraEntrada.text.toString(),
            "horaSalida" to txtHoraSalida.text.toString(),
            "automatico" to switchAutomatico.isChecked,
            "activo" to switchActivo.isChecked,
            "feriadosActivos" to switchFeriados.isChecked,
            "lunes" to checkLunes.isChecked,
            "martes" to checkMartes.isChecked,
            "miercoles" to checkMiercoles.isChecked,
            "jueves" to checkJueves.isChecked,
            "viernes" to checkViernes.isChecked,
            "sabado" to checkSabado.isChecked,
            "domingo" to checkDomingo.isChecked,
            "tenant" to tenant,
            "user" to user
        )

        FirebaseManager
            .guardarConfiguracion(user, data,
                onSuccess = { Toast.makeText(requireContext(), "Guardado en Firebase", Toast.LENGTH_LONG).show() },
                onError = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                })

        val entrada = txtHoraEntrada.text.toString()
        val salida = txtHoraSalida.text.toString()
        val entradaSplit = entrada.split(":")
        val salidaSplit = salida.split(":")

        limpiarAlarmas()

        AlarmHelper.programarAlarma(requireContext(), entradaSplit[0].toInt(), entradaSplit[1].toInt(), 100)
        AlarmHelper.programarAlarma(requireContext(), salidaSplit[0].toInt(), salidaSplit[1].toInt(), 200)

        NotificationHelper.show(
            requireContext().applicationContext,
            "Asistencia automática",
            "La configuración se guardó correctamente. - Entrada: $entrada - Salida: $salida"
        )
        Toast.makeText(requireContext(), "Configuración guardada", Toast.LENGTH_LONG).show()
    }

    private fun cargarConfiguracion() {
        val prefs = requireContext().getSharedPreferences("config", Context.MODE_PRIVATE)
        txtHoraEntrada.text = prefs.getString("horaEntrada", "08:25")
        txtHoraSalida.text = prefs.getString("horaSalida", "18:35")
        switchAutomatico.isChecked = prefs.getBoolean("automatico", true)
        switchActivo.isChecked = prefs.getBoolean("activo", true)
        switchFeriados.isChecked = prefs.getBoolean("feriadosActivos", true)
        checkLunes.isChecked = prefs.getBoolean("lunes", true)
        checkMartes.isChecked = prefs.getBoolean("martes", true)
        checkMiercoles.isChecked = prefs.getBoolean("miercoles", true)
        checkJueves.isChecked = prefs.getBoolean("jueves", true)
        checkViernes.isChecked = prefs.getBoolean("viernes", true)
        checkSabado.isChecked = prefs.getBoolean("sabado", true)
        checkDomingo.isChecked = prefs.getBoolean("domingo", true)
    }

    private fun marcarManual() {
        btnMarcarAhora.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {

            val ok = AttendanceManager.marcarAsistencia()

            btnMarcarAhora.isEnabled = true

            if (ok) {
                Toast.makeText(requireContext(), "Asistencia marcada", Toast.LENGTH_LONG).show()
                NotificationHelper.show(
                    requireContext().applicationContext,
                    "Asistencia automática",
                    "Tu asistencia se registró correctamente - Manual"
                )
            } else {
                Toast.makeText(requireContext(), "Error al marcar", Toast.LENGTH_LONG).show()
                NotificationHelper.show(
                    requireContext().applicationContext,
                    "Asistencia automática",
                    "Error al registrar tu asistencia - Manual"
                )
            }
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

    private fun limpiarAlarmas() {
        val alarmManager = requireContext().getSystemService(ALARM_SERVICE) as AlarmManager

        listOf(100, 200).forEach { tipo ->

            val intent = Intent(requireContext(), AttendanceReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                tipo,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
        }
    }
}
