package org.acjn.crashout

import android.annotation.SuppressLint
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
        var displayName by remember { mutableStateOf("") }
        var showChangeNameDialog by remember { mutableStateOf(false) }
        var newDisplayName by remember { mutableStateOf("") }
        var showMap by remember { mutableStateOf(true) }
        var showTarget by remember { mutableStateOf(false) }

        Scaffold(
            bottomBar = {
                BottomAppBar(
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        Icons.Filled.Star, "Home",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showTarget = true
                                showMap = false
                            })
                    Icon(
                        Icons.Filled.LocationOn, "Target",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showMap = true
                                showTarget = false
                            })
                    Icon(
                        Icons.Filled.Person, "Profile",
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
                } else if (showTarget){
                    kotlin.annotation.Target()
                }
                else {
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
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Name
                    Text(text = "Name:", style = MaterialTheme.typography.body1)
                    Text(
                        text = displayName.ifEmpty { userEmail.substringBefore('@') },
                        style = MaterialTheme.typography.h6,
                        color = Color(0xFFE91E63)
                    )
                    TextButton(
                        onClick = { showChangeNameDialog = true },
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("Change Display Name")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
                    Text(text = "Email:", style = MaterialTheme.typography.body1)
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.h6,
                        color = Color(0xFFE91E63)
                    )

                    // Name change dialog
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
                                            displayName = newDisplayName
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
                }
            }
        }
    }
}