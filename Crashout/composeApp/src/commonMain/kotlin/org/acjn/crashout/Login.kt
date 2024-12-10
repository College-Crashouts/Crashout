package org.acjn.crashout

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@Composable
@Preview
fun Login() {
    MaterialTheme {
        val scope = rememberCoroutineScope()
        val auth: FirebaseAuth = remember { Firebase.auth }
        var firebaseUser by remember { mutableStateOf<FirebaseUser?>(null) }
        var userEmail by remember { mutableStateOf("") }
        var userPassword by remember { mutableStateOf("") }
        var showMap by remember { mutableStateOf(false) }
        var showProfile by remember { mutableStateOf(false) }

        if (firebaseUser == null) {
            // Login Screen
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = userEmail,
                    onValueChange = { userEmail = it },
                    placeholder = { Text(text = "Email") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = userPassword,
                    onValueChange = { userPassword = it },
                    placeholder = { Text(text = "Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                firebaseUser = auth.createUserWithEmailAndPassword(
                                    email = userEmail,
                                    password = userPassword
                                ).user
                                showProfile = true
                            } catch (e: Exception) {
                                firebaseUser = auth.signInWithEmailAndPassword(
                                    email = userEmail,
                                    password = userPassword
                                ).user
                                showProfile = true
                            }
                        }
                    }
                ) {
                    Text(text = "Sign in")
                }
            }
        } else {
            when {
                showProfile -> {
                    Profile(userEmail, userPassword) //maybe want to change first screen after login to map?
                }
            }
        }
    }
}