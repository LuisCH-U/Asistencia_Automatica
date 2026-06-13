package com.pr_asistencia.asistencia_auto

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Build
import android.content.Intent
import android.widget.*
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pr_asistencia.asistencia_auto.manager.AttendanceManager
import kotlinx.coroutines.launch
import java.util.*
import com.pr_asistencia.asistencia_auto.firebase.FirebaseManager
import com.pr_asistencia.asistencia_auto.helper.AlarmHelper
import androidx.core.content.edit
import com.pr_asistencia.asistencia_auto.activities.ListActivity
import com.pr_asistencia.asistencia_auto.helper.NotificationHelper

@SuppressLint("UseSwitchCompatOrMaterialCode")
class HomeActivity : AppCompatActivity() {

    private lateinit var txtHoraEntrada: TextView
    private lateinit var txtHoraSalida: TextView
    private lateinit var switchAutomatico: Switch
    private lateinit var switchActivo: Switch
    private lateinit var btnGuardar: Button
    private lateinit var btnMarcarAhora: Button
    private lateinit var btnVerAsistencias: Button
    private lateinit var btnCerrarSesion: Button
    private lateinit var checkLunes: CheckBox
    private lateinit var checkMartes: CheckBox
    private lateinit var checkMiercoles: CheckBox
    private lateinit var checkJueves: CheckBox
    private lateinit var checkViernes: CheckBox
    private lateinit var checkSabado: CheckBox
    private lateinit var checkDomingo: CheckBox


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        txtHoraEntrada = findViewById(R.id.txtHoraEntrada)
        txtHoraSalida = findViewById(R.id.txtHoraSalida)

        switchAutomatico = findViewById(R.id.switchAutomatico)
        switchActivo = findViewById(R.id.switchActivo)

        checkLunes = findViewById(R.id.checkLunes)
        checkMartes = findViewById(R.id.checkMartes)
        checkMiercoles = findViewById(R.id.checkMiercoles)
        checkJueves = findViewById(R.id.checkJueves)
        checkViernes = findViewById(R.id.checkViernes)
        checkSabado = findViewById(R.id.checkSabado)
        checkDomingo = findViewById(R.id.checkDomingo)

        btnGuardar = findViewById(R.id.btnGuardar)
        btnMarcarAhora = findViewById(R.id.btnMarcarAhora)
        btnVerAsistencias = findViewById(R.id.btnVerAsistencia)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        txtHoraEntrada.setOnClickListener { seleccionarHora(txtHoraEntrada) }
        txtHoraSalida.setOnClickListener { seleccionarHora(txtHoraSalida) }

        btnGuardar.setOnClickListener { guardarConfiguracion() }
        btnMarcarAhora.setOnClickListener { marcarManual() }

