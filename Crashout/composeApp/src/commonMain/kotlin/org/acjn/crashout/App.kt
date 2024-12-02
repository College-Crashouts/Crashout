package org.acjn.crashout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
        var showAccountInfo by remember { mutableStateOf(false) }

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
            when {
                showMap -> {
                    MapComponent()
                }
                showAccountInfo -> {
                    // Account Info Screen
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Profile Section
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            // Placeholder for profile image
                            Surface(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colors.primary.copy(alpha = 0.1f)
                            ) {
                                // You can add an actual image here later
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Name Section
                        Text(
                            text = "Name:",
                            style = MaterialTheme.typography.subtitle1
                        )
                        Text(
                            text = firebaseUser?.displayName ?: "Unknown",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email Section
                        Text(
                            text = "Email:",
                            style = MaterialTheme.typography.subtitle1
                        )
                        Text(
                            text = firebaseUser?.email ?: "Unknown",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Section
                        Text(
                            text = "Password:",
                            style = MaterialTheme.typography.subtitle1
                        )
                        TextButton(
                            onClick = { /* Implement password view */ },
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text("View Password")
                        }
                        TextButton(
                            onClick = { /* Implement password change */ },
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text("Change Password")
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Bottom Navigation
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(onClick = { showMap = false; showAccountInfo = false }) {
                                Icon(Icons.Filled.Home, "Home")
                            }
                            IconButton(onClick = { /* Implement target navigation */ }) {
                                //Icon(Icons.Filled.RadioButtonChecked, "Target")
                            }
                            IconButton(onClick = { showMap = true; showAccountInfo = false }) {
                                Icon(Icons.Filled.PlayArrow, "Play")
                            }
                            IconButton(onClick = { showMap = false; showAccountInfo = true }) {
                                Icon(Icons.Filled.Person, "Profile")
                            }
                        }
                    }
                }
                else -> {
                    // Home Screen
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
                        Button(
                            onClick = {
                                showAccountInfo = true
                            }
                        ) {
                            Text(text = "Account Info")
                        }
                    }
                }
            }
        }
    }
}