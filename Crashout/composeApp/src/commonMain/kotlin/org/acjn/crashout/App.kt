package org.acjn.crashout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


import crashout.composeapp.generated.resources.Res
import crashout.composeapp.generated.resources.compose_multiplatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    MaterialTheme {
        val scope = rememberCoroutineScope()
        val auth: FirebaseAuth = remember { Firebase.auth }
        var firebaseUser by remember { mutableStateOf<FirebaseUser?>(null) }
        var userEmail by remember { mutableStateOf("") }
        var userPassword by remember { mutableStateOf("") }
        var showMap by remember { mutableStateOf(false) }


        if (firebaseUser == null) {
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
                            } catch (e: Exception) {
                                firebaseUser = auth.signInWithEmailAndPassword(
                                    email = userEmail,
                                    password = userPassword
                                ).user
                            }
                        }
                    }
                ) {
                    Text(text = "Sign in")
                }
            }
        } else {
            if (showMap) {
                MapComponent()
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = firebaseUser?.uid ?: "Unknown ID")
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                auth.signOut()
                                firebaseUser = auth.currentUser
                            }
                        }
                    ) {
                        Text(text = "Sign out")
                    }
                    Button(
                        onClick = {
                            showMap = true
                        }
                    ) {
                        Text(text = "Map")
                    }
                }
            }
        }
    }
}
