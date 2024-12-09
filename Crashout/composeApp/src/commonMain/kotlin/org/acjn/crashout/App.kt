package org.acjn.crashout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.filled.LocationOn

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
                    }                ) {
                    Text(text = "Sign in")
                }
            }        } else {
            when {
                showMap -> {
                    MapComponent()
                }
                showAccountInfo -> {
                    //Account Info Screen
                    var showChangeNameDialog by remember { mutableStateOf(false) }
                    var newDisplayName by remember { mutableStateOf("") }
                    var showChangePasswordDialog by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        //Prof image
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.compose_multiplatform),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        //Name
                        Text(text = "Name:", style = MaterialTheme.typography.body1)
                        Text(
                            text = firebaseUser?.displayName ?: "Unknown",
                            style = MaterialTheme.typography.h6,
                            color = Color(0xFFE91E63)
                        )
                        TextButton(
                            onClick = { showChangeNameDialog = true },
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text("Change Name")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        //Email
                        Text(text = "Email:", style = MaterialTheme.typography.body1)
                        Text(
                            text = firebaseUser?.email ?: "Unknown",
                            style = MaterialTheme.typography.h6,
                            color = Color(0xFFE91E63)
                        )

                        //Name change dialog
                        if (showChangeNameDialog) {
                            AlertDialog(
                                onDismissRequest = { showChangeNameDialog = false },
                                title = { Text("Change Display Name") },
                                text = {
                                    TextField(
                                        value = newDisplayName,
                                        onValueChange = { newDisplayName = it },
                                        placeholder = { Text("New Display Name") }
                                    )
                                },
                                confirmButton = {
                                    Button(onClick = {
                                        scope.launch {
                                            try {
                                                firebaseUser?.updateProfile(displayName = newDisplayName)
                                                showChangeNameDialog = false
                                                newDisplayName = ""
                                            } catch (e: Exception) { }
                                        }
                                    }) {
                                        Text("Update")
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { showChangeNameDialog = false }) {
                                        Text("Cancel")
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        //Bottom navigation
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Icon(Icons.Filled.Home, "Home",
                                modifier = Modifier.clickable {
                                    showMap = false; showAccountInfo = false
                                })
                            Icon(Icons.Filled.LocationOn, "Target",
                                modifier = Modifier.clickable { /* target navigation */ })
                            Icon(Icons.Filled.PlayArrow, "Play",
                                modifier = Modifier.clickable {
                                    showMap = true; showAccountInfo = false
                                })
                            Icon(Icons.Filled.Person, "Profile",
                                modifier = Modifier.clickable {
                                    showMap = false; showAccountInfo = true
                                })
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
                            }                        ) {
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
                    }                }
            }
        }
    }
}