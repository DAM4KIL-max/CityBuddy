package com.adam.citybuddy.ml

import android.content.Context
import android.content.res.AssetFileDescriptor
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteHelper(context: Context) {
    private var interpreter: Interpreter? = null

    // This MUST match the exact alphabetical order the AI learned in Colab
    private val classNames = listOf(
        "Aamily", "Aidil", "Ain", "Azib",
        "Madam Wani", "Naqeeb", "Qaisy", "Sir Firdaus"
    )

    init {
        try {
            val model = loadModelFile(context, "matcher_model.tflite")
            val options = Interpreter.Options()
            options.setNumThreads(4)
            interpreter = Interpreter(model, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        fileDescriptor.close()
        return buffer
    }

    // ── NEW SUPERPOWER: GET ALL SCORES ─────────────────────────────────────────
    // This feeds the raw string to the AI and gets a scorecard for all 8 people.
    fun getAllProbabilities(text: String): Map<String, Float> {
        val currInterpreter = interpreter ?: return emptyMap()

        // 1. Pass the raw string exactly as the user typed it
        val input = arrayOf(text)

        // 2. Expect 8 outputs (one probability score for each person)
        val output = Array(1) { FloatArray(8) }

        return try {
            currInterpreter.run(input, output)
            val scores = output[0]

            // 3. Match the 8 scores to the 8 names to create a map
            // Example: { "Aamily": 0.1f, "Azib": 0.8f, ... }
            classNames.mapIndexed { index, name ->
                name to scores[index]
            }.toMap()

        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    // (Kept so older parts of your app don't break, though SurveyScreen uses getAllProbabilities now)
    fun predictMatch(userInput: String): String {
        val probabilities = getAllProbabilities(userInput)
        // Find the person with the highest score
        return probabilities.maxByOrNull { it.value }?.key ?: "Prediction Error"
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }
}