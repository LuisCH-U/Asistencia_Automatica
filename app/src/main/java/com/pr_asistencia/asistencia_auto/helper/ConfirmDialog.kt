package com.pr_asistencia.asistencia_auto.helper

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pr_asistencia.asistencia_auto.R

object ConfirmDialog {

    fun marcarAhora(context: Context, onConfirmar: () -> Unit) {
        mostrar(
            context = context,
            icono = R.drawable.ic_check_circle,
            tinteIcono = R.color.brand_primary,
            fondoIcono = R.drawable.bg_icon_circle,
            titulo = "Marcar asistencia",
            mensaje = "¿Deseas marcar tu asistencia ahora mismo?",
            textoPositivo = "Marcar ahora",
            colorPositivo = R.color.brand_primary,
            colorTextoPositivo = R.color.brand_on_primary,
            onConfirmar = onConfirmar
        )
    }

    fun cerrarSesion(context: Context, onConfirmar: () -> Unit) {
        mostrar(
            context = context,
            icono = R.drawable.ic_logout,
            tinteIcono = R.color.brand_danger,
            fondoIcono = R.drawable.bg_icon_circle_danger,
            titulo = "Cerrar sesión",
            mensaje = "¿Estás seguro de cerrar la sesión actual? Las marcaciones automáticas se pausarán hasta que vuelvas a iniciar sesión.",
            textoPositivo = "Cerrar sesión",
            colorPositivo = R.color.brand_danger,
            colorTextoPositivo = R.color.brand_on_danger,
            onConfirmar = onConfirmar
        )
    }

    private fun mostrar(
        context: Context,
        icono: Int,
        tinteIcono: Int,
        fondoIcono: Int,
        titulo: String,
        mensaje: String,
        textoPositivo: String,
        colorPositivo: Int,
        colorTextoPositivo: Int,
        onConfirmar: () -> Unit
    ) {
        val vista = LayoutInflater.from(context).inflate(R.layout.dialog_confirmacion, null)

        val fondo = vista.findViewById<android.view.View>(R.id.dialogoFondoIcono)
        fondo.setBackgroundResource(fondoIcono)

        val imagenIcono = vista.findViewById<ImageView>(R.id.dialogoIcono)
        imagenIcono.setImageResource(icono)
        imagenIcono.setColorFilter(context.getColor(tinteIcono))

        vista.findViewById<TextView>(R.id.dialogoTitulo).text = titulo
        vista.findViewById<TextView>(R.id.dialogoMensaje).text = mensaje

        val botonPositivo = vista.findViewById<MaterialButton>(R.id.dialogoPositivo)
        botonPositivo.text = textoPositivo
        botonPositivo.backgroundTintList = android.content.res.ColorStateList.valueOf(context.getColor(colorPositivo))
        botonPositivo.setTextColor(context.getColor(colorTextoPositivo))

        val botonNegativo = vista.findViewById<TextView>(R.id.dialogoNegativo)

        val dialog = MaterialAlertDialogBuilder(context)
            .setView(vista)
            .create()

        botonPositivo.setOnClickListener {
            dialog.dismiss()
            onConfirmar()
        }

        botonNegativo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
