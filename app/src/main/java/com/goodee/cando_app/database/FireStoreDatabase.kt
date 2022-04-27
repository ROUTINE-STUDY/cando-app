package com.goodee.cando_app.database

import com.google.firebase.firestore.FirebaseFirestore

object FireStoreDatabase {
    private val database = FirebaseFirestore.getInstance()
    fun getDatabase() : FirebaseFirestore {
        return database
    }
}