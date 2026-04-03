package com.adam.citybuddy.ml

import android.content.Context
import android.content.res.AssetFileDescriptor
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteHelper(context: Context) {
    private var interpreter: Interpreter? = null

    // This vocabulary must match your Python training script exactly
    private val vocab = mapOf(
        "stress" to 1, "study" to 2, "exam" to 3, "sad" to 4,
        "friend" to 5, "help" to 6, "tired" to 7, "sleep" to 8,
        "anxious" to 9, "professional" to 10
    )

    init {
        try {
            val model = loadModelFile(context, "matcher_model.tflite")
            val options = Interpreter.Options()
            // Some newer devices require explicitly setting the number of threads
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

        // IMPORTANT: Close the descriptor to prevent "Could not find package" or memory errors
        fileDescriptor.close()
        return buffer
    }

    private fun tokenize(text: String): FloatArray {
        val inputVector = FloatArray(10) { 0f }
        // Clean the text: remove punctuation so "stress." becomes "stress"
        val cleanText = text.lowercase().replace(Regex("[^a-z\\s]"), "")
        val words = cleanText.split(Regex("\\s+"))

        var count = 0
        for (word in words) {
            if (count >= 10) break
            if (word.isNotBlank()) {
                val id = vocab[word] ?: 0
                inputVector[count] = id.toFloat()
                count++
            }
        }
        return inputVector
    }

    fun predictMatch(userInput: String): String {
        // Double-check if interpreter initialized correctly
        val currInterpreter = interpreter ?: return "AI not ready"

        // Input shape: [1, 10]
        val input = arrayOf(tokenize(userInput))
        // Output shape: [1, 3]
        val output = Array(1) { FloatArray(3) }

        try {
            currInterpreter.run(input, output)

            val scores = output[0]
            // Finding the index with the highest probability score
            val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: 0

            return when (maxIndex) {
                0 -> "Madam Wani (Counselor)"
                1 -> "Aidil (PRS)"
                2 -> "Naqeeb (PRS)"
                else -> "General Support"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Prediction Error: ${e.message}"
        }
    }

    // Call this in your Activity's onDestroy to free up your phone's RAM
    fun close() {
        interpreter?.close()
        interpreter = null
    }
}