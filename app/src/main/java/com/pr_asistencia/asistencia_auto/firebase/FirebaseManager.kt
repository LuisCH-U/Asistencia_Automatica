package com.pr_asistencia.asistencia_auto.firebase

import com.google.firebase.firestore.FirebaseFirestore

object FirebaseManager {

    private val db = FirebaseFirestore.getInstance()

    fun guardarConfiguracion(
        user: String,
        data: HashMap<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        db.collection("users")
            .document(user)
            .set(data)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(
                    it.message ?: "Error"
                )
            }
    }

    fun obtenerConfiguracion(
        user: String,
        onSuccess: (Map<String, Any>?) -> Unit,
        onError: (String) -> Unit
    ) {

        db.collection("users")
            .document(user)
            .get()
            .addOnSuccessListener {

                onSuccess(it.data)

            }
            .addOnFailureListener {

                onError(
                    it.message ?: "Error"
                )
            }
    }
}