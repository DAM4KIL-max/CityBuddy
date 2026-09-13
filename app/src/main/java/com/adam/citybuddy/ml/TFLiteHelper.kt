package com.adam.citybuddy.ml

import android.content.Context
import android.content.res.AssetFileDescriptor
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import android.util.Log

class TFLiteHelper(context: Context) {
    private var interpreter: Interpreter? = null

    // Must match your Colab training alphabetical order exactly
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
            Log.d("CITY_BUDDY_AI", "Model loaded successfully!")
        } catch (e: Exception) {
            Log.e("CITY_BUDDY_AI", "Failed to load model: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val buffer = fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            startOffset,
            fileDescriptor.declaredLength
        )
        fileDescriptor.close()
        return buffer
    }

    fun getAllProbabilities(text: String): Map<String, Float> {
        val currInterpreter = interpreter ?: return emptyMap()
        if (text.isBlank()) return emptyMap()

        // Input: Wrap text into a 2D array [1][1] for the TFLite interpreter
        val input = arrayOf(text)

        // Output: Create a 2D float array container to receive the 8 prediction scores [1][8]
        val outputArray = Array(1) { FloatArray(classNames.size) }

        return try {
            // Standard direct run execution for 1 input layer and 1 output layer
            currInterpreter.run(input, outputArray)

            val scores = outputArray[0]

            // FORCE PRINT to Logcat with tag CITY_BUDDY_AI
            Log.d("CITY_BUDDY_AI", "RAW AI OUTPUT: ${scores.joinToString(", ")}")
            Log.d("CITY_BUDDY_AI", "SCORES MAP: ${classNames.zip(scores.toList()).toMap()}")

            classNames.mapIndexed { index, name ->
                name to scores[index]
            }.toMap()
        } catch (e: Exception) {
            Log.e("CITY_BUDDY_AI", "AI PROCESSING ERROR: ${e.message}")
            e.printStackTrace()
            emptyMap()
        }
    }

    fun predictMatch(userInput: String): String {
        val probabilities = getAllProbabilities(userInput)
        return probabilities.maxByOrNull { it.value }?.key ?: "Sir Firdaus"
    }

    fun close() {
        interpreter?.close()
    }
}