        btnVerAsistencias.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }

        btnCerrarSesion.setOnClickListener { cerrarSesion() }

        solicitarPermisoExactAlarm()

        cargarConfiguracion()
    }

    private fun solicitarPermisoExactAlarm()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

            if (!alarmManager.canScheduleExactAlarms()) {

                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                )

                startActivity(intent)
            }
        }

        //val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        //if (!alarmManager.canScheduleExactAlarms()) {
        //    val intent = Intent(
        //        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
        //    )
        //    startActivity(intent)
        //}
    }

    @SuppressLint("DefaultLocale")
    private fun seleccionarHora(textView: TextView)
    {

        val calendar = Calendar.getInstance()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            this,
            { _, h, m -> textView.text = String.format("%02d:%02d", h, m) },
            hour,
            minute,
            true
        ).show()
    }

    private fun guardarConfiguracion()
    {
        val prefs = getSharedPreferences("config", MODE_PRIVATE)

        prefs.edit {
            putString("horaEntrada",txtHoraEntrada.text.toString()
            ).putString("horaSalida",txtHoraSalida.text.toString()
            ).putBoolean("automatico",switchAutomatico.isChecked
            ).putBoolean("activo",switchActivo.isChecked
            ).putBoolean("lunes",checkLunes.isChecked
            ).putBoolean("martes",checkMartes.isChecked
            ).putBoolean("miercoles",checkMiercoles.isChecked
            ).putBoolean("jueves",checkJueves.isChecked
            ).putBoolean("viernes",checkViernes.isChecked
            ).putBoolean("sabado",checkSabado.isChecked
            ).putBoolean("domingo",checkDomingo.isChecked)
        }

        val securePrefs = App.instance.securePrefs()
        val user = securePrefs.getString("user","") ?: ""
        val password = securePrefs.getString("password", "") ?: ""

        val data = hashMapOf<String, Any>(
            "horaEntrada" to txtHoraEntrada.text.toString(),
            "horaSalida" to txtHoraSalida.text.toString(),
            "automatico" to switchAutomatico.isChecked,
            "activo" to switchActivo.isChecked,
            "lunes" to checkLunes.isChecked,
            "martes" to checkMartes.isChecked,
            "miercoles" to checkMiercoles.isChecked,
            "jueves" to checkJueves.isChecked,
            "viernes" to checkViernes.isChecked,
            "sabado" to checkSabado.isChecked,
            "domingo" to checkDomingo.isChecked,
            "tenant" to "inlearning",
            "user" to user,
            "password" to password
        )

        FirebaseManager
            .guardarConfiguracion(
                user,
                data,
                onSuccess = {

                    //val entrada = txtHoraEntrada.text.toString().split(":")

                    //val salida = txtHoraSalida.text.toString().split(":")

                    //WorkerScheduler.scheduleWorker(this,entrada[0].toInt(),entrada[1].toInt())

                    //WorkerScheduler.scheduleWorker(this,salida[0].toInt(),salida[1].toInt())

                    Toast.makeText(this, "Guardado en Firebase", Toast.LENGTH_LONG).show()
                },
                onError = {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
            )
        val entrada =  txtHoraEntrada.text.toString()

        val salida = txtHoraSalida.text.toString()

        val entradaSplit = entrada.split(":")

        val salidaSplit = salida.split(":")

        AlarmHelper.programarAlarma(this, entradaSplit[0].toInt(), entradaSplit[1].toInt(), 100)

        AlarmHelper.programarAlarma(this, salidaSplit[0].toInt(), salidaSplit[1].toInt(), 200)

        NotificationHelper.show(applicationContext,"Asistencia","Configuración guardada - Entrada: $entrada - Salida: $salida")
        Toast.makeText(this, "Configuración guardada", Toast.LENGTH_LONG).show()
    }

    private fun cargarConfiguracion()
    {
        val prefs = getSharedPreferences("config", MODE_PRIVATE)
        txtHoraEntrada.text = prefs.getString("horaEntrada", "08:25")
        txtHoraSalida.text = prefs.getString("horaSalida", "18:36")
        switchAutomatico.isChecked = prefs.getBoolean("automatico", true)
        switchActivo.isChecked = prefs.getBoolean("activo", true)
        checkLunes.isChecked = prefs.getBoolean("lunes", true)
        checkMartes.isChecked = prefs.getBoolean("martes", true)
        checkMiercoles.isChecked = prefs.getBoolean("miercoles", true)
        checkJueves.isChecked = prefs.getBoolean("jueves", true)
        checkViernes.isChecked = prefs.getBoolean("viernes", true)
        checkSabado.isChecked = prefs.getBoolean("sabado", true)
        checkDomingo.isChecked = prefs.getBoolean("domingo", true)
    }

    private fun marcarManual()
    {
        btnMarcarAhora.isEnabled = false

        lifecycleScope.launch {

            val ok = AttendanceManager.marcarAsistencia()

            btnMarcarAhora.isEnabled = true

            if (ok) {
                Toast.makeText(this@HomeActivity, "Asistencia marcada", Toast.LENGTH_LONG).show()
                NotificationHelper.show(applicationContext,"Asistencia","Asistencia marcada - Manual")
            } else {
                Toast.makeText(this@HomeActivity, "Error al marcar", Toast.LENGTH_LONG).show()
                NotificationHelper.show(applicationContext,"Asistencia","Error al marcar - Manual")
            }
        }
    }

    private fun cerrarSesion() {

        val prefs = App.instance.securePrefs()

        prefs.edit().clear().apply()

        finish()
    }

    /*
    private fun iniciarWorker() {

        val workRequest = PeriodicWorkRequestBuilder<AttendanceWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("attendanceWorker", ExistingPeriodicWorkPolicy.UPDATE, workRequest)
    }
    */
}