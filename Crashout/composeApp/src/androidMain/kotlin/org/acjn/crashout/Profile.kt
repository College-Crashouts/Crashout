package org.acjn.crashout

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import crashout.composeapp.generated.resources.Res
import crashout.composeapp.generated.resources.compose_multiplatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
actual fun Profile(userName: String, password: String) {
    MaterialTheme {
        val scope = rememberCoroutineScope()
        val auth: FirebaseAuth = remember { Firebase.auth }
        var firebaseUser by remember { mutableStateOf<FirebaseUser?>(null) }
        var userEmail by remember { mutableStateOf(userName) } //PARAM FOR USER PW PULLED FROM LOGIN
        var userPassword by remember { mutableStateOf(password) } //NEW PARAM OF USER PW PULLED FROM LOGIN SCREEN
        var showChangeNameDialog by remember { mutableStateOf(false) }
        var newDisplayName by remember { mutableStateOf("") }
        // var showChangePasswordDialog by remember { mutableStateOf(false) }
        var showMap by remember { mutableStateOf(true) }
        var showTarget by remember { mutableStateOf(false) }

        Scaffold(
            bottomBar = {
                BottomAppBar(
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        Icons.Filled.Star, "Home", // NAV TO HOME
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showTarget = true
                                showMap = false
                            })
                    Icon(
                        Icons.Filled.LocationOn, "Target", // NAV TO MAP
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showMap = true
                                showTarget = false
                            })
                    Icon(
                        Icons.Filled.Person, "Profile", // NAV TO PROFILE
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showMap = false
                                showTarget = false
                            }
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(0.dp),
                horizontalAlignment = Alignment.Start
            ) {
                if (showMap) {
                    MapComponent()
                } else if (showTarget) {
                    kotlin.annotation.Target()
                } else {
                    // Profile
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
                        TextButton(
                            onClick = { showChangeNameDialog = true },
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text("Change Name")
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Name
                        Text(text = "Name:", style = MaterialTheme.typography.body1)
                        Text(
                            text = userEmail, //TODO FIX TO GIVE USER AN ACTUAL NAME OR REMOVE
                            style = MaterialTheme.typography.h6,
                            color = Color(0xFFE91E63)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email
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
                                            } catch (e: Exception) {
                                            }
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

                        //Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}