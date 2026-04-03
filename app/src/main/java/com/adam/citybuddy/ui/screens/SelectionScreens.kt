package com.adam.citybuddy.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.adam.citybuddy.ui.theme.*

// 1. Data Class
data class PRSUser(
    val name: String,
    val status: String,
    val initial: String,
    val language: String,
    val imageUrl: String,
    val bio: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PRSSelectionScreen(onBack: () -> Unit, onSelectPRS: (String) -> Unit) {
    var selectedLanguage by remember { mutableStateOf("All") }

    val prsList = listOf(
        PRSUser("Aidil", "Online", "A", "Malay", "https://i.pravatar.cc/150?u=aidil", "Expert in exam stress."),
        PRSUser("Naqeeb", "Busy", "N", "Malay", "https://i.pravatar.cc/150?u=naqeeb", "Here for a quick talk."),
        PRSUser("Aamily", "Online", "A", "English", "https://i.pravatar.cc/150?u=aamily", "Let's work through it."),
        PRSUser("Azib", "Away", "A", "English", "https://i.pravatar.cc/150?u=azib", "Available after 5PM.")
    )

    val filteredList = if (selectedLanguage == "All") prsList else prsList.filter { it.language == selectedLanguage }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Peer Supporters", fontWeight = FontWeight.ExtraBold, color = DeepPurple) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepPurple)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = SoftBg)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(SoftBg)) {

            // Language Filter
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("All", "Malay", "English").forEach { lang ->
                    val isSelected = selectedLanguage == lang
                    Surface(
                        modifier = Modifier.clickable { selectedLanguage = lang },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) ButtonPurple else Color.White,
                        border = BorderStroke(1.dp, LightPurple),
                        shadowElevation = if (isSelected) 4.dp else 0.dp
                    ) {
                        Text(
                            text = lang,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                            color = if (isSelected) Color.White else DeepPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredList) { prs ->
                    PRSCard(prs) { onSelectPRS(prs.name) }
                }
            }
        }
    }
}

@Composable
fun PRSCard(prs: PRSUser, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                SubcomposeAsyncImage(
                    model = prs.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(65.dp)
                        .clip(CircleShape)
                        .border(2.dp, LightPurple, CircleShape),
                    contentScale = ContentScale.Crop,
                    loading = { Box(Modifier.fillMaxSize().background(LightPurple)) }
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                        .background(Color.White, CircleShape)
                        .padding(2.dp)
                ) {
                    Box(
                        Modifier.fillMaxSize().background(
                            if (prs.status == "Online") Color(0xFF4CAF50) else Color.LightGray,
                            CircleShape
                        )
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(prs.name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = DeepPurple)
                Text(prs.bio, color = Color.Gray, fontSize = 12.sp, lineHeight = 16.sp)

                Surface(
                    color = LightPurple.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        prs.language,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        color = ButtonPurple,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                tint = ButtonPurple,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounselorSelectionScreen(onBack: () -> Unit) {
    val counselors = listOf(
        Triple("Sir Firdaus", "Stress & Anxiety", "10+ Years Exp"),
        Triple("Madam Wani", "Family & Relationships", "Available Today"),
        Triple("Mr Zill", "Study Motivation", "Specialist")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Professional Help", fontWeight = FontWeight.Bold, color = DeepPurple) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepPurple)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(SoftBg)) {
            Text(
                "Book a private session with our certified counselors.",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray,
                fontSize = 14.sp
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(counselors) { (name, spec, tag) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(50.dp).background(
                                        Brush.linearGradient(listOf(LightPurple, ButtonPurple)),
                                        CircleShape
                                    ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(name.takeLast(1), color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DeepPurple)
                                    Text(spec, color = ButtonPurple, fontSize = 14.sp)
                                }
                            }
                            HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = LightPurple)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(tag, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Button(
                                    onClick = { /* Book */ },
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepPurple),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Book Now")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}