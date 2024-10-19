package com.example.app_firebase

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_firebase.ui.theme.AppfirebaseTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppfirebaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(db)
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun App(db: FirebaseFirestore) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }

    val clientes = remember { mutableStateListOf<Map<String, String>>() }

    fun fetchClientes() {
        db.collection("Clientes")
            .get()
            .addOnSuccessListener { documents ->
                clientes.clear()
                for (document in documents) {
                    val cliente = mapOf(
                        "nome" to (document.getString("nome") ?: "--"),
                        "telefone" to (document.getString("telefone") ?: "--")
                    )
                    clientes.add(cliente)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    Column(
        Modifier.fillMaxWidth()
    ) {
        Row(Modifier.fillMaxWidth().padding(20.dp)) {}
        Row(Modifier.fillMaxWidth(), Arrangement.Center) {
            Text(text = "App Firebase Firestore")
        }
        Row(Modifier.fillMaxWidth().padding(20.dp)) {}
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(0.3f)) {
                Text(text = "Nome:")
            }
            Column {
                TextField(
                    value = nome,
                    onValueChange = { nome = it },
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(0.3f)) {
                Text(text = "Telefone:")
            }
            Column {
                TextField(
                    value = telefone,
                    onValueChange = { telefone = it },
                )
            }
        }
        Row(Modifier.fillMaxWidth().padding(20.dp)) {}
        Row(Modifier.fillMaxWidth(), Arrangement.Center) {
            Button(onClick = {
                val pessoa = hashMapOf(
                    "nome" to nome,
                    "telefone" to telefone
                )

                db.collection("Clientes")
                    .add(pessoa)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        fetchClientes()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }) {
                Text(text = "Cadastrar")
            }
        }

        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(0.5f)) {
                Text(text = "Nome:")
            }
            Column(Modifier.fillMaxWidth(0.5f)) {
                Text(text = "Telefone:")
            }
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(clientes) { cliente ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(0.5f)) {
                        Text(text = cliente["nome"] ?: "--")
                    }
                    Column(modifier = Modifier.weight(0.5f)) {
                        Text(text = cliente["telefone"] ?: "--")
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchClientes()
    }
}

