package com.pr_asistencia.asistencia_auto.activities.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pr_asistencia.asistencia_auto.R
import com.pr_asistencia.asistencia_auto.manager.AttendanceAllmanager
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Calendar

class AsistenciasFragment : Fragment() {

    private lateinit var dateFrom: EditText
    private lateinit var dateTo: EditText
    private lateinit var btnBuscar: Button
    private lateinit var tableAsistencia: TableLayout
    private var progressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateFrom = view.findViewById(R.id.datetime_inicio)
        dateTo = view.findViewById(R.id.datetime_fin)
        btnBuscar = view.findViewById(R.id.btn_filt)
        tableAsistencia = view.findViewById(R.id.table_lista)
        progressBar = view.findViewById(R.id.progressBar)

        establecerFechaActual()

        obtenerAsistencias()

        dateFrom.setOnClickListener { mostrarSelectorFecha(dateFrom) }

        dateTo.setOnClickListener { mostrarSelectorFecha(dateTo) }

        btnBuscar.setOnClickListener { obtenerAsistencias() }

        view.findViewById<Button>(R.id.btnVolver)?.setOnClickListener {
            requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottomNav)
                ?.selectedItemId = R.id.nav_home
        }
    }

    @SuppressLint("DefaultLocale")
    private fun establecerFechaActual() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val fechaActual = String.format("%04d-%02d-%02d", year, month, day)

        dateFrom.setText(fechaActual)
        dateTo.setText(fechaActual)
    }

    @SuppressLint("DefaultLocale")
    private fun mostrarSelectorFecha(editText: EditText) {
        val calendar = Calendar.getInstance()
        val fechaActualCampo = editText.text.toString()

        if (fechaActualCampo.length == 10) {
            try {
                val partes = fechaActualCampo.split("-")

                calendar.set(partes[0].toInt(), partes[1].toInt() - 1, partes[2].toInt())
            } catch (_: Exception) {
                // Si la fecha no se puede leer, usa la fecha actual
            }
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val fechaSeleccionada = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)

                editText.setText(fechaSeleccionada)
            },
            year,
            month,
            day
        ).show()
    }

    private fun obtenerAsistencias() {
        val fechaInicio = dateFrom.text.toString().trim()
        val fechaFin = dateTo.text.toString().trim()

        if (fechaInicio.isEmpty()) {
            dateFrom.error = "Ingrese fecha inicio"
            return
        }
        if (fechaFin.isEmpty()) {
            dateTo.error = "Ingrese fecha fin"
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            btnBuscar.isEnabled = false
            btnBuscar.text = "Buscando..."
            progressBar?.visibility = View.VISIBLE

            val response = AttendanceAllmanager.recuperarAsistencias(
                fechaInicio = fechaInicio,
                fechaFin = fechaFin
            )

            mostrarRespuestaEnTabla(response ?: "Sin respuesta")

            progressBar?.visibility = View.GONE
            btnBuscar.isEnabled = true
            btnBuscar.text = "Buscar"
        }
    }

    private fun mostrarRespuestaEnTabla(respuesta: String) {
        tableAsistencia.removeAllViews()

        try {
            agregarFilaTabla(
                "Nombre",
                "Apellidos",
                "Código",
                "Grupo",
                "Fecha",
                true
            )

            val json = JSONObject(respuesta)

            val contenedor = if (json.has("result")) {
                json.getJSONObject("result")
            } else {
                json
            }

            val items = contenedor.getJSONArray("items")

            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)

                val fechaCompleta = item.optString("issued", "")
                val fecha = formatearFecha(fechaCompleta)

                val employee = item.optJSONObject("employee")

                val nombre = employee?.optString("first_name", "")?.trim().orEmpty()
                val apellidos = employee?.optString("last_name", "")?.trim().orEmpty()
                val codigo = employee?.optString("code", "")?.trim().orEmpty()

                val group = employee?.optJSONObject("group")
                val grupo = group?.optString("name", "")?.trim().orEmpty()

                agregarFilaTabla(
                    nombre,
                    apellidos,
                    codigo,
                    grupo,
                    fecha,
                    false
                )
            }

            if (items.length() == 0) {
                agregarFilaTabla(
                    "Sin datos",
                    "",
                    "",
                    "",
                    "",
                    false
                )
            }
        } catch (e: Exception) {
            agregarFilaTabla(
                "Error al leer respuesta",
                e.message ?: "",
                "",
                "",
                "",
                false
            )
        }
    }

    private fun agregarFilaTabla(
        nombre: String,
        apellidos: String,
        codigo: String,
        grupo: String,
        fecha: String,
        esCabecera: Boolean
    ) {
        val fila = TableRow(requireContext())

        fila.addView(crearCelda(nombre, esCabecera))
        fila.addView(crearCelda(apellidos, esCabecera))
        fila.addView(crearCelda(codigo, esCabecera))
        fila.addView(crearCelda(grupo, esCabecera))
        fila.addView(crearCelda(fecha, esCabecera))

        tableAsistencia.addView(fila)
    }

    private fun crearCelda(
        texto: String,
        esCabecera: Boolean
    ): TextView {
        val celda = TextView(requireContext())
        celda.text = texto
        celda.setPadding(12, 12, 12, 12)
        celda.textSize = 12f

        if (esCabecera) {
            celda.setTypeface(null, Typeface.BOLD)
        }
        return celda
    }

    private fun formatearFecha(fechaCompleta: String): String {
        if (fechaCompleta.length < 10) {
            return fechaCompleta
        }
        val fecha = fechaCompleta.substring(0, 10)
        val hora = fechaCompleta.substring(11, 16)

        return "$fecha $hora"
    }
}
