package com.adam.citybuddy.ui.screens


import com.adam.citybuddy.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adam.citybuddy.ui.theme.*

// ── Data ──────────────────────────────────────────────────────────────────────
data class SupportPerson(
    val name: String,
    val role: String,
    val emoji: String,
    val specialty: String,
    val availability: String,
    val accentColor: Color,
    val email: String,
    val imageRes: Int,
    val language: String // Added for filtering
)

val counselors = listOf(
    SupportPerson("Sir Firdaus", "School Counselor", "👨‍💼", "Personal Development", "9am–4pm", Color(0xFF00897B), "firsaufi@ukm.edu.my", R.drawable.firdaus_img, "Both"),
    SupportPerson("Madam Wani", "Senior Counselor", "👩‍💼", "Anxiety & Academic Stress", "8am–5pm", Color(0xFF5C6BC0), "nursyazwani@ukm.edu.my", R.drawable.wani_img, "Both")
)

val prsList = listOf(
    SupportPerson("Aidil", "PRS Student", "🙋‍♂️", "Social Wellbeing", "After school", Color(0xFF1E88E5), "m-gpm001809@moe-dl.edu.my", R.drawable.aidil_img, "English"),
    SupportPerson("Naqeeb", "PRS Student", "🙋‍♂️", "Peer Listener", "Flexible", Color(0xFFEF6C00), "m-gpm001789@moe-dl.edu.my", R.drawable.naqeeb_img, "Malay"),
    SupportPerson("Azib", "PRS Student", "🙋‍♂️", "Academic Peer", "Evening", Color(0xFF43A047), "m-gpm001746@moe-dl.edu.my", R.drawable.azib_img, "English"),
    SupportPerson("Aamily", "PRS Student", "🙋‍♀️", "Mental Health Aid", "Lunchtime", Color(0xFFD81B60), "m001723@permatapintar.ukm.edu.my", R.drawable.aamily_img, "English"),
    SupportPerson("Qaisy", "PRS Student", "🙋‍♀️", "Peer Support", "Flexible", Color(0xFF8E24AA), "qaiysinsyirah@gmail.com", R.drawable.qaisy_img, "Malay"),
    SupportPerson("Ain", "PRS Student", "🙋‍♀️", "Social Support", "After school", Color(0xFFF4511E), "nurhafiya.hamidi@gmail.com", R.drawable.ain_img, "Malay")
)

