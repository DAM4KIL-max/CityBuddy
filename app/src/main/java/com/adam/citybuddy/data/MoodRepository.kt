package com.adam.citybuddy.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class DailyMoodRecord(
    val dateString: String, // YYYY-MM-DD
    val moodLabel: String,
    val score: Int
)

object MoodRepository {
    private const val PREF_NAME = "citybuddy_mood_prefs"
    private const val KEY_MOOD_HISTORY = "mood_history_json"
    private const val KEY_LAST_CHECKIN_DATE = "last_checkin_date"
    private const val KEY_STREAK_COUNT = "streak_count"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getMoodScore(label: String): Int {
        return when (label.lowercase()) {
            "great" -> 100
            "okay" -> 75
            "down" -> 50
            "stressed" -> 35
            "frustrated" -> 25
            else -> 50
        }
    }

    /**
     * Saves or updates today's mood entry.
     * Prevents duplicate entries for the same day.
     */
    fun saveMood(context: Context, moodLabel: String) {
        val prefs = getPrefs(context)
        val today = getTodayDateString()
        val score = getMoodScore(moodLabel)

        val history = getAllRecords(context).toMutableList()
        val existingIndex = history.indexOfFirst { it.dateString == today }

        if (existingIndex != -1) {
            history[existingIndex] = DailyMoodRecord(today, moodLabel, score)
        } else {
            history.add(DailyMoodRecord(today, moodLabel, score))
            updateStreak(context, today)
        }

        val jsonArray = JSONArray()
        history.forEach { record ->
            val obj = JSONObject()
            obj.put("date", record.dateString)
            obj.put("label", record.moodLabel)
            obj.put("score", record.score)
            jsonArray.put(obj)
        }

        prefs.edit().putString(KEY_MOOD_HISTORY, jsonArray.toString()).apply()
    }

    fun getAllRecords(context: Context): List<DailyMoodRecord> {
        val prefs = getPrefs(context)
        val jsonStr = prefs.getString(KEY_MOOD_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<DailyMoodRecord>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    DailyMoodRecord(
                        dateString = obj.getString("date"),
                        moodLabel = obj.getString("label"),
                        score = obj.getInt("score")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list.sortedBy { it.dateString }
    }

    fun getTodayMood(context: Context): String? {
        val today = getTodayDateString()
        return getAllRecords(context).find { it.dateString == today }?.moodLabel
    }

    /**
     * Gets records for the past N days ending today.
     */
    fun getRecentRecords(context: Context, days: Int = 7): List<DailyMoodRecord> {
        val all = getAllRecords(context)
        if (all.isEmpty()) return emptyList()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -(days - 1))
        val startDateStr = sdf.format(cal.time)

        return all.filter { it.dateString >= startDateStr }
    }

    fun getAverageScore(context: Context, days: Int = 7): Int {
        val recent = getRecentRecords(context, days)
        if (recent.isEmpty()) return -1
        return recent.map { it.score }.average().toInt()
    }

    /**
     * Calculates real mood trend comparing recent days to the previous period.
     * Returns percentage difference or null if insufficient history exists.
     */
    fun getMoodTrend(context: Context): Int? {
        val all = getAllRecords(context)
        if (all.size < 4) return null // Need at least some historical depth

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()

        // Past 7 days
        val currentPeriodRecords = getRecentRecords(context, 7)
        if (currentPeriodRecords.isEmpty()) return null

        // Previous 7 days (days 8 to 14 ago)
        cal.add(Calendar.DAY_OF_YEAR, -7)
        val endPrevStr = sdf.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, -6)
        val startPrevStr = sdf.format(cal.time)

        val prevPeriodRecords = all.filter { it.dateString in startPrevStr..endPrevStr }
        if (prevPeriodRecords.isEmpty()) return null

        val currentAvg = currentPeriodRecords.map { it.score }.average()
        val prevAvg = prevPeriodRecords.map { it.score }.average()

        return (currentAvg - prevAvg).toInt()
    }

    private fun updateStreak(context: Context, todayStr: String) {
        val prefs = getPrefs(context)
        val lastCheckin = prefs.getString(KEY_LAST_CHECKIN_DATE, "")
        var currentStreak = prefs.getInt(KEY_STREAK_COUNT, 0)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            if (lastCheckin.isNullOrEmpty()) { // 👈 Fixed typo here
                currentStreak = 1
            } else {
                val lastDate = sdf.parse(lastCheckin)
                val todayDate = sdf.parse(todayStr)
                if (lastDate != null && todayDate != null) {
                    val diffDays = ((todayDate.time - lastDate.time) / (1000 * 60 * 60 * 24)).toInt()
                    if (diffDays == 1) {
                        currentStreak += 1
                    } else if (diffDays > 1) {
                        currentStreak = 1 // Reset calmly without shaming
                    }
                }
            }
        } catch (e: Exception) {
            currentStreak = 1
        }

        prefs.edit()
            .putString(KEY_LAST_CHECKIN_DATE, todayStr)
            .putInt(KEY_STREAK_COUNT, currentStreak)
            .apply()
    }

    fun getStreak(context: Context): Int {
        return getPrefs(context).getInt(KEY_STREAK_COUNT, 1)
    }
}