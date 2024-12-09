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
import org.jetbrains.compose.ui.tooling.preview.Preview

import crashout.composeapp.generated.resources.Res
import crashout.composeapp.generated.resources.compose_multiplatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton

@Composable
@Preview
fun App() {
    var authReady by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val auth: FirebaseAuth = remember { Firebase.auth }
    var firebaseUser by remember { mutableStateOf<FirebaseUser?>(null) }
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var showMap by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        GoogleAuthProvider.create(
            credentials = GoogleAuthCredentials(
                serverId = "WEB_CLIENT_ID"
            )
        )
        authReady = true
    }

    MaterialTheme {
        if (firebaseUser == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Email/Password Sign-In Fields
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
                Spacer(modifier = Modifier.height(16.dp))
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
                    Text(text = "Sign in with Email")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Google Sign-In Button
                if (authReady) {
                    GoogleButtonUiContainer(
                        onGoogleSignInResult = { googleUser ->
                            val tokenId = googleUser?.idToken
                            println("TOKEN: $tokenId")
                            // Here, you should handle the token and sign in with Firebase
                        }
                    ) {
                        GoogleSignInButton(
                            onClick = { this.onClick() }
                        )
                    }
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