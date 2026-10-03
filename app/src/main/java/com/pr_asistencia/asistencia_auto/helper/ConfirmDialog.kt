package com.pr_asistencia.asistencia_auto.helper

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object ConfirmDialog {

    fun marcarAhora(context: Context, onConfirmar: () -> Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Marcar asistencia")
            .setMessage("¿Deseas marcar tu asistencia ahora mismo?")
            .setPositiveButton("Marcar") { dialog, _ ->
                dialog.dismiss()
                onConfirmar()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    fun cerrarSesion(context: Context, onConfirmar: () -> Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de cerrar la sesión actual?")
            .setPositiveButton("Cerrar sesión") { dialog, _ ->
                dialog.dismiss()
                onConfirmar()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
