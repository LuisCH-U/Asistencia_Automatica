package com.pr_asistencia.asistencia_auto.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import com.pr_asistencia.asistencia_auto.R
import com.pr_asistencia.asistencia_auto.manager.ProfileManager
import com.pr_asistencia.asistencia_auto.models.Profile
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var vistaCargando: LinearLayout
    private lateinit var vistaError: View
    private lateinit var vistaContenido: LinearLayout
    private lateinit var textoErrorMensaje: TextView
    private lateinit var avatarIniciales: TextView
    private lateinit var avatarFoto: ShapeableImageView
    private lateinit var textoNombre: TextView
    private lateinit var textoRol: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vistaCargando = view.findViewById(R.id.perfilCargando)
        vistaError = view.findViewById(R.id.perfilError)
        vistaContenido = view.findViewById(R.id.perfilContenido)
        textoErrorMensaje = view.findViewById(R.id.perfilErrorMensaje)
        avatarIniciales = view.findViewById(R.id.avatarIniciales)
        avatarFoto = view.findViewById(R.id.avatarFoto)
        textoNombre = view.findViewById(R.id.perfilNombre)
        textoRol = view.findViewById(R.id.perfilRol)

        view.findViewById<Button>(R.id.btnReintentar)?.setOnClickListener {
            cargarPerfil()
        }

        cargarPerfil()
    }

    private fun cargarPerfil() {
        viewLifecycleOwner.lifecycleScope.launch {
            mostrarCargando()

            val respuesta = ProfileManager.obtenerPerfil()
            val perfil = respuesta?.let { Profile.desdeJson(it) }

            if (perfil == null) {
                mostrarError(respuesta ?: "Sin respuesta del servidor")
            } else {
                mostrarPerfil(perfil)
            }
        }
    }

    private fun mostrarCargando() {
        vistaCargando.visibility = View.VISIBLE
        vistaError.visibility = View.GONE
        vistaContenido.visibility = View.GONE
    }

    private fun mostrarError(mensaje: String) {
        vistaCargando.visibility = View.GONE
        vistaError.visibility = View.VISIBLE
        vistaContenido.visibility = View.GONE

        textoErrorMensaje.text = mensaje.ifBlank { "Intenta de nuevo en unos momentos" }
    }

    private fun mostrarPerfil(perfil: Profile) {
        vistaCargando.visibility = View.GONE
        vistaError.visibility = View.GONE
        vistaContenido.visibility = View.VISIBLE

        avatarIniciales.text = perfil.iniciales
        if (perfil.foto.isNotBlank()) {
            avatarFoto.load(perfil.foto) {
                crossfade(true)
            }
        }

        textoNombre.text = perfil.nombreCompleto.ifBlank { "Usuario" }
        textoRol.text = perfil.rolPrincipal

        configurarFila(R.id.filaCorreo, R.id.valorCorreo, perfil.correo)
        configurarFila(R.id.filaCodigo, R.id.valorCodigo, perfil.codigo)
        configurarFila(R.id.filaTelefono, R.id.valorTelefono, perfil.telefono)
        configurarFila(R.id.filaCiudad, R.id.valorCiudad, perfil.ciudad)
        configurarFila(R.id.filaPais, R.id.valorPais, perfil.pais)
        configurarFila(R.id.filaCumpleanhos, R.id.valorCumpleanhos, perfil.cumpleanhos)
    }

    private fun configurarFila(idFila: Int, idValor: Int, texto: String) {
        val fila = view?.findViewById<View>(idFila) ?: return
        val valor = fila.findViewById<TextView>(idValor)

        if (texto.isBlank()) {
            fila.visibility = View.GONE
        } else {
            fila.visibility = View.VISIBLE
            valor.text = texto
        }
    }
}
