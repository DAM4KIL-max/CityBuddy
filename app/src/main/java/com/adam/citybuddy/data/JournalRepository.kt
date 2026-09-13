package com.adam.citybuddy.data

import android.content.Context
import android.content.SharedPreferences

data class JournalEntry(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: Long
)

class JournalRepository(context: Context) {
    // Private storage, inaccessible to other apps or the home screen
    private val prefs: SharedPreferences = context.getSharedPreferences("private_journal", Context.MODE_PRIVATE)

    fun saveEntry(entry: JournalEntry) {
        // Simple string serialization: id|title|content|timestamp
        val serialized = "${entry.id}|||${entry.title}|||${entry.content}|||${entry.timestamp}"
        prefs.edit().putString(entry.id, serialized).apply()
    }

    fun getAllEntries(): List<JournalEntry> {
        val allPrefs = prefs.all
        return allPrefs.values.mapNotNull { value ->
            if (value is String) {
                val parts = value.split("|||")
                if (parts.size == 4) {
                    JournalEntry(parts[0], parts[1], parts[2], parts[3].toLong())
                } else null
            } else null
        }.sortedByDescending { it.timestamp }
    }

    fun getEntry(id: String): JournalEntry? {
        val value = prefs.getString(id, null) ?: return null
        val parts = value.split("|||")
        if (parts.size == 4) {
            return JournalEntry(parts[0], parts[1], parts[2], parts[3].toLong())
        }
        return null
    }
}