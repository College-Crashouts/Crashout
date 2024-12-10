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
        var userEmail by remember { mutableStateOf(userName) }
        var userPassword by remember { mutableStateOf(password) }
        var showPassword by remember { mutableStateOf(false) }
        var newPassword by remember { mutableStateOf("") }
        var showChangePasswordDialog by remember { mutableStateOf(false) }
        var showMap by remember { mutableStateOf(false) }

        Scaffold(
            bottomBar = {
                BottomAppBar(
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        Icons.Filled.Home, "Home", // NAV TO HOME
                        modifier = Modifier
                            .weight(1f)
                            .clickable { /* target navigation */ })
                    Icon(
                        Icons.Filled.LocationOn, "Target", // NAV TO MAP
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showMap = true })
                    Icon(
                        Icons.Filled.Person, "Profile", // NAV TO PROFILE
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showMap = false }
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
                } else {
                    // Profile
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.compose_multiplatform),  // replace this with profile image resource
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Name
                    Text(text = "Name:", style = MaterialTheme.typography.body1)
                    Text(
                        text = firebaseUser?.displayName ?: "Unknown",
                        style = MaterialTheme.typography.h6,
                        color = Color(0xFFE91E63)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
                    Text(text = "Email:", style = MaterialTheme.typography.body1)
                    Text(
                        text = firebaseUser?.email?.let { email ->
                            val username = email.substringBefore('@')
                            "******@${email.substringAfter('@')}"
                        } ?: "Unknown",
                        style = MaterialTheme.typography.h6,
                        color = Color(0xFFE91E63)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Section
                    Text(text = "Password:", style = MaterialTheme.typography.body1)
                    TextButton(
                        onClick = { showPassword = !showPassword },
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("View Password")
                    }
                    TextButton(
                        onClick = { showChangePasswordDialog = true },
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("Change Password")
                    }

                    // Password change dialog
                    if (showChangePasswordDialog) {
                        AlertDialog(
                            onDismissRequest = { showChangePasswordDialog = false },
                            title = { Text("Change Password") },
                            text = {
                                TextField(
                                    value = newPassword,
                                    onValueChange = { newPassword = it },
                                    placeholder = { Text("New Password") },
                                    visualTransformation = PasswordVisualTransformation()
                                )
                            },
                            confirmButton = {
                                Button(onClick = {
                                    scope.launch {
                                        try {
                                            firebaseUser?.updatePassword(newPassword)
                                            showChangePasswordDialog = false
                                            newPassword = ""
                                        } catch (e: Exception) { }
                                    }
                                }) {
                                    Text("Update")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showChangePasswordDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}