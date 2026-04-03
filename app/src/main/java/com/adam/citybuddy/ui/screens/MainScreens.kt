package com.adam.citybuddy.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adam.citybuddy.ui.components.FloatingBubble
import com.adam.citybuddy.ml.TFLiteHelper
import com.google.firebase.auth.FirebaseAuth

val DeepPurple = Color(0xFF4A148C)
val ButtonPurple = Color(0xFF7B1FA2)
val LightPurple = Color(0xFFE1BEE7)
val SoftBg = Color(0xFFF8F4FF)

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        FloatingBubble(LightPurple, 200f, 5000, 0)
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("CityBuddy", color = DeepPurple, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(32.dp))
            Button(onClick = onStart, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Get Started")
            }
        }
    }
}

@Composable
fun AuthChoiceScreen(onLoginSelected: () -> Unit, onSignUpSelected: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center) {
        Button(onClick = onLoginSelected, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Login") }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onSignUpSelected, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Sign Up") }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center) {
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(32.dp))
        Button(onClick = {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { if (it.isSuccessful) onLoginSuccess() else Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show() }
        }, modifier = Modifier.fillMaxWidth()) { Text("Login") }
        TextButton(onClick = onBack) { Text("Back") }
    }
}

@Composable
fun SignUpScreen(onSignUpSuccess: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center) {
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onSignUpSuccess()
                            } else {
                                Toast.makeText(context, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }
        TextButton(onClick = onBack) { Text("Back") }
    }
}

@Composable
fun SupportHubScreen(onNav: (Int) -> Unit, onLogout: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center) {
        Button(onClick = { onNav(2) }, modifier = Modifier.fillMaxWidth()) { Text("Chat with Counselor") }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = { FirebaseAuth.getInstance().signOut(); onLogout() }) { Text("Logout") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(buddyName: String, onBack: () -> Unit) {
    var message by remember { mutableStateOf("") }
    Scaffold(
        topBar = { TopAppBar(title = { Text(buddyName) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "") } }) }
    ) { p ->
        Column(Modifier.padding(p).fillMaxSize()) {
            Spacer(Modifier.weight(1f))
            Row(Modifier.padding(16.dp)) {
                OutlinedTextField(value = message, onValueChange = { message = it }, modifier = Modifier.weight(1f))
                IconButton(onClick = { message = "" }) { Icon(Icons.AutoMirrored.Filled.Send, "", tint = DeepPurple) }
            }
        }
    }
}