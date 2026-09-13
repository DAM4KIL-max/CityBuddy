package com.adam.citybuddy

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class PRMatcher(private val context: Context) {

    private var interpreter: Interpreter? = null
    private val labels = mutableListOf<String>()
    val prList = mutableListOf<PRPersona>()

    init {
        loadLabels()
        loadDataset()
        loadModel()
    }

    private fun loadLabels() {
        context.assets.open("labels.txt").bufferedReader().useLines { lines ->
            lines.forEach { labels.add(it.trim()) }
        }
    }

    private fun loadDataset() {
        val jsonString = context.assets.open("prs_dataset.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<PRPersona>>() {}.type
        val data: List<PRPersona> = Gson().fromJson(jsonString, type)
        prList.addAll(data)
    }

    private fun loadModel() {
        val fileDescriptor = context.assets.openFd("matcher_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val modelBuffer: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

        val options = Interpreter.Options().apply {
            setNumThreads(4)
        }
        interpreter = Interpreter(modelBuffer, options)
    }

    fun match(query: String): Pair<PRPersona?, Float> {
        if (interpreter == null || labels.isEmpty()) return Pair(null, 0f)

        val input = arrayOf(query)
        val output = Array(1) { FloatArray(labels.size) }

        interpreter?.run(input, output)

        val probabilities = output[0]
        var maxIndex = 0
        var maxScore = probabilities[0]

        for (i in 1 until probabilities.size) {
            if (probabilities[i] > maxScore) {
                maxScore = probabilities[i]
                maxIndex = i
            }
        }

        val matchedName = labels[maxIndex]
        val matchedPersona = prList.find { it.name.equals(matchedName, ignoreCase = true) }

        return Pair(matchedPersona, maxScore)
    }

    fun close() {
        interpreter?.close()
    }
}