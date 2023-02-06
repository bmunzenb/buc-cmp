package com.munzenberger.buc

import java.io.File

typealias CsvRecord = Map<String, String>

fun parse(file: File): List<CsvRecord> {

    val results = mutableListOf<Map<String, String>>()
    val keys = mutableListOf<String>()

    var firstLine = true

    file.forEachLine {
        when (firstLine) {
            true -> {
                firstLine = false
                parseLine(it).let { values -> keys.addAll(values) }
            }
            else -> {
                val map = mutableMapOf<String, String>()
                parseLine(it).forEachIndexed { index, value ->
                    if (index >= keys.size) {
                        throw IllegalStateException("Too many values in record: $it")
                    }
                    map[keys[index]] = value
                }
                results.add(map)
            }
        }
    }

    return results
}

private fun parseLine(line: String): List<String> {

    val values = mutableListOf<String>()

    val value = StringBuffer()
    var inQuotes = false

    line.forEach {

        when {
            !inQuotes && it == ',' -> {
                values.add(value.toString())
                value.setLength(0)
            }
            it == '"' -> {
                inQuotes = !inQuotes
            }
            else -> {
                value.append(it)
            }
        }
    }
    // the value after the last comma
    values.add(value.toString())

    return values
}