// ── Match Result Screen ───────────────────────────────────────────────────────
// This is the NEW screen shown after the survey
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchResultScreen(
    matchedName: String,      // e.g. "Madam Wani (Counselor)"
    onGoToHub: () -> Unit,
    onBook: (SupportPerson) -> Unit
) {
    // Find the matched person from our lists
    val allPeople = counselors + prsList
    val matched = allPeople.firstOrNull { matchedName.contains(it.name, ignoreCase = true) }
        ?: counselors.first() // fallback to Madam Wani

    var visible by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f, label = "glow",
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOutSine), RepeatMode.Reverse)
    )
    val emojiScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.08f, label = "emoji",
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse)
    )

    LaunchedEffect(Unit) { visible = true }

    Box(
        Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0D001F), Color(0xFF1A0035), DeepPurple)))
    ) {
        // Glowing orb behind match card
        Box(
            Modifier
                .size(280.dp)
                .align(Alignment.Center)
                .offset(y = (-40).dp)
                .background(matched.accentColor.copy(alpha = glowAlpha * 0.3f), CircleShape)
                .blur(60.dp)
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { it / 3 }
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header
                Text("✨ Your Match", color = AccentGold, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "CityBuddy found\nthe right person for you",
                    color = Color.White.copy(0.8f),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )

                Spacer(Modifier.height(40.dp))

                // Match card
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.07f)),
                    border = BorderStroke(1.dp, Color.White.copy(0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar orb
                        Box(
                            Modifier
                                .size(100.dp)
                                .background(
                                    Brush.radialGradient(listOf(matched.accentColor.copy(0.6f), matched.accentColor.copy(0.1f))),
                                    CircleShape
                                )
                                .scale(emojiScale),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(matched.emoji, fontSize = 48.sp)
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(matched.name, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = matched.accentColor.copy(0.25f)
                        ) {
                            Text(
                                matched.role,
                                color = matched.accentColor.copy(alpha = 1f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                            )
                        }

                        Spacer(Modifier.height(20.dp))
                        HorizontalDivider(color = Color.White.copy(0.1f))
                        Spacer(Modifier.height(20.dp))

                        // Details
                        MatchDetailRow(icon = "🎯", label = "Specialises in", value = matched.specialty)
                        Spacer(Modifier.height(12.dp))
                        MatchDetailRow(icon = "🕐", label = "Available", value = matched.availability)
                    }
                }

                Spacer(Modifier.height(28.dp))

                // Book button
                val context = LocalContext.current // Get the context

                Button(
                    onClick = { sendEmail(context, matched) }, // <--- Call the new function
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Email, null, tint = DeepPurple) // Changed icon to Email
                    Spacer(Modifier.width(8.dp))
                    Text("Email for Appointment", color = DeepPurple, fontWeight = FontWeight.ExtraBold)
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onGoToHub,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    border = BorderStroke(1.dp, Color.White.copy(0.3f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Go to Support Hub", color = Color.White.copy(0.8f), fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun MatchDetailRow(icon: String, label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, color = Color.White.copy(0.5f), fontSize = 12.sp)
            Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ── Booking Confirmation Screen ───────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(person: SupportPerson, onBack: () -> Unit) {
    val context = LocalContext.current
    var booked by remember { mutableStateOf(false) }
    val scaleAnim by animateFloatAsState(
        if (booked) 1f else 0f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "checkScale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book a Meeting", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepPurple)
            )
        },
        containerColor = SoftBg
    ) { padding ->
        if (booked) {
            // Success state
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Box(
                        Modifier
                            .size(100.dp)
                            .scale(scaleAnim)
                            .background(Color(0xFF4CAF50), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(56.dp))
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("Booking Confirmed!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = DeepPurple)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Your meeting with ${person.name} has been requested. They will contact you shortly.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 15.sp
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepPurple),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) { Text("Done", fontWeight = FontWeight.Bold) }
                }
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Person summary card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(60.dp)
                                .background(person.accentColor.copy(0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(person.emoji, fontSize = 28.sp)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(person.name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = DeepPurple)
                            Text(person.role, color = Color.Gray, fontSize = 13.sp)
                            Text(person.availability, color = person.accentColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text("Meeting Details", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepPurple)
                Spacer(Modifier.height(12.dp))

                // Info cards
                listOf(
                    Triple("📍", "Location", "Counseling Room, Block B"),
                    Triple("📅", "When",     "Next available slot"),
                    Triple("⏱️", "Duration", "30–45 minutes"),
                    Triple("🔒", "Privacy",  "All sessions are confidential"),
                ).forEach { (icon, label, value) ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(icon, fontSize = 20.sp)
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text(label, color = Color.Gray, fontSize = 12.sp)
                                Text(value, fontWeight = FontWeight.SemiBold, color = Color.Black, fontSize = 14.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { booked = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepPurple),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.CalendarMonth, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Confirm Booking", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

// ── Counselor Selection ───────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounselorSelectionScreen(onBack: () -> Unit) {
    var selectedPerson by remember { mutableStateOf<SupportPerson?>(null) }

    if (selectedPerson != null) {
        BookingScreen(person = selectedPerson!!, onBack = { selectedPerson = null })
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Counselors", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepPurple)
            )
        },
        containerColor = SoftBg
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Professional counselors are here to help with any personal, academic or emotional challenges.",
                color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(4.dp)
            )
            counselors.forEach { person ->
                PersonCard(person = person, onBook = { selectedPerson = person })
            }
        }
    }
}

// ── PRS Selection ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PRSSelectionScreen(onBack: () -> Unit, onSelectPRS: (String) -> Unit) {
    var selectedLanguage by remember { mutableStateOf("All") }
    var selectedPerson by remember { mutableStateOf<SupportPerson?>(null) }

    if (selectedPerson != null) {
        BookingScreen(person = selectedPerson!!, onBack = { selectedPerson = null })
        return
    }

    // Filter logic
    val filteredList = if (selectedLanguage == "All") {
        prsList
    } else {
        prsList.filter { it.language == selectedLanguage }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PRS Students", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepPurple)
            )
        },
        containerColor = SoftBg
    ) { padding ->
        Column(Modifier.padding(padding)) {
            // Filter Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "English", "Malay").forEach { lang ->
                    FilterChip(
                        selected = selectedLanguage == lang,
                        onClick = { selectedLanguage = lang },
                        label = { Text(lang) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepPurple,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Showing ${if(selectedLanguage == "All") "all" else selectedLanguage} speaking peer supporters.",
                    color = Color.Gray, fontSize = 13.sp
                )

                filteredList.forEach { person ->
                    PersonCard(person = person, onBook = { selectedPerson = person })
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
// ── Shared Person Card ────────────────────────────────────────────────────────
@Composable
fun PersonCard(person: SupportPerson, onBook: () -> Unit) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Profile Picture / Avatar Orb
                Box(
                    Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(person.accentColor.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = person.imageRes),
                        contentDescription = person.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = person.name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = DeepPurple
                    )
                    Text(
                        text = person.role,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    Spacer(Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎯", fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = person.specialty,
                            color = Color.DarkGray,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🕐", fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = person.availability,
                            color = Color.DarkGray,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { sendEmail(context, person) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = person.accentColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Send Email", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
fun sendEmail(context: Context, person: SupportPerson) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(person.email))
        putExtra(Intent.EXTRA_SUBJECT, "CityBuddy Support: ${person.name}")
        putExtra(Intent.EXTRA_TEXT, "Hello ${person.name},\n\nI'd like to book a session regarding ${person.specialty}.")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
    }
}