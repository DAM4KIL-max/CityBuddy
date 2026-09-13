package com.adam.citybuddy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.adam.citybuddy.data.JournalEntry
import com.adam.citybuddy.data.JournalRepository
import java.util.UUID

@Composable
fun JournalScreen(
    onNewEntry: () -> Unit,
    onOpenEntry: (String) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { JournalRepository(context) }
    var entries by remember { mutableStateOf(listOf<JournalEntry>()) }

    // Reload entries every time the screen appears
    LaunchedEffect(Unit) {
        entries = repository.getAllEntries()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNewEntry) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Text("Private Journal", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(entries) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onOpenEntry(entry.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(entry.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            entry.content,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JournalEditorScreen(
    entryId: String?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { JournalRepository(context) }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    LaunchedEffect(entryId) {
        if (entryId != null) {
            val existing = repository.getEntry(entryId)
            if (existing != null) {
                title = existing.title
                content = existing.content
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onNavigateBack) { Text("Cancel") }
            Button(onClick = {
                val newEntry = JournalEntry(
                    id = entryId ?: UUID.randomUUID().toString(),
                    title = title.ifBlank { "Untitled" },
                    content = content,
                    timestamp = System.currentTimeMillis()
                )
                repository.saveEntry(newEntry)
                onNavigateBack()
            }) { Text("Save") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("How are you feeling today?") },
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
    }
}